package com.fniephaus.bulkdataprocessor.helpers;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

public class Configuration {
	private final static XMLConfiguration config = loadConfiguration();

	public static enum StatusType {
		UNKNOWN, OK, ERROR;
	}

	private static XMLConfiguration loadConfiguration() {
		try {
			XMLConfiguration config = new XMLConfiguration();
			config.setDelimiterParsingDisabled(true);
			config.load("resources/config.xml");
			return config;
		} catch (ConfigurationException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static int getInt(String key) {
		return config.getInt(key);
	}

	public static float getFloat(String key) {
		return config.getFloat(key);
	}

	public static boolean getBoolean(String key) {
		return config.getBoolean(key);
	}

	public static String getString(String key) {
		return config.getString(key);
	}

	public static String[] getStringArray(String key) {
		return config.getStringArray(key);
	}
}
