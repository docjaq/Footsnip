package maths;

public class LinearAlgebra {

	private LinearAlgebra() {
	};

	public static final double PI = 3.14159265358979323846;
	public static final double TAU = 2 * PI;

	public static float coTangent(float angle) {
		return (float) (1f / Math.tan(angle));
	}

	public static float degreesToRadians(float degrees) {
		return degrees * (float) (PI / 180d);
	}
}
