package pooling;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import math.types.Vector4;
import renderer.glmodels.GLMesh;
import renderer.glprimitives.GLTriangle;
import renderer.glprimitives.GLVertex;
import terraingen.MeshGenerationUtilities;

public class DefaultMeshPool extends MeshPool<GLMesh> {

	public DefaultMeshPool(int minPoolSize, int maxPoolSize, long updateInterval, int tileComplexity) {
		super(minPoolSize, maxPoolSize, updateInterval, tileComplexity);
	}

	@Override
	protected void transformMeshFromHeightmap(GLMesh mesh, float[][] heightmap, boolean debug) {
		// float step = 1f / (float) heightmap.length;
		int numCells = heightmap.length - 1;
		if (debug)
			System.out.println("Size of heightmap is " + numCells + ", " + numCells);
		if (debug)
			System.out.println("Number of vertices is " + mesh.getVertices().size());
		int count = 0;
		if (debug)
			System.out.println("\nArray:");
		for (GLVertex v : mesh.getVertices()) {
			Vector4 pos = v.xyzw;
			// System.out.print("(" + pos.x() + "," + pos.y() + "), ");
			v.xyzw.z(heightmap[(int) ((pos.x() + 0.5f) * numCells)][(int) ((pos.y() + 0.5f) * numCells)]);
			if (debug) {
				// System.out.print("(" + (int) ((pos.x() + 0.5f) * numCells) +
				// "," + (int) ((pos.y() + 0.5f) * numCells) + "), ");
				System.out.print(v.xyzw.z() + " ");
			}
			count++;
		}
		System.out.println();
	}

	protected void transformMeshFromHeightmap(GLMesh mesh, ByteBuffer heightmap) {
		int numCells = (int) Math.sqrt((heightmap.limit() - 1) / 4);
		int count = 0;
		System.out.println("\nBuffer:");
		System.out.print(" ");
		for (GLVertex v : mesh.getVertices()) {
			Vector4 pos = v.xyzw;
			// v.xyzw.z(heightmap.get((int) (pos.x() + 0.5f * numCells) * (int)
			// (pos.y() + 0.5f * numCells)));
			// if (count < 20) {
			// System.out.print(((int) ((pos.x() + 0.5f) * numCells) + (int)
			// ((pos.y() + 0.5f) * numCells) * numCells + ",     "));
			// System.out.print(heightmap.getFloat((int) (pos.x() + 0.5f *
			// numCells) + (int) (pos.y() + 0.5f * numCells)) + " ");
			// }
			int bufferPosition = (int) ((pos.x() + 0.5f) * numCells) + (int) ((pos.y() + 0.5f) * numCells) * (numCells + 1);
			// System.out.print(bufferPosition + ",     ");
			System.out.print(heightmap.getFloat(bufferPosition) + " ");
			count++;
		}
		System.out.println();
	}

	@Override
	protected GLMesh initaliseObject() {

		List<GLVertex> vertices = new ArrayList<GLVertex>(tileComplexity * tileComplexity);
		List<GLTriangle> triangles = new ArrayList<GLTriangle>((tileComplexity - 1) * (tileComplexity - 1) * 2);

		System.out.println("TileComp before genMesh = " + tileComplexity);
		MeshGenerationUtilities.generatePlanarMesh(vertices, triangles, tileComplexity);

		GLMesh model = new GLMesh(triangles, vertices);

		System.out.println("Creating new mesh for pool");

		return model;
	}

}
