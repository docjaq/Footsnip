package assets.world;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import math.types.Vector3;
import math.types.Vector4;
import renderer.GLPosition;
import renderer.glmodels.GLMesh;
import renderer.glmodels.GLModel;
import renderer.glprimitives.GLTriangle;
import renderer.glprimitives.GLVertex;
import terraingeneration.PlasmaFractalFactory;
import assets.world.datastructures.DataStructureKey2D;
import assets.world.datastructures.TileDataStructure2D;

public class PolygonHeightmapTileFactory {

	private List<GLVertex> factoryVertices;
	private List<GLTriangle> factoryTriangles;
	private GLModel model;

	private int tileComplexity;
	private TileDataStructure2D tileDataStructure;

	private final static float zOffset = -0.1f;

	public PolygonHeightmapTileFactory(int tileComplexity, TileDataStructure2D tileDataStructure) {
		this.tileComplexity = tileComplexity;
		this.tileDataStructure = tileDataStructure;
		this.factoryVertices = new ArrayList<GLVertex>(tileComplexity * tileComplexity);
		this.factoryTriangles = new ArrayList<GLTriangle>((tileComplexity - 1) * (tileComplexity - 1) * 2);

		generatePlanarMesh();

		model = new GLMesh(this.factoryTriangles, this.factoryVertices);
	}

	public AbstractTile create(DataStructureKey2D key, GLPosition position) {

		AbstractTile tile = new PolygonHeightmapTile(key, model, position);

		float[][] heightmap = new float[tileComplexity][tileComplexity];
		PlasmaFractalFactory.create(heightmap);
		FloatBuffer buf = generateNormalMap(heightmap);

		if (PolygonHeightmapTile.class.isInstance(tile)) {
			PolygonHeightmapTile polygonTile = ((PolygonHeightmapTile) tile);

			polygonTile.setHeightmap(heightmap);
			polygonTile.setHeightmapBuf(convertArrayToBuffer(heightmap));
			polygonTile.setHeightmapSize(tileComplexity);

			polygonTile.setNormalmapBuf(buf);
			polygonTile.setNormalmapSize(tileComplexity);
		}
		adjustHeightmapToNeighbours(tile, heightmap);

		return tile;
	}

	private FloatBuffer convertArrayToBuffer(float[][] array) {
		int tWidth = array.length;
		int tHeight = array.length;

		// Change this to
		// http://stackoverflow.com/questions/7070576/get-one-dimensionial-array-from-a-mutlidimensional-array-in-java
		// Float is four bytes
		ByteBuffer buf = ByteBuffer.allocateDirect(tWidth * tHeight * 4);
		buf.order(ByteOrder.nativeOrder());

		FloatBuffer fBuf = buf.asFloatBuffer();
		for (int y = 0; y < tHeight; y++) {
			for (int x = 0; x < tWidth; x++) {
				fBuf.put((array[x][y] + 1) / 2f);
			}
		}
		fBuf.flip();
		return fBuf;
	}

	private void generatePlanarMesh() {

		int localTileComplexity = 33;

		float xInc = AbstractTile.SIZE / (float) (localTileComplexity - 1);
		float yInc = AbstractTile.SIZE / (float) (localTileComplexity - 1);

		float xyOffset = AbstractTile.SIZE / 2f;

		int index = 0;

		for (int i = 0; i < localTileComplexity * localTileComplexity; i++) {
			GLVertex vertex = new GLVertex(i);
			vertex.rgba = new Vector4(0.4f, 0.4f, 0.9f, 1f);
			vertex.nxnynznw = new Vector4(0, 0, 1, 1);
			factoryVertices.add(vertex);

		}

		for (int i = 0; i < localTileComplexity; i++) {
			factoryVertices.get(index).setXYZ((float) (i * xInc - xyOffset), -xyOffset, zOffset);
			index++;
		}

		int numItems = index;
		System.out.println("NumItems: " + numItems);

		for (int i = 1; i < localTileComplexity; i++) {
			int count = 0;
			for (int j = 0; j < localTileComplexity; j++) {
				factoryVertices.get(index).setXYZ((float) (j * xInc - xyOffset), (float) (i * yInc - xyOffset), zOffset);
				addTriangles(index, count, numItems);
				index++;
				count++;
			}
		}
	}

