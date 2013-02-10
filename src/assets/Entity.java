package assets;

import geometry.GeometryObject;

public abstract class Entity {

	public Position position;
	
	//Is this a scaling factor for the geometry, or something else?
	private float size;

	//May or may refer to animation, whether the object can be destroyed, or  whether it 'physically' moves
	private boolean dynamic;

	//May require acceleration, mass, etc
	private float movementRate;
	
	public Entity(Position position, GeometryObject geometry, float size){
		this.position = position;
		this.geometry = geometry;
		this.size = size;
		this.dynamic = true; //hard coded until we know what it is
	}

	private GeometryObject geometry;

	public void draw(){
		geometry.draw(position); //Maybe pass the current position to the geometry class,
	}
	
	/****************
	 * Potential methods
	 */
	//public abstract void update();
	//public abstract void checkStillAlive();
}
