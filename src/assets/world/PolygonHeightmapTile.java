package assets.world;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import renderer.GLPosition;
import renderer.glmodels.GLModel;
import renderer.glmodels.factories.GLTileFactory;
import assets.world.datastructures.DataStructureKey2D;

public class PolygonHeightmapTile extends AbstractTile {

	// private float[][] heightmap;
	private ByteBuffer heightmapBuf;
	private int heightmapLocation = -1;
	private int heightmapSize;

	// private FloatBuffer normalmapBuf;
	// private int normalmapLocation = -1;
	// private int normalmapSize;
	private int colorMapLocation = -1;
	private FloatBuffer colorMapBuffer;
	private int colorMapSize;

	private boolean water;
	private float waterHeight;

	// public float[][] getHeightmap() {
	// return heightmap;
	// }

	// public void setHeightmap(float[][] heightmap) {
	// this.heightmap = heightmap;
	// }

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

	public ByteBuffer getHeightmapBuf() {
		return heightmapBuf;
	}

	public void setHeightmapBuf(ByteBuffer heightmapBuf) {
		this.heightmapBuf = heightmapBuf;
	}

	public int getHeightmapSize() {
		return heightmapSize;
	}

	public void setHeightmapSize(int heightmapSize) {
		this.heightmapSize = heightmapSize;
	}

	public boolean isWater() {
		return water;
	}

	public void setWater(boolean water) {
		this.water = water;
	}

	public float getWaterHeight() {
		return waterHeight;
	}

	public void setWaterHeight(float waterHeight) {
		this.waterHeight = waterHeight;
	}

	public int getColorMapLocation() {
		return colorMapLocation;
	}

	public void setColorMapLocation(int colorMapLocation) {
		this.colorMapLocation = colorMapLocation;
	}

	public FloatBuffer getColorMap() {
		return colorMapBuffer;
	}

	public void setColorMap(FloatBuffer colorMapBuffer) {
		this.colorMapBuffer = colorMapBuffer;
	}

	public int getColorMapSize() {
		return colorMapSize;
	}

	public void setColorMapSize(int colorMapSize) {
		this.colorMapSize = colorMapSize;
	}

	// public FloatBuffer getNormalmapBuf() {
	// return normalmapBuf;
	// }
	//
	// public void setNormalmapBuf(FloatBuffer normalmapBuf) {
	// this.normalmapBuf = normalmapBuf;
	// }
	//
	// public int getNormalmapLocation() {
	// return normalmapLocation;
	// }
	//
	// public void setNormalmapLocation(int normalmapLocation) {
	// this.normalmapLocation = normalmapLocation;
	// }
	//
	// public int getNormalmapSize() {
	// return normalmapSize;
	// }
	//
	// public void setNormalmapSize(int normalmapSize) {
	// this.normalmapSize = normalmapSize;
	// }

}
