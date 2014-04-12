package main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FootsnipProperties {
	private static final String DEFAULT_FILEPATH = "resources/conf/default.properties";
	private static final String LOCAL_FILEPATH = "resources/conf/local.properties";

	private static Properties properties;

	private synchronized static void init() {
		if (properties == null) {
			loadProperties();
		}
	}

	private static void loadProperties() {
		properties = new Properties();
		try {
			properties.load(getInputStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static InputStream getInputStream() {
		InputStream stream;
		try {
			stream = new FileInputStream(LOCAL_FILEPATH);
		} catch (FileNotFoundException ex) {
			try {
				stream = new FileInputStream(DEFAULT_FILEPATH);
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		}

		return stream;
	}

	private static int getIntProperty(String key) {
		return Integer.parseInt(getProperty(key));
	}

	private static String getProperty(String key) {
		init();
		return properties.getProperty(key);
	}

	public static int getHeight() {
		return getIntProperty("window.height");
	}

	public static int getWidth() {
		return getIntProperty("window.width");
	}
}
