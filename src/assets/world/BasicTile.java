package assets.world;

import org.lwjgl.util.vector.Vector3f;

import renderer.glmodels.GLModel;
import renderer.glshaders.GLShader;
import assets.world.datastructures.DataStructureKey2D;

public class BasicTile extends AbstractTile {

	public BasicTile(DataStructureKey2D key, GLModel model, Vector3f tilePos) {
		super(key, model, tilePos);
	}

	@Override
	public void createModel(GLShader shader) {
		if (this.model != null) {
			throw new RuntimeException("You can only set the model once.");
		}
		this.model = TileFactory.createTileGLModel(BasicTile.class, tilePos, shader);
	}
}
