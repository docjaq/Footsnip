package renderer.glmodels;

import java.io.File;

import mesh.Ply;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import renderer.glshaders.GLShader;

public class GLDefaultProjectileFactory implements GLProjectileFactory {

	private Ply mesh;

	public GLDefaultProjectileFactory() {
		Vector4f projectileColor = new Vector4f(0.3f, 0.3f, 0.05f, 1.0f);
		mesh = new Ply();
		mesh.read(new File("resources/meshes/projectile_small.ply"), projectileColor);
	}

	public GLMesh create(Vector3f position, Vector3f angle, float scale, GLShader shader, float[] color) {
		return new GLMesh(mesh.getTriangles(), mesh.getVertices(), position, angle, scale, shader, color);
	}
}
