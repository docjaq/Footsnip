package renderer;

import maths.types.Vector3;

public class GLPosition {
	public Vector3 modelPos;
	public Vector3 modelAngle;
	public Vector3 modelScale;
	private float entityRadius;

	public GLPosition(Vector3 modelPos, Vector3 modelAngle, float modelScale, float modelRadius) {
		this.modelPos = modelPos;
		this.modelAngle = modelAngle;
		setModelScale(modelScale);
		setEntityRadiusWithModelRadius(modelRadius);
	}

	public Vector3 getModelPos() {
		return modelPos;
	}

	public void setModelPos(Vector3 modelPos) {
		this.modelPos = modelPos;
	}

	public Vector3 getModelAngle() {
		return modelAngle;
	}

	public void setModelAngle(Vector3 modelAngle) {
		this.modelAngle = modelAngle;
	}

	public void setModelScale(float modelScale) {
		/**
		 * This has been changed to only allow uniform scaling of the model.
		 * This means that we can compute the bounding sphere more easily at
		 * runtime
		 **/
		if (this.modelScale == null) {
			this.modelScale = new Vector3();
		}
		this.modelScale.set(modelScale, modelScale, modelScale);

		// radius *= getModelScale();
	}

	public float getModelScale() {
		return modelScale.x();
	}

	public float getEntityRadius() {
		return entityRadius;
	}

	public void setEntityRadiusWithModelRadius(float modelRadius) {
		this.entityRadius = modelRadius * modelScale.x();
	}
}
