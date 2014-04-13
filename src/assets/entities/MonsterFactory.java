package assets.entities;

import math.types.Vector3;
import mesh.GeometryFile;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import renderer.GLPosition;
import renderer.glmodels.GLMesh;
import renderer.glmodels.GLModel;

public class MonsterFactory {

	private GeometryFile mesh;
	private GLModel model;

	public MonsterFactory(GeometryFile mesh) {
		this.mesh = mesh;
		model = new GLMesh(mesh.getTriangles(), mesh.getVertices());
	}

	public Monster create(Vector3 monsterPos, String scriptLocation) {

		// scriptGlobal is apparently a table (presumably of functions)
		Globals scriptGlobal = JsePlatform.standardGlobals();

		// These return chunks are references to particular scripts
		LuaValue chunkMonster = scriptGlobal.loadfile(scriptLocation);
		LuaValue chunkTest = scriptGlobal.loadfile("resources/lua/test.lua");

		LuaTable globalTable = scriptGlobal.checktable();
		System.err.println(globalTable.tojstring());

		// Call binds the specific chunk. Such that you can only access
		// functions from the global that have been bound
		chunkMonster.call();
		chunkTest.call();
		chunkMonster.call();

		// This is some debug that gets the script name of the chunk. Though
		// weirdly it calls it a function...
		LuaFunction monsterScriptLocation = chunkMonster.checkfunction();
		System.err.println(monsterScriptLocation.toString());

		LuaValue getRotationDelta = scriptGlobal.get("getRotationDelta");
		float rotationDelta = getRotationDelta.call(LuaValue.valueOf((int) (Math.random() * 100))).tofloat();
		// System.out.println("Rotation delta = " + rotationDelta);

		LuaValue getScale = scriptGlobal.get("getScale");
		float luaScale = getScale.call(LuaValue.valueOf(2)).tofloat();
		System.out.println("scale = " + luaScale);

		Vector3 monsterAngle = new Vector3(0, 0, 0);
		float monsterScale = (float) (Math.random() * 2f);

		GLPosition position = new GLPosition(monsterPos, monsterAngle, monsterScale, model.getModelRadius());
		position.setEntityRadiusWithModelRadius(model.getModelRadius());

		Monster monster = new Monster(model, position);
		monster.setRotationDelta(rotationDelta);

		return monster;
	}
}
