package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.util.vector.Vector4f;

import renderer.glprimitives.GLTriangle;
import renderer.glprimitives.GLVertex;

/**
 * NOTES: Not entirely sure how robust this is to different instances of ply
 * files. Also, I wrote it quickly, and is probably not the best way to read a
 * file
 **/

public class Ply extends AbstractGeometryFile {

	private ArrayList<GLVertex> vertices;
	private ArrayList<GLTriangle> triangles;
	private int numVertices;
	private int numFaces;

	public ArrayList<GLTriangle> getTriangles() {
		return triangles;
	}

	public ArrayList<GLVertex> getVertices() {
		return vertices;
	}

	public void read(File file) {

		BufferedReader br;

		try {
			br = new BufferedReader(new FileReader(file));
			readHeader(br);
			readGeometry(br);
		} catch (IOException e) {
		}
	}

	private void readHeader(BufferedReader br) {
		numVertices = 0;
		numFaces = 0;
		try {
			String wholeLine;
			while (br.ready()) {
				wholeLine = br.readLine();
				if (wholeLine.equals("end_header")) {
					break;
				}

				if (wholeLine.startsWith("element")) {
					if (wholeLine.contains("vertex")) {
						numVertices = Integer.parseInt(wholeLine.split(" ")[2]);
						System.out.println("Num vertices = " + numVertices);
					}
					if (wholeLine.contains("face")) {
						numFaces = Integer.parseInt(wholeLine.split(" ")[2]);
						System.out.println("Num faces = " + numFaces);
					}
				}

			}
		} catch (IOException e) {
		}
		vertices = new ArrayList<GLVertex>(numVertices);
		triangles = new ArrayList<GLTriangle>(numFaces);
	}

	private void readGeometry(BufferedReader br) {

		Vector4f rgba = new Vector4f((float) Math.random(), (float) Math.random(), (float) Math.random(), 1.0f);

		try {
			String wholeLine;
			for (int i = 0; i < numVertices; i++) {
				wholeLine = br.readLine();
				String[] elements = wholeLine.split(" ");
				GLVertex vertex = new GLVertex(i, Float.parseFloat(elements[0]), Float.parseFloat(elements[1]),
						Float.parseFloat(elements[2]), rgba);
				if (elements.length >= 6) {
					vertex.setNXNYNZ(Float.parseFloat(elements[3]), Float.parseFloat(elements[4]), Float.parseFloat(elements[5]));
				}
				vertices.add(vertex);
			}

			for (int i = 0; i < numFaces; i++) {
				wholeLine = br.readLine();
				String[] elements = wholeLine.split(" ");
				GLTriangle triangle = new GLTriangle(vertices.get(Integer.parseInt(elements[1])), vertices.get(Integer
						.parseInt(elements[2])), vertices.get(Integer.parseInt(elements[3])));
				triangles.add(triangle);
			}

		} catch (IOException e) {
		}
	}
}
