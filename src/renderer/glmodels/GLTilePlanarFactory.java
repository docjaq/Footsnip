package renderer.glmodels;

import org.lwjgl.util.vector.Vector3f;

import renderer.glshaders.GLShader;

public class GLTilePlanarFactory implements GLTileFactory {

	@Override
	public GLTilePlane create(Vector3f position, Vector3f rotation, float scale, GLShader shader, float[] color, float size) {
		return new GLTilePlane(position, rotation, scale, shader, color, size);
	}

}
