package assets.world;

import org.lwjgl.util.vector.Vector3f;

import renderer.glmodels.GLModel;
import renderer.glmodels.GLTilePlane;

public class TileFactory {

	public static BasicTile createTile(BasicTile tile, Vector3f tilePos) {
		Vector3f tileAngle = new Vector3f(0, 0, 0);
		float tileScale = 1f;
		float[] tileColor = { 1.0f, 1.0f, 1.0f, 1.0f };
		float size = tile.getSize();
		GLModel model = new GLTilePlane(tilePos, tileAngle, tileScale, tile.getModel().getShader(), tileColor, size);
		return new BasicTile(model, size);

	}
}
