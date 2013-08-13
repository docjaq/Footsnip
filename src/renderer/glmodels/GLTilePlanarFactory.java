package renderer.glmodels;

import org.lwjgl.util.vector.Vector3f;

import renderer.glshaders.GLShader;
import assets.world.AbstractTile;

public class GLTilePlanarFactory implements GLTileFactory {

	@Override
	public GLTilePlane create(AbstractTile tile, Vector3f position, Vector3f rotation, float scale, GLShader shader, float[] color,
			float size) {
		return new GLTilePlane(position, rotation, scale, shader, color, size);
	}

}
