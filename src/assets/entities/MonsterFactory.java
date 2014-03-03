package assets.entities;

import maths.types.Vector3;
import mesh.GeometryFile;
import renderer.glmodels.GLMesh;
import renderer.glmodels.GLModel;
import renderer.glshaders.GLShader;

public class MonsterFactory {

	public static Monster create(GeometryFile mesh, GLShader shader, Vector3 monsterPos, float rotationDelta) {

		Vector3 monsterAngle = new Vector3(0, 0, 0);
		float monsterScale = (float) (Math.random() * 2f);

		/**
		 * TODO: Currently the color is actually set per vertex of the mesh when
		 * the mesh GeometryFile is generated, and so monsters initiated from
		 * the same GeometryFile share the same per vertex coloring. Currently
		 * that could be changed by modifying the GeometryFile for every
		 * creation, but for now it's fixed, and this color array does nothing
		 **/
		// float[] monsterColor = { (float) Math.random(), (float)
		// Math.random(), (float) Math.random(), (float) 1 };

		GLModel monsterModel = new GLMesh(mesh.getTriangles(), mesh.getVertices(), monsterPos, monsterAngle, monsterScale);
		Monster monster = new Monster(monsterModel, "Monster_", 0);
		monster.setRotationDelta(rotationDelta);

		return monster;
	}
}
