package util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import audio.AudioException;

public class FileUtil {

	public static BufferedInputStream loadFile(String fileLocation) {
		try {
			return new BufferedInputStream(new FileInputStream(fileLocation));
		} catch (java.io.FileNotFoundException ex) {
			throw new AudioException("Error reading wav file.", ex);
		}
	}

	public static void closeFile(BufferedInputStream bis) {
		try {
			bis.close();
		} catch (java.io.IOException ex) {
			throw new AudioException("Error closing wav file.", ex);
		}
	}
}
