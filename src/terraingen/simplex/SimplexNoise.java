package terraingen.simplex;

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

		System.err.println("Creating tile for " + xOffset + "," + yOffset);

		int offsetScale = resolution;

		int xStart = xOffset * offsetScale;
		int yStart = yOffset * offsetScale;

		int xEnd = xStart + offsetScale;
		int yEnd = yStart + offsetScale;

		float[][] result = new float[resolution][resolution];

		for (int i = 0; i < resolution; i++) {
			for (int j = 0; j < resolution; j++) {
				int x = (int) (xStart + i * ((xEnd - xStart) / (double) resolution));
				int y = (int) (yStart + j * ((yEnd - yStart) / (double) resolution));
				result[i][j] = (float) (0.5d * (1 + getNoise(x, y))) - 0.6f;
			}
		}

		return result;
	}

}