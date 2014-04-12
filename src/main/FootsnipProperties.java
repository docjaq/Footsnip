package main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FootsnipProperties {
	private static final String FILEPATH = "resources/conf/default.properties";
	private static Properties properties;

	private synchronized static void init() {
		if (properties == null) {
			loadProperties();
		}
	}

	private static void loadProperties() {
		properties = new Properties();
		try {
			InputStream stream = new FileInputStream(FILEPATH);
			properties.load(stream);
		} catch (IOException e) {
			throw new RuntimeException("Error loading properties file: " + FILEPATH, e);
		}
	}

	public static int getHeight() {
		init();
		return Integer.parseInt(properties.getProperty("window.height"));
	}

	public static int getWidth() {
		init();
		return Integer.parseInt(properties.getProperty("window.width"));
	}
}
