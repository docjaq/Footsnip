package maths;

import renderer.glprimitives.GLVertex_normal;

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

	// TODO: Convert this method to working on the Vector4fs that will
	// eventually be a part of the GLVertex classes
	public static void addNormalToGLVertex(GLVertex_normal v0, GLVertex_normal v1, GLVertex_normal v2) {
		float[] va0 = v0.getXYZ();
		float[] va1 = v1.getXYZ();
		float[] va2 = v2.getXYZ();

		float[] normal = normaliseVec(crossProduct(subVec3D(va1, va0), subVec3D(va2, va0)));

		v0.setNXNYNZ(normal);
	}

	// 0 -> TA2, 1 -> TA0, 2 -> TA1

	public static void addNormalToTriangle(GLVertex_normal v0, GLVertex_normal v1, GLVertex_normal v2) {
		float[] va0 = v0.getXYZ();
		float[] va1 = v1.getXYZ();
		float[] va2 = v2.getXYZ();

		float[] normal = normaliseVec(crossProduct(subVec3D(va1, va0), subVec3D(va2, va0)));
		v0.setNXNYNZ(normal);

		float[] normal2 = normaliseVec(crossProduct(subVec3D(va2, va1), subVec3D(va0, va1)));
		v1.setNXNYNZ(normal2);

		float[] normal3 = normaliseVec(crossProduct(subVec3D(va0, va2), subVec3D(va1, va2)));
		v2.setNXNYNZ(normal3);
	}

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
}
