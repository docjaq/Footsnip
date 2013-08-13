package TerrainGeneration;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class PlasmaFractalFactory {

	private static final float variance = 0.4f;

	public static void create(float[][] inputMatrix) {
		int size = inputMatrix.length;
		/*
		 * for (int i = 0; i < inputMatrix.length; i++) { for (int j = 0; j <
		 * inputMatrix.length; j++) { inputMatrix[i][j] = 0; } }
		 */
		recursiveGeneration(inputMatrix, 0, 0, size, size, 0, 0, 0, 0);
		scale(inputMatrix, size);
		// smooth(inputMatrix, size);
		// debug(inputMatrix, size);
	}

	private static void scale(float[][] matrix, int size) {
		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				matrix[x][y] *= variance;
			}
		}
	}

	private static void smooth(float[][] matrix, int size) {
		float maxZ = 0f, minZ = Float.MAX_VALUE;
		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				if (matrix[x][y] > maxZ) {
					maxZ = matrix[x][y];
				} else if (matrix[x][y] < minZ) {
					minZ = matrix[x][y];
				}
			}
		}

		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				matrix[x][y] = (matrix[x][y] - minZ) / ((maxZ - minZ) * 2);
				// matrix[x][y] *= variance;
				// if (matrix[x][y] > 0.18f) {
				// matrix[x][y] = 0.18f;
				// }
			}
		}
	}

	private static void recursiveGeneration(float[][] matrix, float x, float y, float width, float height, float c1, float c2, float c3,
			float c4) {
		float Edge1, Edge2, Edge3, Edge4, Middle;
		float newWidth = width / 2f;
		float newHeight = height / 2f;

		if (width > 2 || height > 2) {
			Middle = (c1 + c2 + c3 + c4) / 4f + Displace(matrix.length, matrix.length, newWidth + newHeight); // Randomly
			// displace
			// the
			// midpoint!
			Edge1 = (c1 + c2) / 2; // Calculate the edges by averaging the two
									// corners of each edge.
			Edge2 = (c2 + c3) / 2;
			Edge3 = (c3 + c4) / 2;
			Edge4 = (c4 + c1) / 2;

			// Make sure that the midpoint doesn't accidentally
			// "randomly displaced" past the boundaries!
			if (Middle < -1.0f) {
				Middle = -1.0f;
			} else if (Middle > 1.0f) {
				Middle = 1.0f;
			}

			// Do the operation over again for each of the four new grids.
			recursiveGeneration(matrix, x, y, newWidth, newHeight, c1, Edge1, Middle, Edge4);
			recursiveGeneration(matrix, x + newWidth, y, newWidth, newHeight, Edge1, c2, Edge2, Middle);
			recursiveGeneration(matrix, x + newWidth, y + newHeight, newWidth, newHeight, Middle, Edge2, c3, Edge3);
			recursiveGeneration(matrix, x, y + newHeight, newWidth, newHeight, Edge4, Middle, Edge3, c4);
		} else // This is the "base case," where each grid piece is less than
				// the size of a pixel.
		{
			// The four corners of the grid piece will be averaged and drawn as
			// a single pixel.
			float c = (c1 + c2 + c3 + c4) / 4f;

			// g.setColor(new Color(c, c, c, 1));

			matrix[(int) x][(int) y] = c;
			// System.out.println((int) (x) + "," + (int) (y));// Java doesn't
			// have
			// a function
			// to draw a single pixel, so
			// a 1 by 1 rectangle is used.
		}
	}

	private static float Displace(float width, float height, float num) {
		float max = num / (float) (width + height) * 10f;
		return ((float) Math.random() - 0.5f) * max * 1.0f;
	}

	private static void debug(float[][] matrix, int size) {

		BufferedImage myImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);

		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				// System.out.print(matrix[x][y] + ",");
				myImage.setRGB(x, y,
						pack(new int[] { (int) (matrix[x][y] * 255f), (int) (matrix[x][y] * 255f), (int) (matrix[x][y] * 255f) }));
			}
		}

		try {
			// retrieve image
			File outputfile = new File("saved" + Math.random() * 100 + ".png");
			ImageIO.write(myImage, "png", outputfile);
		} catch (IOException e) {
		}
	}

	private static int pack(int[] rgb) {
		return ((rgb[0] & 255) << 16) | ((rgb[1] & 255) << 8) | ((rgb[2] & 255)) | 0xff000000;
	}
}
