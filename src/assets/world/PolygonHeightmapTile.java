package assets.world;

import org.lwjgl.util.vector.Vector3f;

import renderer.glmodels.GLModel;
import renderer.glmodels.GLTileFactory;
import assets.world.datastructures.DataStructureKey2D;

public class PolygonHeightmapTile extends AbstractTile {

	private float[][] heightmap;

	public float[][] getHeightmap() {
		return heightmap;
	}

	public void setHeightmap(float[][] heightmap) {
		this.heightmap = heightmap;
	}

	public PolygonHeightmapTile(DataStructureKey2D key, GLModel model, Vector3f tilePos) {
		super(key, model, tilePos);
	}

	@Override
	public void createModel(GLTileFactory glTileFactory) {
		if (this.model != null) {
			throw new RuntimeException("You can only set the model once.");
		}
		Vector3f tileAngle = new Vector3f(0, 0, 0);
		float tileScale = 1f;
		float[] tileColor = { 1.0f, 1.0f, 1.0f, 1.0f };

		this.model = glTileFactory.create(this, tilePos, tileAngle, tileScale, tileColor, AbstractTile.SIZE);

	}
}
