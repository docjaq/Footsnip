package renderer.glmodels;

import java.io.File;

import maths.types.Vector3;
import maths.types.Vector4;
import mesh.Ply;

public class GLDefaultProjectileFactory implements GLProjectileFactory {

	private Ply mesh;

	public GLDefaultProjectileFactory() {
		Vector4 projectileColor = new Vector4(0.8f, 0.9f, 1.0f, 1.0f);
		mesh = new Ply();
		mesh.read(new File("resources/meshes/projectile_small.ply"), projectileColor);
	}

	public GLMesh create(Vector3 position, Vector3 angle, float scale, float[] color) {
		return new GLMesh(mesh.getTriangles(), mesh.getVertices(), position, angle, scale);
	}
}
