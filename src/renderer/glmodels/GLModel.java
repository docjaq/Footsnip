package renderer.glmodels;

import org.lwjgl.util.vector.Vector3f;

public class GLModel {

	// Quad Moving variables
	public Vector3f modelPos;
	public Vector3f modelAngle;
	public Vector3f modelScale;

	// Textures

	// Geometry

	public GLModel(Vector3f modelPos, Vector3f modelAngle, Vector3f modelScale) {
		// Set the default quad rotation, scale and position values
		this.modelPos = modelPos;
		this.modelAngle = modelAngle;
		this.modelScale = modelScale;
	}

}
