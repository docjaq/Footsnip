package assets;

import geometry.GeometryObject;

public class Player extends Character {
	
	private int age;
	private float[] color;
	
	public Player (Position position, GeometryObject geometry, float size, String name, int age, float[] color){
		super(position, geometry, size, name);
		this.age = age;
		this.color = color;
	}

}
