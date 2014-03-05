package math;

import math.types.Matrix4;
import math.types.Quaternion;
import math.types.Vector3;

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

	// TODO: Replace this with vec3f
	public static float[] crossProduct(float[] A, float[] B) {

		float[] crossProduct = new float[3];

		crossProduct[0] = A[1] * B[2] - A[2] * B[1];
		crossProduct[1] = A[2] * B[0] - A[0] * B[2];
		crossProduct[2] = A[0] * B[1] - A[1] * B[0];

		return crossProduct;
	}

	public static float euclideanDistance(Vector3 a, Vector3 b) {
		float dx = a.x() - b.x();
		dx *= dx;
		float dy = a.y() - b.y();
		dy *= dy;
		float dz = a.z() - b.z();
		dz *= dz;

		return (float) Math.sqrt(dx + dy + dz);
	}

	public static Quaternion angleAxisDeg(float angle, Vector3 vec) {
		return new Quaternion((float) Math.toRadians(angle), vec);
	}

	public static float clamp(float value, float low, float high) {
		return Math.min(Math.max(value, low), high);
	}

	public static float mix(float f1, float f2, float a) {
		return f1 + (f2 - f1) * a;
	}

	public static Matrix4 lookAt(Vector3 eye, Vector3 center, Vector3 up) {
		Vector3 f = center.copy().sub(eye).normalize();
		up = up.copy().normalize();

		Vector3 s = f.cross(up);
		Vector3 u = s.cross(f);

		return new Matrix4(new float[] { s.x(), u.x(), -f.x(), 0, s.y(), u.y(), -f.y(), 0, s.z(), u.z(), -f.z(), 0, 0, 0, 0, 1 })
				.translate(eye.copy().mult(-1));
	}
}
