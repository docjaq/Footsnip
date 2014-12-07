package terraingen.simplex;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageWriter {
	// just convinence methods for debug

	public static void greyWriteImage(float[][] fs, String fileName) {
		// this takes and array of doubles between 0 and 1 and generates a grey
		// scale image from them

		BufferedImage image = new BufferedImage(fs.length, fs[0].length, BufferedImage.TYPE_INT_RGB);

		for (int y = 0; y < fs[0].length; y++) {
			for (int x = 0; x < fs.length; x++) {
				if (fs[x][y] > 1) {
					fs[x][y] = 1;
				}
				if (fs[x][y] < 0) {
					fs[x][y] = 0;
				}
				Color col = new Color((float) fs[x][y], (float) fs[x][y], (float) fs[x][y]);
				image.setRGB(x, y, col.getRGB());
			}
		}

		try {
			// retrieve image
			File outputfile = new File(fileName);
			outputfile.createNewFile();

			ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
			// o no! Blank catches are bad
			throw new RuntimeException("I didn't handle this very well");
		}
	}

}