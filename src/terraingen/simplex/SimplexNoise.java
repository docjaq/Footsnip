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

	public float[][] getSection(int resolution, int xOffset, int yOffset) {

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
				result[i][j] = ((float) getNoise(x, y) * 0.7f - 0.5f);// - 0.7f
																		// +
																		// 0.3f;
			}
		}

		/*
		 * System.out.println("Heightmap creation test: array"); for (int i = 0;
		 * i < 10; i++) { System.out.print(result[0][i]); }
		 * System.out.println();
		 */

		return result;
	}

	public ByteBuffer getSectionAsByteBuffer(int resolution, int xOffset, int yOffset) {

		int xStart, yStart;
		int offsetScale = resolution;

		xStart = xOffset * (offsetScale - 1);
		yStart = yOffset * (offsetScale - 1);

		int xEnd = xStart + offsetScale;
		int yEnd = yStart + offsetScale;

		ByteBuffer result = ByteBuffer.allocateDirect(resolution * resolution * 4).order(ByteOrder.nativeOrder());

		for (int i = 0; i < resolution; i++) {
			for (int j = 0; j < resolution; j++) {
				int x = (int) (xStart + i * ((xEnd - xStart) / (float) (resolution)));
				int y = (int) (yStart + j * ((yEnd - yStart) / (float) (resolution)));
				result.putFloat(((float) getNoise(x, y) * 0.7f - 0.5f));// -
																		// 0.7f
																		// +
																		// 0.3f;
			}
		}

		result.flip();
		/*
		 * System.out.println("Heightmap creation test: buffer"); for (int i =
		 * 0; i < 10; i++) { System.out.print(result.getFloat()); }
		 * result.rewind();
		 */

		System.out.println();

		return result;
	}
}