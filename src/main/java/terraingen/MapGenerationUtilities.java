package terraingen;

import math.types.Vector3;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class MapGenerationUtilities {

    public static FloatBuffer generateColorMap(int colorMapSize) {
        ByteBuffer buf = ByteBuffer.allocateDirect(colorMapSize * 4 * 4);
        buf.order(ByteOrder.nativeOrder());

        float fraction = 1f / (float) colorMapSize;

        FloatBuffer fBuf = buf.asFloatBuffer();
        for (int i = 0; i < colorMapSize; i++) {

            float currentFraction = (fraction * i);
            float[] color;

            currentFraction += Math.random() < 0.5 ? -Math.random() * 0.01f : Math.random() * 0.01f;

            // Probably pre-compute the buffer and just adjust it
            if (currentFraction < 0.3) {
                color = new float[] { 0.2f, 0.294117f, 0.3286274509802f, 1 };
            } else if (currentFraction < 0.37) {
                color = new float[] { 0.6f, 0.594117f, 0.4686274509802f, 1 };
            } else if (currentFraction < 0.42) {
                color = new float[] { 1, 0.894117f, 0.7686274509802f, 1 };
            } else if (currentFraction < 0.65) {
                color = new float[] { 0.23921568627445f, 0.56862745098025f, 0.26921568627445f, 1 };
            } else if (currentFraction < 0.82) {
                color = new float[] { 0.3686274509803f, 0.1490196078431f, 0.0705882352941f, 1 };
            } else if (currentFraction < 0.88) {
                color = new float[] { 0.1686274509803f, 0.0290196078431f, 0.0305882352941f, 1 };
            } else {
                color = new float[] { 1, 0.99f, 0.99f, 1 };
            }

            fBuf.put(color);
        }
        fBuf.flip();

        // Apply a low pass gaussian convolution filter to smooth the boundaries
        for (int i = 12; i < fBuf.capacity() - 12; i++) {
            fBuf.put(
                    i,
                    (fBuf.get(i - 12) * 0.06f + fBuf.get(i - 8) * 0.061f + fBuf.get(i - 4) * 0.242f + fBuf.get(i) * 0.383f
                            + fBuf.get(i + 4) * 0.242f + fBuf.get(i + 8) * 0.061f + fBuf.get(i + 12) * 0.006f));
        }

        return fBuf;
    }

    private static FloatBuffer generateNormalMap(float[][] data) {
        ByteBuffer buf = ByteBuffer.allocateDirect(data.length * data.length * 4 * 3);
        buf.order(ByteOrder.nativeOrder());

        int half = data.length / 2;

        FloatBuffer fBuf = buf.asFloatBuffer();
        for (int y = 0; y < data.length; y++) {
            for (int x = 0; x < data.length; x++) {
				/*
				 * float[] normal; if (x < half && y < half) normal = new
				 * float[] { 1, 0, 0 };// else if (x > half && y < half) normal
				 * = new float[] { 0, 1, 0 }; else if (x < half && y > half)
				 * normal = new float[] { 0, 0, 1 }; else if (x > half && y >
				 * half) normal = new float[] { 1, 1, 0 }; else normal = new
				 * float[] { 0, 0, 0 };
				 *
				 * if (x < 1 || y < 1 || x > data.length - 2 || y > data.length
				 * - 2) { normal = new float[] { 0, 0, 0 }; }
				 */

                float[] normal = calculateNormal(data, x, y);
                normal[0] = (normal[0] + 1) / 2f;
                normal[1] = (normal[1] + 1) / 2f;
                normal[2] = (normal[2] + 1) / 2f;
                // float[] normal = calculateNormal(data, x, y);
                // float[] normal = new float[] { (float) Math.random(), (float)
                // Math.random(), (float) Math.random() };
                fBuf.put(normal);
            }
        }
        fBuf.flip();

        return fBuf;
    }

    private static float[] calculateNormal(float[][] data, int u, int v) {
        Vector3 normal;

        float strength = 10f; // 3 and 4 work well
        if (u > 0 && v > 0 && u < data.length - 1 && v < data.length - 1) {

            float tl = Math.abs(data[u - 1][v - 1]);
            float l = Math.abs(data[u - 1][v]);
            float bl = Math.abs(data[u - 1][v + 1]);
            float b = Math.abs(data[u][v + 1]);
            float br = Math.abs(data[u + 1][v + 1]);
            float r = Math.abs(data[u + 1][v]);
            float tr = Math.abs(data[u + 1][v - 1]);
            float t = Math.abs(data[u][v - 1]);

            // Compute dx using Sobel:
            // -1 0 1
            // -2 0 2
            // -1 0 1
            float dX = tr + 2 * r + br - tl - 2 * l - bl;

            // Compute dy using Sobel:
            // -1 -2 -1
            // 0 0 0
            // 1 2 1
            float dY = bl + 2 * b + br - tl - 2 * t - tr;

            normal = new Vector3(dX, dY, 1.0f / strength);
            normal.normalize();
        } else {
            normal = new Vector3(0, 0, 1);
        }

        // convert (-1.0 , 1.0) to (0.0 , 1.0), if necessary
        // Vector3 scale = new Vector3(0.5f, 0.5f, 0.5f);
        // Vector3.Multiply(ref N, ref scale, out N);
        // Vector3.Add(ref N, ref scale, out N);

        return new float[] { normal.x(), normal.y(), normal.z() };
    }
}
