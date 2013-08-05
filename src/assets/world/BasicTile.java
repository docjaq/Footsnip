package assets.world;

import org.lwjgl.util.vector.Vector3f;

import renderer.glmodels.GLModel;
import renderer.glmodels.GLTileFactory;
import renderer.glshaders.GLShader;
import assets.world.datastructures.DataStructureKey2D;

public class BasicTile extends AbstractTile {

	public BasicTile(DataStructureKey2D key, GLModel model, Vector3f tilePos) {
		super(key, model, tilePos);
	}

	@Override
	public void createModel(GLTileFactory glTileFactory, GLShader shader) {
		if (this.model != null) {
			throw new RuntimeException("You can only set the model once.");
		}
		Vector3f tileAngle = new Vector3f(0, 0, 0);
		float tileScale = 1f;
		float[] tileColor = { 1.0f, 1.0f, 1.0f, 1.0f };

		this.model = glTileFactory.create(tilePos, tileAngle, tileScale, shader, tileColor, AbstractTile.SIZE);

		// new GLTilePlane(tilePos, tileAngle, tileScale, shader, tileColor,
		// AbstractTile.SIZE);
	}
}
