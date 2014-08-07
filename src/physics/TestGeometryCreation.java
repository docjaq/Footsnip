package physics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.vecmath.Vector3f;

public class TestGeometryCreation {
	private static final float TRIANGLE_SIZE = 8.f;
	private static int NUM_VERTS_X = 30;
	private static int NUM_VERTS_Y = 30;
	int totalVerts = NUM_VERTS_X * NUM_VERTS_Y;
	private static float waveheight = 5.f;

	int totalTriangles = 2 * (NUM_VERTS_X - 1) * (NUM_VERTS_Y - 1);

	ByteBuffer gVertices;
	ByteBuffer gIndices;

	int vertStride = 3 * 4;
	int indexStride = 3 * 4;

	public TestGeometryCreation() {

		gVertices = ByteBuffer.allocateDirect(totalVerts * 3 * 4).order(ByteOrder.nativeOrder());
		gIndices = ByteBuffer.allocateDirect(totalTriangles * 3 * 4).order(ByteOrder.nativeOrder());

		int i;

		setVertexPositions(waveheight, 0.f);

		// int index=0;
		gIndices.clear();
		for (i = 0; i < NUM_VERTS_X - 1; i++) {
			for (int j = 0; j < NUM_VERTS_Y - 1; j++) {
				gIndices.putInt(j * NUM_VERTS_X + i);
				gIndices.putInt(j * NUM_VERTS_X + i + 1);
				gIndices.putInt((j + 1) * NUM_VERTS_X + i + 1);

				gIndices.putInt(j * NUM_VERTS_X + i);
				gIndices.putInt((j + 1) * NUM_VERTS_X + i + 1);
				gIndices.putInt((j + 1) * NUM_VERTS_X + i);
			}
		}
		gIndices.flip();
	}

	private void setVertexPositions(float waveheight, float offset) {
		int i;
		int j;
		Vector3f tmp = new Vector3f();

		for (i = 0; i < NUM_VERTS_X; i++) {
			for (j = 0; j < NUM_VERTS_Y; j++) {
				tmp.set((i - NUM_VERTS_X * 0.5f) * TRIANGLE_SIZE,
				// 0.f,
						waveheight * (float) Math.sin((float) i + offset) * (float) Math.cos((float) j + offset), (j - NUM_VERTS_Y * 0.5f)
								* TRIANGLE_SIZE);

				int index = i + j * NUM_VERTS_X;
				gVertices.putFloat((index * 3 + 0) * 4, tmp.x);
				gVertices.putFloat((index * 3 + 1) * 4, tmp.y);
				gVertices.putFloat((index * 3 + 2) * 4, tmp.z);
			}
		}
	}
}
