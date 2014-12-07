package terraingen.simplex;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

public class SimplexNoise {

	SimplexOctave[] octaves;
	double[] frequencys;
	double[] amplitudes;

	int largestFeature;
	double persistence;
	int seed;

	public SimplexNoise(int largestFeature, double persistence, int seed) {
		this.largestFeature = largestFeature;
		this.persistence = persistence;
		this.seed = seed;

		// recieves a number (eg 128) and calculates what power of 2 it is (eg
		// 2^7)
		int numberOfOctaves = (int) Math.ceil(Math.log10(largestFeature) / Math.log10(2));

		octaves = new SimplexOctave[numberOfOctaves];
		frequencys = new double[numberOfOctaves];
		amplitudes = new double[numberOfOctaves];

		Random rnd = new Random(seed);

		for (int i = 0; i < numberOfOctaves; i++) {
			octaves[i] = new SimplexOctave(rnd.nextInt());

			frequencys[i] = Math.pow(2, i);
			amplitudes[i] = Math.pow(persistence, octaves.length - i);

		}

	}

	private double getNoise(int x, int y) {

		double result = 0;

		for (int i = 0; i < octaves.length; i++) {
			// double frequency = Math.pow(2,i);
			// double amplitude = Math.pow(persistence,octaves.length-i);

			result = result + octaves[i].noise(x / frequencys[i], y / frequencys[i]) * amplitudes[i];
		}

		return result;

	}

	// Deprecated method, should not be used.
	public float[][] getSection(int resolution, int xOffset, int yOffset, float zScale, float zOffset) {

		int xStart, yStart;
		int offsetScale = resolution;

		xStart = xOffset * (offsetScale - 1);
		yStart = yOffset * (offsetScale - 1);

		int xEnd = xStart + offsetScale;
		int yEnd = yStart + offsetScale;

		float[][] result = new float[resolution][resolution];

		for (int i = 0; i < resolution; i++) {
			for (int j = 0; j < resolution; j++) {
				int x = (int) (xStart + i * ((xEnd - xStart) / (double) (resolution)));
				int y = (int) (yStart + j * ((yEnd - yStart) / (double) (resolution)));
				result[i][j] = ((float) getNoise(x, y) * zScale - zOffset);
			}
		}

		return result;
	}

	public ByteBuffer getSectionAsByteBuffer(int resolution, int xOffset, int yOffset, float zScale, float zOffset) {

		int xStart, yStart;
		int offsetScale = resolution;

		xStart = xOffset * (offsetScale - 1);
		yStart = yOffset * (offsetScale - 1);

		int xEnd = xStart + offsetScale;
		int yEnd = yStart + offsetScale;

		int bufferSize = resolution * resolution * 4;

		ByteBuffer result = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder());
		for (int i = 0; i < resolution; i++) {
			for (int j = 0; j < resolution; j++) {
				int x = (int) (xStart + j * ((xEnd - xStart) / (float) (resolution)));
				int y = (int) (yStart + i * ((yEnd - yStart) / (float) (resolution)));

				// Careful here, as had to flip the xy to get the same ordering
				// as the array above

				// (* 0.7f - 0.5f)
				// TODO: Bit of a hack here to get some relevant terrain
				float noiseZ = (float) getNoise(x, y);
				noiseZ += 1;
				noiseZ = (float) Math.pow(noiseZ, 2);
				noiseZ -= 0.3;

				// (z + 1f) / 2f is for buffer embedding for gpu
				result.putFloat(((noiseZ * zScale - zOffset) + 1f) / 2f);

			}
		}

		result.rewind();

		return result;
	}
}