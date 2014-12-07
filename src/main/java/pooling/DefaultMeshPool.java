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
	protected void transformMeshFromHeightmap(GLMesh mesh, float[][] heightmap) {
		int numCells = heightmap.length - 1;
		for (GLVertex v : mesh.getVertices()) {
			Vector4 pos = v.xyzw;
			v.xyzw.z(heightmap[(int) ((pos.x() + 0.5f) * numCells)][(int) ((pos.y() + 0.5f) * numCells)]);
		}
		System.out.println();
	}

	protected void transformMeshFromHeightmap(GLMesh mesh, ByteBuffer heightmap) {
		int numCells = (int) Math.sqrt((heightmap.limit() - 1) / 4);
		for (GLVertex v : mesh.getVertices()) {
			Vector4 pos = v.xyzw;
			int bufferPosition = (int) ((pos.x() + 0.5f) * numCells) + ((int) ((pos.y() + 0.5f) * numCells) * (numCells + 1));
			pos.z(heightmap.getFloat(bufferPosition * 4) * 2f - 1f);
		}
		heightmap.rewind();
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
