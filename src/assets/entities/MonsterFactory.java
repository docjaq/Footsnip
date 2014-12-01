package assets.entities;

import java.io.File;

import math.types.Vector3;
import math.types.Vector4;
import mesh.Ply;
import renderer.GLPosition;
import renderer.glmodels.GLMesh;
import renderer.glmodels.GLModel;

public class MonsterFactory {

	// private GeometryFile mesh;
	private GLModel model;

	public MonsterFactory() {
		Vector4 color = new Vector4(1.0f, 0.2f, 0.0f, 1.0f);
		Ply mesh = new Ply();
		mesh.read(new File("resources/meshes/SmoothBlob_small.ply"), color);

		model = new GLMesh(mesh.getTriangles(), mesh.getVertices());
		model.pushToGPU();
	}

	public Monster create(Vector3 position) {
		Vector3 angle = new Vector3(0, 0, 0);
		float scale = (float) (0.5);

		GLPosition glPosition = new GLPosition(position, angle, scale, model.getModelRadius());
		glPosition.setEntityRadiusWithModelRadius(model.getModelRadius());

		float mass = 1f;

		Monster monster = new Monster(model, glPosition, mass);

		return monster;
	}
}