	private void adjustHeightmapToNeighbours(AbstractTile tile, float[][] heightmap) {
		// For each neighbour
		// 1. Check if it's not null
		// 2. loop through all the elements on the adjacent side, and set the
		// current
		// tile to have those heights

		// TODO: So much messy code here, I hate it. Quick implementation to
		// test it. Rework later.
		PolygonHeightmapTile neighRight = (PolygonHeightmapTile) tileDataStructure.getTileRight(tile);
		if (neighRight != null) {
			float[][] neighHeightmap = neighRight.getHeightmap();
			if (neighHeightmap != null) {
				for (int i = 0; i < tileComplexity; i++) {
					heightmap[tileComplexity - 1][i] = neighHeightmap[0][i];
				}
			}
		}

		PolygonHeightmapTile neighLeft = (PolygonHeightmapTile) tileDataStructure.getTileLeft(tile);
		if (neighLeft != null) {
			float[][] neighHeightmap = neighLeft.getHeightmap();
			if (neighHeightmap != null) {
				for (int i = 0; i < tileComplexity; i++) {
					heightmap[0][i] = neighHeightmap[tileComplexity - 1][i];
				}
			}
		}

		PolygonHeightmapTile neighTop = (PolygonHeightmapTile) tileDataStructure.getTileTop(tile);
		if (neighTop != null) {
			float[][] neighHeightmap = neighTop.getHeightmap();
			if (neighHeightmap != null) {
				for (int i = 0; i < tileComplexity; i++) {
					heightmap[i][tileComplexity - 1] = neighHeightmap[i][0];
				}
			}
		}

		PolygonHeightmapTile neighBot = (PolygonHeightmapTile) tileDataStructure.getTileBottom(tile);
		if (neighBot != null) {
			float[][] neighHeightmap = neighBot.getHeightmap();
			if (neighHeightmap != null) {
				for (int i = 0; i < tileComplexity; i++) {
					heightmap[i][0] = neighHeightmap[i][tileComplexity - 1];
				}
			}
		}
	}

	private FloatBuffer generateNormalMap(float[][] data) {
		ByteBuffer buf = ByteBuffer.allocateDirect(data.length * data.length * 4 * 3);
		buf.order(ByteOrder.nativeOrder());

		int half = data.length / 2;

		FloatBuffer fBuf = buf.asFloatBuffer();
		for (int y = 0; y < data.length; y++) {
			for (int x = 0; x < data.length; x++) {
				/*
				 * float[] normal; if (x < half && y < half) normal = new
				 * float[] { 1, 0, 0 };// else if (x > half && y < half) normal
				 * = new float[] { 0, 1, 0 }; else if (x < half && y > half)
				 * normal = new float[] { 0, 0, 1 }; else if (x > half && y >
				 * half) normal = new float[] { 1, 1, 0 }; else normal = new
				 * float[] { 0, 0, 0 };
				 * 
				 * if (x < 1 || y < 1 || x > data.length - 2 || y > data.length
				 * - 2) { normal = new float[] { 0, 0, 0 }; }
				 */

				float[] normal = calculateNormal(data, x, y);
				normal[0] = (normal[0] + 1) / 2f;
				normal[1] = (normal[1] + 1) / 2f;
				normal[2] = (normal[2] + 1) / 2f;
				// float[] normal = calculateNormal(data, x, y);
				// float[] normal = new float[] { (float) Math.random(), (float)
				// Math.random(), (float) Math.random() };
				fBuf.put(normal);
			}
		}
		fBuf.flip();

		return fBuf;
	}

	private float[] calculateNormal(float[][] data, int u, int v) {
		Vector3 normal;

		float strength = 3f;
		if (u > 0 && v > 0 && u < data.length - 1 && v < data.length - 1) {

			float tl = Math.abs(data[u - 1][v - 1]);
			float l = Math.abs(data[u - 1][v]);
			float bl = Math.abs(data[u - 1][v + 1]);
			float b = Math.abs(data[u][v + 1]);
			float br = Math.abs(data[u + 1][v + 1]);
			float r = Math.abs(data[u + 1][v]);
			float tr = Math.abs(data[u + 1][v - 1]);
			float t = Math.abs(data[u][v - 1]);

			// Compute dx using Sobel:
			// -1 0 1
			// -2 0 2
			// -1 0 1
			float dX = tr + 2 * r + br - tl - 2 * l - bl;

			// Compute dy using Sobel:
			// -1 -2 -1
			// 0 0 0
			// 1 2 1
			float dY = bl + 2 * b + br - tl - 2 * t - tr;

			normal = new Vector3(dX, dY, 1.0f / strength);
			normal.normalize();
		} else {
			normal = new Vector3(0, 0, 1);
		}

		// convert (-1.0 , 1.0) to (0.0 , 1.0), if necessary
		// Vector3 scale = new Vector3(0.5f, 0.5f, 0.5f);
		// Vector3.Multiply(ref N, ref scale, out N);
		// Vector3.Add(ref N, ref scale, out N);

		return new float[] { normal.x(), normal.y(), normal.z() };
	}

	private void addTriangles(int index, int count, int numItems) {
		if (count == 0) {
		} else {
			addTriangle(factoryVertices.get(index), factoryVertices.get(index - 1), factoryVertices.get(index - numItems - 1));
			addTriangle(factoryVertices.get(index), factoryVertices.get(index - numItems - 1), factoryVertices.get(index - numItems));
		}
	}

	private void addTriangle(GLVertex v0, GLVertex v1, GLVertex v2) {
		GLTriangle t0 = new GLTriangle(v0, v1, v2);
		factoryTriangles.add(t0);
		v0.addParentTriangle(t0);
		v1.addParentTriangle(t0);
		v2.addParentTriangle(t0);

	}
}
