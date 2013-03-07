package main;

public class Main {

	/*********************************
	 * JAQ Levels should maybe be entities, as it would seemingly make
	 * intersections etc simpler to do (as you only ever have to intersect two
	 * entities). Having said that, we might use special rules to intersect
	 * entities, with, say, zone 'boundaries' or something... so I'm not sure
	 * again. Plus things like boundbox unions would be confusing up for a map.
	 * Hmm.
	 */

	public Main() {

	}

	public static void main(String[] args) {
		System.out.println("Hello Creamsnip");
		new Main();
	}
}

/**
 * TODO: J: SHADERS: I think we need to implement a different shader to handle
 * non-textured entities. Basically, we're sending no texture to the geometry
 * shader, and therefore it's just using the old texture. In opengl <2.0, you
 * could just enable and disable the texture unit, but not possible anymore, so
 * I think we need to use a different shader.
 * 
 * TODO: J: LIGHTING: Add lighting to the game. I'm currently looking into this
 * in OGL3.3
 */
