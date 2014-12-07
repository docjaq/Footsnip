package assets.entities;

import java.io.File;

import math.types.Vector3;
import math.types.Vector4;
import mesh.Ply;
import renderer.GLPosition;
import renderer.glmodels.GLMesh;
import renderer.glmodels.GLModel;

public class AsteroidFactory {

	// private GeometryFile mesh;
	private GLModel model;

	public AsteroidFactory() {
		Vector4 asteroidColor = new Vector4(0.7f, 0.6f, 0.6f, 1.0f);
		Ply mesh = new Ply();
		mesh.read(new File("src/main/resources/meshes/Asteroid.ply"), asteroidColor);

		model = new GLMesh(mesh.getTriangles(), mesh.getVertices());
		model.pushToGPU();
	}

	public Asteroid create(Vector3 position) {
		Vector3 angle = new Vector3((float) Math.random() * 360, (float) Math.random() * 360, (float) Math.random() * 360);
		float scale = (float) (Math.random() * 0.5 + 0.5f);

		GLPosition glPosition = new GLPosition(position, angle, scale, model.getModelRadius());
		glPosition.setEntityRadiusWithModelRadius(model.getModelRadius());

		float mass = 4f;

		Asteroid asteroid = new Asteroid(model, glPosition, mass);

		return asteroid;
	}
}
