package assets;

import renderer.glmodels.GLModel;

//This should probably be a class NPC, which Monster then extends, but decided
//to simplify it
public class Monster extends Character {

	private int level;

	// Ambiguous for now, replace this with a class to define a type
	// which can then be procedurally generated
	private int type;

	public Monster(GLModel model, String name, int level, int type) {
		super(model, name);
		this.level = level;
		this.type = type;
	}

	public int getLevel() {
		return level;
	}

	public int getType() {
		return type;
	}
}
