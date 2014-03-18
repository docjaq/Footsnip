package assets.world;

import java.nio.FloatBuffer;

import renderer.GLPosition;
import renderer.glmodels.GLModel;
import renderer.glmodels.factories.GLTileFactory;
import assets.world.datastructures.DataStructureKey2D;

public class PolygonHeightmapTile extends AbstractTile {

	private float[][] heightmap;
	private FloatBuffer heightmapBuf;
	private int heightmapLocation = -1;
	private int heightmapSize;

	private FloatBuffer normalmapBuf;
	private int normalmapLocation = -1;
	private int normalmapSize;

	public float[][] getHeightmap() {
		return heightmap;
	}

	public void setHeightmap(float[][] heightmap) {
		this.heightmap = heightmap;
	}

	public PolygonHeightmapTile(DataStructureKey2D key, GLModel model, GLPosition position) {
		super(key, model, position);
	}

	@Override
	public void createModel(GLTileFactory glTileFactory) {
		if (this.model != null) {
			throw new RuntimeException("You can only set the model once.");
		}
		this.model = glTileFactory.create(this);
	}

	public int getHeightmapLocation() {
		return heightmapLocation;
	}

	public void setHeightmapLocation(int textureLocation) {
		this.heightmapLocation = textureLocation;
	}

	public FloatBuffer getHeightmapBuf() {
		return heightmapBuf;
	}

	public void setHeightmapBuf(FloatBuffer heightmapBuf) {
		this.heightmapBuf = heightmapBuf;
	}

	public int getHeightmapSize() {
		return heightmapSize;
	}

	public void setHeightmapSize(int heightmapSize) {
		this.heightmapSize = heightmapSize;
	}

	public FloatBuffer getNormalmapBuf() {
		return normalmapBuf;
	}

	public void setNormalmapBuf(FloatBuffer normalmapBuf) {
		this.normalmapBuf = normalmapBuf;
	}

	public int getNormalmapLocation() {
		return normalmapLocation;
	}

	public void setNormalmapLocation(int normalmapLocation) {
		this.normalmapLocation = normalmapLocation;
	}

	public int getNormalmapSize() {
		return normalmapSize;
	}

	public void setNormalmapSize(int normalmapSize) {
		this.normalmapSize = normalmapSize;
	}
}
