package geometry;

import java.util.ArrayList;

import assets.Position;

public class GeometryObject {

	private ArrayList<GeometryObject> children;
	private GeometryObject parent;
	private BoundingBox bounds;
	private BoundingBox childBounds;
	private boolean hasChildren;
	
	public GeometryObject(){
		hasChildren = false;
		computeBoundingBox();
	}
	
	public void draw(Position position){
		//Note, a GeometryObject has its own coordinate system,
		//probably from 0,0 at its centroid
		//Thus, the GeometryObject, and any children it has,
		//should be drawn OFFSET from the position passed to it
		
		if(hasChildren)
			for(GeometryObject g: children){
				g.draw(position);
			}
		
		//Perform actual rendering
	}
	
	public void addChild(GeometryObject geometry){
		if(children == null){
			//It tells me that the type here should be ArrayList<GeometryObjects>,
			//but that's already defined. Netbeans always corrects it the other way
			//around. Preference?
			children = new ArrayList();
			hasChildren = true;
		}
		geometry.setParent(this);
		children.add(geometry);
	}
	
	public GeometryObject getParent() {
		return parent;
	}

	public void setParent(GeometryObject parent) {
		this.parent = parent;
	}
	
	/**************************
	 * I started writing all these boundingbox/intersection methods then realised
	 * A) I didn't know what the fuck I was doing
	 * B) How the bounding box hierarchy worked (do you test at each level to maximise performance?
	 * C) How often do we have to do this, and it's probably based on a data-structure to say whether
	 * two objects are even close or not
	 * D) How to efficiently test the intersection of two hierarchies
	 * E) It's probably best to do this later
	 */
	
	
	public void computeBoundingBox(){
		if(bounds == null)
			bounds = new BoundingBox();
	}
	public void computeChildBounds(){
		//Recursively compute the union of an object and its children
		//storing that union at each level in the hierarchy
	}	
	public BoundingBox getBoundingBox(){
		return bounds;
	}
	public boolean intersectsThis(){
		//If it intersects the bbox of this entity
		return false; //hardcoded for now
	}
	public boolean intersects(GeometryObject target){
		for(GeometryObject g: children)
			g.intersects(target);
		
		//Perform intersection test
		//It should check intersection against all children of object
		//Perhaps best to compute a general
		
		return false; //hardcoded for now
	}
}
