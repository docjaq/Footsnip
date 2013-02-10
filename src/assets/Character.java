package assets;

import geometry.GeometryObject;

public class Character extends Entity{

	private String name;
	
	public Character(Position position, GeometryObject geometry, float size, String name){
		super(position, geometry, size);
		this.name = name;
	}
}
