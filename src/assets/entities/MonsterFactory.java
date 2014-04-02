package assets.entities;

import math.types.Vector3;
import mesh.GeometryFile;
import renderer.GLPosition;
import renderer.glmodels.GLMesh;
import renderer.glmodels.GLModel;
import renderer.glshaders.GLShader;

public class MonsterFactory {

	private GeometryFile mesh;
	private GLModel model;

	public MonsterFactory(GeometryFile mesh) {
		this.mesh = mesh;
		model = new GLMesh(mesh.getTriangles(), mesh.getVertices());
	}

	public Monster create(Vector3 monsterPos, float rotationDelta) {

		Vector3 monsterAngle = new Vector3(0, 0, 0);
		float monsterScale = (float) (Math.random() * 2f);

		/**
		 * TODO: Currently the color is actually set per vertex of the mesh when
		 * the mesh GeometryFile is generated, and so monsters initiated from
		 * the same GeometryFile share the same per vertex coloring. Currently
		 * that could be changed by modifying the GeometryFile for every
		 * creation, but for now it's fixed, and this color array does nothing
		 **/
		GLPosition position = new GLPosition(monsterPos, monsterAngle, monsterScale, model.getModelRadius());
		position.setEntityRadiusWithModelRadius(model.getModelRadius());

		Monster monster = new Monster(model, position, "Monster_", 0);
		monster.setRotationDelta(rotationDelta);

		return monster;
	}
}
