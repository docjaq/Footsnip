package util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class FileUtil {

	public static BufferedInputStream loadFile(String fileLocation) {
		try {
			return new BufferedInputStream(new FileInputStream(fileLocation));
		} catch (java.io.FileNotFoundException ex) {
			throw new RuntimeException("Error reading wav file.", ex);
		}
	}

	public static void closeFile(BufferedInputStream bis) {
		try {
			bis.close();
		} catch (java.io.IOException ex) {
			throw new RuntimeException("Error closing wav file.", ex);
		}
	}
}
