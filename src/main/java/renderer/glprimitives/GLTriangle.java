package renderer.glprimitives;

import math.types.Vector3;

public class GLTriangle {
	/**
	 * Debatable whether to leave these public. I know it's bad, but the thought
	 * of always doing .getX() to refer to them is annoying
	 **/
	public GLVertex v0, v1, v2;
	public Vector3 normal;

	public GLTriangle(GLVertex v0, GLVertex v1, GLVertex v2) {
		this.v0 = v0;
		this.v1 = v1;
		this.v2 = v2;
	}
}
