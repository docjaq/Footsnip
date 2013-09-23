package mesh;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Scanner;

import org.lwjgl.util.vector.Vector4f;

import renderer.glprimitives.GLTriangle;
import renderer.glprimitives.GLVertex;
import exception.GameException;

/**
 * NOTES: Not entirely sure how robust this is to different instances of ply
 * files. Also, I wrote it quickly, and is probably not the best way to read a
 * file
 **/

public class Ply extends AbstractGeometryFile {

	private int numVertices;
	private int numFaces;

	public Ply(ArrayList<GLTriangle> triangles, ArrayList<GLVertex> vertices) {
		super(triangles, vertices);
	}

	public Ply() {

	}

	public ArrayList<GLTriangle> getTriangles() {
		return triangles;
	}

	public ArrayList<GLVertex> getVertices() {
		return vertices;
	}

	public void read(File file, Vector4f color) {

		try {
			Scanner scanner = new Scanner(new BufferedReader(new FileReader(file)));
			readHeader(scanner);
			readGeometry(scanner, color);
		} catch (IOException e) {
			throw new GameException(MessageFormat.format("Error reading file {0}; error message is: {1}", file.getPath(),
					e.getMessage() == null ? e.toString() : e.getMessage()), e);
		}
	}

	private void readHeader(Scanner scanner) {
		numVertices = 0;
		numFaces = 0;
		while (scanner.hasNext()) {
			String line = scanner.nextLine();
			if (line.equals("end_header")) {
				break;
			}

			if (line.startsWith("element")) {
				if (line.contains("vertex")) {
					numVertices = Integer.parseInt(line.split(" ")[2]);
					System.out.println("Num vertices = " + numVertices);
				}
				if (line.contains("face")) {
					numFaces = Integer.parseInt(line.split(" ")[2]);
					System.out.println("Num faces = " + numFaces);
				}
			}
		}
		vertices = new ArrayList<GLVertex>(numVertices);
		triangles = new ArrayList<GLTriangle>(numFaces);
	}

	private void readGeometry(Scanner scanner, Vector4f color) {

		for (int i = 0; i < numVertices; i++) {
			String line = scanner.nextLine();
			String[] elements = line.split(" ");
			GLVertex vertex = new GLVertex(i, Float.parseFloat(elements[0]), Float.parseFloat(elements[1]), Float.parseFloat(elements[2]),
					color);
			if (elements.length >= 6) {
				vertex.setNXNYNZ(Float.parseFloat(elements[3]), Float.parseFloat(elements[4]), Float.parseFloat(elements[5]));
			}
			vertices.add(vertex);
		}

		for (int i = 0; i < numFaces; i++) {
			String line = scanner.nextLine();
			String[] elements = line.split(" ");
			GLTriangle triangle = new GLTriangle(vertices.get(Integer.parseInt(elements[1])), vertices.get(Integer.parseInt(elements[2])),
					vertices.get(Integer.parseInt(elements[3])));
			triangles.add(triangle);
		}
	}

	@Override
	public void write(File file) {

		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
			out.write("ply\nformat ascii 1.0\ncomment VCGLIB generated\nelement vertex " + vertices.size() + "\nproperty float x\n"
					+ "property float y\nproperty float z\nproperty float nx\nproperty float ny\nproperty float nz\n" + "element face "
					+ triangles.size() + "\nproperty list uchar int vertex_indices\nend_header\n");

			out.newLine();

			for (GLVertex v : vertices) {
				out.write(Float.toString(v.xyzw.x) + " " + Float.toString(v.xyzw.y) + " " + Float.toString(v.xyzw.z) + " "
						+ Float.toString(v.nxnynznw.x) + " " + Float.toString(v.nxnynznw.y) + " " + Float.toString(v.nxnynznw.z));
				out.newLine();
			}

			for (GLTriangle t : triangles) {

				out.write(Integer.toString(3) + " " + Integer.toString(t.v0.index) + " " + Integer.toString(t.v1.index) + " "
						+ Integer.toString(t.v2.index));
				out.newLine();
			}

			out.close();
		} catch (IOException e) {
		}

	}

	@Override
	public void setTriangles(ArrayList<GLTriangle> triangles) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setVertices(ArrayList<GLVertex> vertices) {
		// TODO Auto-generated method stub

	}
}
