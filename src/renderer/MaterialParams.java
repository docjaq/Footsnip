package renderer;

public class MaterialParams {
	private float gaussianExponent;

	public MaterialParams() {
		this.gaussianExponent = 0.5f;
	}

	public MaterialParams(float gaussianExponent) {
		this.gaussianExponent = gaussianExponent;
	}

	public float getSpecularValue() {
		return gaussianExponent;
	}

	public void setSpecularValue(float value) {
		gaussianExponent = value;
	}

	public void increment(boolean isLarge) {
		float param = getSpecularValue();
		if (isLarge)
			param += 0.1f;
		else
			param += 0.01f;

		setSpecularValue(clamp(param));
	}

	public void decrement(boolean isLarge) {
		float param = getSpecularValue();

		if (isLarge)
			param -= 0.1f;
		else
			param -= 0.01f;

		setSpecularValue(clamp(param));
	}

	private float clamp(float param) {
		return Math.min(Math.max(0.00001f, param), 1);

	}
}