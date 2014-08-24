package assets.world;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import renderer.GLPosition;
import renderer.glmodels.GLMesh;
import renderer.glprimitives.GLTriangle;
import renderer.glprimitives.GLVertex;
import terraingen.MapGenerationUtilities;
import terraingen.MeshGenerationUtilities;
import terraingen.simplex.SimplexNoise;
import assets.world.datastructures.DataStructureKey2D;
import assets.world.datastructures.TileDataStructure2D;

public class PolygonHeightmapTileFactory {

	private final static float Z_OFFSET = 0f;
	private final static double WATER_CHANCE = 1;
	private final static int COLOR_MAP_SIZE = 32;

	// private List<GLVertex> factoryVertices;
	// private List<GLTriangle> factoryTriangles;
	// private GLModel openGLReferenceMesh;

	private int tileComplexity;
	private TileDataStructure2D tileDataStructure;

	private SimplexNoise simplexNoise;

	// TODO: As this is only created once, should really only set it once in the
	// shader...
	private FloatBuffer colorMapBuffer;

	public GLMesh getOpenGLReferenceMesh() {
		return openGLReferenceMesh;
	}

	private GLMesh openGLReferenceMesh;

	public PolygonHeightmapTileFactory(int tileComplexity, TileDataStructure2D tileDataStructure) {
		this.tileComplexity = tileComplexity;
		this.tileDataStructure = tileDataStructure;

		// MeshGenerationUtilities.generatePlanarMesh(this.factoryVertices,
		// this.factoryTriangles);

		// Function of number of octaves (noise complexity)
		// Type of terrain (low = flat. High = rocky)
		// Random seed
		simplexNoise = new SimplexNoise(320, 0.5, 5000);

		// adjustMeshToHeightmap(this.factoryVertices,
		// simplexNoise.getSection(tileComplexity, 0, 0));

		List<GLVertex> vertices = new ArrayList<GLVertex>(tileComplexity * tileComplexity);
		List<GLTriangle> triangles = new ArrayList<GLTriangle>((tileComplexity - 1) * (tileComplexity - 1) * 2);
		MeshGenerationUtilities.generatePlanarMesh(vertices, triangles);
		openGLReferenceMesh = new GLMesh(triangles, vertices);
		openGLReferenceMesh.pushToGPU();

		colorMapBuffer = MapGenerationUtilities.generateColorMap(COLOR_MAP_SIZE);

	}

	public AbstractTile create(DataStructureKey2D key, GLPosition position) {

		System.out.println("Creating new tile");

		AbstractTile tile = new PolygonHeightmapTile(key, openGLReferenceMesh, position);
		if (key == null)
			key = new DataStructureKey2D(0, 0);

		float[][] heightmap = simplexNoise.getSection(tileComplexity, key.x, key.y);

		if (PolygonHeightmapTile.class.isInstance(tile)) {
			PolygonHeightmapTile polygonTile = ((PolygonHeightmapTile) tile);

			// Not currently used I think, but maybe if we want to look it up
			// later

			// REMOVED
			polygonTile.setHeightmap(heightmap);

			polygonTile.setHeightmapBuf(convertArrayToBuffer(heightmap));
			polygonTile.setHeightmapSize(tileComplexity);

			polygonTile.setColorMap(colorMapBuffer);
			polygonTile.setColorMapSize(COLOR_MAP_SIZE);

			// Water stuff
			polygonTile.setWater((Math.random() < WATER_CHANCE) ? true : false);
			polygonTile.setWaterHeight((float) ((0.05 - 0.025) + 0.45));
		}

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
				// Scale it like this to make sure the value is always positive
				// Due to texture format (unsigned int)
				// (but over the same range. Invert in shader)
				fBuf.put((array[x][y] + 1f) / 2f);
			}
		}
		fBuf.flip();
		return fBuf;
	}

}
