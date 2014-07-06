package terraingen.simplex;

public class SimplexNoiseTest {

	SimplexNoise simplexNoise;

	public SimplexNoiseTest() {
		simplexNoise = new SimplexNoise(100, 0.3, 5000);

		int resolution = 512;

		ImageWriter.greyWriteImage(simplexNoise.getSection(resolution, -1, 0), "Image0.png");
		ImageWriter.greyWriteImage(simplexNoise.getSection(resolution, 0, 0), "Image1.png");
		ImageWriter.greyWriteImage(simplexNoise.getSection(resolution, 1, 0), "Image2.png");

		System.out.println("Done");
	}

	public static void main(String args[]) {

		new SimplexNoiseTest();

	}

}
