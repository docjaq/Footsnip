package terraingen;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GlobalHeightmap {

	public static final int TILE_SIZE = 3;
	private BufferedImage heightMap;

	/*
	 * NOTE
	 * 
	 * 1. Probably need to either flip the y of the heightmap when it's read, or
	 * adjust each indexing call, as Java uses a top left origin co-ordinate
	 * system, and Footsnip uses middle. I.e., we increase up and right, they
	 * increase down and right
	 * 
	 * 2. To further compound this problem, our heightmap data-structure uses an
	 * indexing system that starts at 0,0 for the initial tile. This is somewhat
	 * confusing with respect to the initial position within the file-read
	 * heightap. I suppose there's no reason that this *should* start at 0,0, or
	 * we just do a shift of the coordinates at runtime
	 */

	public GlobalHeightmap(String fileLocation) {
		try {
			heightMap = ImageIO.read(new File(fileLocation));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getHeight(int x, int y) {
		// May need to 'unpack' these values from a single n-bit value, although
		// something simpler might be required as it's just a single value
		// (grayscale)
		return heightMap.getRGB(x, y);
	}

	public BufferedImage getBlock(int x, int y) {
		return heightMap.getSubimage(x - 1, y - 1, TILE_SIZE, TILE_SIZE);
	}

}
