package assets.entities;

import math.types.Vector3;
import mesh.GeometryFile;
import renderer.GLPosition;
import renderer.glmodels.GLMesh;
import renderer.glmodels.GLModel;

public class AsteroidFactory {

	// private GeometryFile mesh;
	private GLModel model;

	public AsteroidFactory(GeometryFile mesh) {
		// this.mesh = mesh;
		model = new GLMesh(mesh.getTriangles(), mesh.getVertices());
		model.pushToGPU();
	}

	public Asteroid create(Vector3 position, float rotationDelta) {

		Vector3 angle = new Vector3(0, 0, 0);
		float scale = (float) (Math.random() * 0.5 + 0.5f);

		/**
		 * TODO: Currently the color is actually set per vertex of the mesh when
		 * the mesh GeometryFile is generated, and so asteroids initiated from
		 * the same GeometryFile share the same per vertex coloring. Currently
		 * that could be changed by modifying the GeometryFile for every
		 * creation, but for now it's fixed, and this color array does nothing
		 **/
		GLPosition glPosition = new GLPosition(position, angle, scale, model.getModelRadius());
		glPosition.setEntityRadiusWithModelRadius(model.getModelRadius());

		float mass = 4f;

		Asteroid asteroid = new Asteroid(model, glPosition, mass);

		return asteroid;
	}
}
