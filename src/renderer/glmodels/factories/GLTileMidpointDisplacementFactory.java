package renderer.glmodels.factories;

import java.util.ArrayList;
import java.util.List;

import math.types.Vector3;
import math.types.Vector4;
import renderer.glmodels.GLMesh;
import renderer.glprimitives.GLTriangle;
import renderer.glprimitives.GLVertex;
import terraingen.PlasmaFractalFactory;
import assets.world.AbstractTile;
import assets.world.PolygonHeightmapTile;
import assets.world.datastructures.TileDataStructure2D;

public class GLTileMidpointDisplacementFactory implements GLTileFactory {
	/*
	 * TODO: We don't want to keep generating the basic grid, so if we create
	 * that here in the constructor. Then every time we want to create a tile,
	 * we clone the existing grid, generate a new heightmap image, then deform
	 * the default grid according to the heightmap
	 */

	// private float[][] heightMap;
	private List<GLVertex> factoryVertices;

	public List<GLVertex> getFactoryVertices() {
		return factoryVertices;
	}

	public List<GLTriangle> getFactoryTriangles() {
		return factoryTriangles;
	}

	private List<GLTriangle> factoryTriangles;
	private int tileComplexity;
	private TileDataStructure2D tileDataStructure;
	private int rangeMax;

	final static float zOffset = -0.1f;
	final static float zAdjust = 0.005f;

	public GLTileMidpointDisplacementFactory(int tileComplexity, TileDataStructure2D tileDataStructure) {
		this.tileComplexity = tileComplexity;
		this.tileDataStructure = tileDataStructure;
		// this.heightMap = new float[tileComplexity][tileComplexity];
		this.factoryVertices = new ArrayList<GLVertex>(tileComplexity * tileComplexity);
		this.factoryTriangles = new ArrayList<GLTriangle>((tileComplexity - 1) * (tileComplexity - 1) * 2);
		rangeMax = (tileComplexity - 1) * (tileComplexity - 1) * 2;
		generatePlanarMesh();
	}

	public GLMesh create(AbstractTile tile) {
		/*
		 * TODO: Create copy of factoryVertices, and a copy of factoryTriangles.
		 * Currently not doing this, as it will screw up the references between
		 * the triangles and vertices. Needs to be clever. For now, just
		 * creating mesh, and reusing mesh
		 */
		// List<GLVertex> localVertices = new
		// ArrayList<GLVertex>(factoryVertices.size());
		// for (GLVertex vertex : factoryVertices) {
		// localVertices.add((GLVertex) vertex.clone());
		// }

		// Create heightmap
		// resetHeights(this.factoryVertices);
		float[][] heightmap = new float[tileComplexity][tileComplexity];
		PlasmaFractalFactory.create(heightmap);

		if (PolygonHeightmapTile.class.isInstance(tile)) {
			// Save the height-map to the tile for persistence
			// System.out.println("Correct class");
			PolygonHeightmapTile polyTile = (PolygonHeightmapTile) tile;
			polyTile.setHeightmap(heightmap);
		}

		adjustHeightmapToNeighbours(tile, heightmap);

		/*
		 * TODO: This is some weird error checking here: this whole class must
		 * be operating on a PolygonHeightmapTile, otherwise it makes no sense,
		 * but as the factory is an instance of GLTileFactory, I don't know here
		 * that it is.... Better solution?
		 */
		// TODO: get access to the data-structure, then modify the local
		// boundaries to that of existing neighbouring height-map boundaries

		// Adjust mesh according to heightmap
		adjustMeshAccordingToHeightmap(heightmap, this.factoryVertices);

		// Compute normals for new vertexList
		computeNormalsForAllTriangles(this.factoryTriangles, this.factoryVertices);

		int debugValue = 0;
		for (GLVertex v : this.factoryVertices) {
			computeVertexNormal(v, debugValue);
			debugValue++;
		}

		// Create mesh from vertexList and TriangleList
		return new GLMesh(this.factoryTriangles, this.factoryVertices);
	}

	private void generatePlanarMesh() {
		/*
		 * TODO: the size is currently hardcoded. Whilst 1f is likely, currently
		 * we pass the size parameter in the create method above, and it does
		 * nothing
		 */
		float xInc = AbstractTile.SIZE / (float) (tileComplexity - 1);
		float yInc = AbstractTile.SIZE / (float) (tileComplexity - 1);

		float xyOffset = AbstractTile.SIZE / 2f;
		System.out.println(xyOffset);

		int index = 0;

		for (int i = 0; i < tileComplexity * tileComplexity; i++) {
			factoryVertices.add(new GLVertex(i));
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

	private void adjustMeshAccordingToHeightmap(float[][] heightmap, List<GLVertex> vertices) {
		int x = 0, y = 0;
		for (GLVertex v : vertices) {
			// v.xyzw.z(heightmap[x][y] - 0.5f); // was 0.3f
			v.rgba = new Vector4(Math.abs(v.xyzw.z() * 2) + 0.4f, 0.4f, 0.9f, 1f);
			x++;
			if (x == tileComplexity) {
				x = 0;
				y++;
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

	private static void computeNormalsForAllTriangles(List<GLTriangle> triangles, List<GLVertex> vertices) {
		for (GLTriangle triangle : triangles) {
			// addNormalToTriangle(triangle.v0, triangle.v1, triangle.v2);
			computeTriangleFaceNormal(triangle);
		}
	}

	private static void computeTriangleFaceNormal(GLTriangle t) {
		t.normal = Vector3.sub(t.v1.getXYZ(), t.v0.getXYZ()).cross(Vector3.sub(t.v2.getXYZ(), t.v0.getXYZ()));
		t.normal.normalize();

	}

	private static void computeVertexNormal(GLVertex v, int debugValue) {
		Vector3 normal = new Vector3();
		for (GLTriangle t : v.getParentTriangles()) {
			normal.add(t.normal);
		}
		// normal.normalize();
		v.setNXNYNZ(normal);
	}

	private static void addNormalToTriangle(GLVertex v0, GLVertex v1, GLVertex v2) {
		Vector3 vn0 = Vector3.sub(v1.getXYZ(), v0.getXYZ()).cross(Vector3.sub(v2.getXYZ(), v0.getXYZ()));
		vn0.normalize();
		v0.setNXNYNZ(vn0);

		Vector3 vn1 = Vector3.sub(v2.getXYZ(), v1.getXYZ()).cross(Vector3.sub(v0.getXYZ(), v1.getXYZ()));
		vn1.normalize();
		v1.setNXNYNZ(vn1);

		Vector3 vn2 = Vector3.sub(v0.getXYZ(), v2.getXYZ()).cross(Vector3.sub(v1.getXYZ(), v2.getXYZ()));
		vn2.normalize();
		v2.setNXNYNZ(vn2);
	}
}
