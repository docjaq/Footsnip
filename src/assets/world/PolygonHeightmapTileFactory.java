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

		if (PolygonHeightmapTile.class.isInstance(tile)) {
			PolygonHeightmapTile polygonTile = ((PolygonHeightmapTile) tile);
			polygonTile.setHeightmap(heightmap);
			polygonTile.setHeightmapBuf(convertArrayToBuffer(heightmap));
			polygonTile.setTextureSize(tileComplexity);
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
				fBuf.put(array[x][y]);
			}
		}
		fBuf.flip();
		return fBuf;
	}

	private void generatePlanarMesh() {

		float xInc = AbstractTile.SIZE / (float) (tileComplexity - 1);
		float yInc = AbstractTile.SIZE / (float) (tileComplexity - 1);

		float xyOffset = AbstractTile.SIZE / 2f;

		int index = 0;

		for (int i = 0; i < tileComplexity * tileComplexity; i++) {
			GLVertex vertex = new GLVertex(i);
			vertex.rgba = new Vector4(0.4f, 0.4f, 0.9f, 1f);
			vertex.nxnynznw = new Vector4(0, 0, 1, 1);
			factoryVertices.add(vertex);

		}

		for (int i = 0; i < tileComplexity; i++) {
			factoryVertices.get(index).setXYZ((float) (i * xInc - xyOffset), -xyOffset, zOffset);
			index++;
		}

		int numItems = index;
		System.out.println("NumItems: " + numItems);

		for (int i = 1; i < tileComplexity; i++) {
			int count = 0;
			for (int j = 0; j < tileComplexity; j++) {
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

		FloatBuffer fBuf = buf.asFloatBuffer();
		for (int y = 0; y < data.length; y++) {
			for (int x = 0; x < data.length; x++) {
				fBuf.put(calculateNormal(data, x, y));
			}
		}

		return fBuf;
	}

	private float[] calculateNormal(float[][] data, int u, int v) {
		// Value from trial & error.
		// Seems to work fine for the scales we are dealing with.
		float strength = 1 / 16;

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

		Vector3 normal = new Vector3(dX, dY, 1.0f / strength);
		normal.normalize();

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
