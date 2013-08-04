package assets.world;

import org.lwjgl.util.vector.Vector3f;

import renderer.glmodels.GLModel;
import renderer.glmodels.GLTilePlane;
import renderer.glshaders.GLShader;
import assets.world.datastructures.DataStructureKey2D;

public class TileFactory {

	public static BasicTile createTile(DataStructureKey2D key, Class<?> BasicTile, Vector3f tilePos, GLShader shader) {
		Vector3f tileAngle = new Vector3f(0, 0, 0);
		float tileScale = 1f;
		float[] tileColor = { 1.0f, 1.0f, 1.0f, 1.0f };

		GLModel model = new GLTilePlane(tilePos, tileAngle, tileScale, shader, tileColor, AbstractTile.SIZE);
		return new BasicTile(key, model, tilePos);
	}

	public static BasicTile createTileNoGLModel(DataStructureKey2D key, Class<?> BasicTile, Vector3f tilePos, GLShader shader) {
		return new BasicTile(key, null, tilePos);
	}

	public static GLModel createTileGLModel(Class<?> BasicTile, Vector3f tilePos, GLShader shader) {
		Vector3f tileAngle = new Vector3f(0, 0, 0);
		float tileScale = 1f;
		float[] tileColor = { 1.0f, 1.0f, 1.0f, 1.0f };

		return new GLTilePlane(tilePos, tileAngle, tileScale, shader, tileColor, AbstractTile.SIZE);
	}
}
