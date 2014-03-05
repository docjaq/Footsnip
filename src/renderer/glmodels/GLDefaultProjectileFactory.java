package renderer.glmodels;

import java.io.File;

import math.types.Vector4;
import mesh.Ply;

public class GLDefaultProjectileFactory implements GLProjectileFactory {

	private static GLDefaultProjectileFactory instance;

	private Ply mesh;
	private GLModel model;

	private GLDefaultProjectileFactory() {
		Vector4 projectileColor = new Vector4(0.8f, 0.9f, 1.0f, 1.0f);
		mesh = new Ply();
		mesh.read(new File("resources/meshes/projectile_small.ply"), projectileColor);
		this.model = new GLMesh(mesh.getTriangles(), mesh.getVertices());
	}

	public synchronized static GLDefaultProjectileFactory getInstance() {
		if (instance == null) {
			instance = new GLDefaultProjectileFactory();
		}

		return instance;
	}

	public GLModel create() {
		return model;
	}
}
