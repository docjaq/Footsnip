package maths;

import org.lwjgl.util.vector.Vector3f;

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

	public static float radiansToDegrees(float radians) {
		return radians * (float) (180d / PI);
	}

	// 0 -> TA2, 1 -> TA0, 2 -> TA1

	// TODO: This is a horrible method. Replace this.
	public static float[] normaliseVec(float[] v) {
		float sum = 0;
		for (int i = 0; i < v.length; i++) {
			sum += v[i] * v[i];
		}
		sum = (float) Math.sqrt((double) sum);

		for (int i = 0; i < v.length; i++) {
			v[i] = v[i] / sum;
		}
		return v;
	}

	// TODO: Replace this with vec3f
	public static float[] subVec3D(float[] A, float[] B) {
		float[] sum = { A[0] - B[0], A[1] - B[1], A[2] - B[2] };
		return sum;
	}

	// public static float[] subVec3D(Vector4f A, Vector4f B) {
	// float[] sum = { A[0] - B[0], A[1] - B[1], A[2] - B[2] };
	// return sum;
	// }

	// TODO: Replace this with vec3f
	public static float[] crossProduct(float[] A, float[] B) {

		float[] crossProduct = new float[3];

		crossProduct[0] = A[1] * B[2] - A[2] * B[1];
		crossProduct[1] = A[2] * B[0] - A[0] * B[2];
		crossProduct[2] = A[0] * B[1] - A[1] * B[0];

		return crossProduct;
	}

	public static float euclideanDistance(Vector3f a, Vector3f b) {
		float dx = a.x - b.x;
		dx *= dx;
		float dy = a.y - b.y;
		dy *= dy;
		float dz = a.z - b.z;
		dz *= dz;

		return (float) Math.sqrt(dx + dy + dz);
	}
}
