package assets;

import geometry.GeometryObject;

//This should probably be a class NPC, which Monster then extends, but decided
//to simplify it
public class Monster extends Character {
	
	int level;
	
	//Ambiguous for now, replace this with a class to define a type
	//which can then be procedurally generated
	int type; 

	public Monster(Position position, GeometryObject geometry, float size, String name, int level, int type) {
		super(position, geometry, size, name);
		this.level = level;
		this.type = type;
	}

}
