package assets.world;

import java.nio.FloatBuffer;

import renderer.GLPosition;
import renderer.glmodels.GLModel;
import renderer.glmodels.factories.GLTileFactory;
import assets.world.datastructures.DataStructureKey2D;

public class PolygonHeightmapTile extends AbstractTile {

	private float[][] heightmap;
	private FloatBuffer heightmapBuf;
	private int textureLocation = -1;
	private int textureSize;

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

	public int getTextureLocation() {
		return textureLocation;
	}

	public void setTextureLocation(int textureLocation) {
		this.textureLocation = textureLocation;
	}

	public FloatBuffer getHeightmapBuf() {
		return heightmapBuf;
	}

	public void setHeightmapBuf(FloatBuffer heightmapBuf) {
		this.heightmapBuf = heightmapBuf;
	}

	public int getTextureSize() {
		return textureSize;
	}

	public void setTextureSize(int textureSize) {
		this.textureSize = textureSize;
	}
}
