package com.HOIIVUtils.hoi4utils;

import java.io.*;
import java.util.*;
import java.nio.file.*;

/**
 * The SettingsManager class is responsible for managing the HOIIVUtils
 * properties file.
 * It provides methods to read and write settings from/to the file.
 *
 * @author battleskorpion
 */
public class HOIIVUtilsProperties {

	private static final String OLD_PROPERTIES_PATH = System.getProperty("user.home") + File.separator + "Documents"
			+ File.separator + "HOIIVUtils" + File.separator + "HOIIVUtils_properties.txt";
	/**
	 * The path to the properties file.
	 * On Windows, the file is located in the APPDATA directory.
	 * On other operating systems, the file is located in the user's home directory.
	 */
	private static final String NEW_PROPERTIES_PATH = System.getProperty("os.name").startsWith("Windows")
			? System.getenv("APPDATA") + File.separator + "HOIIVUtils" + File.separator + "hoi4utils.properties"
			: System.getProperty("user.home") + File.separator + "HOIIVUtils" + File.separator + "hoi4utils.properties";
	private static Properties properties = new Properties();

	private HOIIVUtilsProperties() {
		// private constructor to hide the implicit public one
	}

	public static void loadSettings() {
		try {
			File oldFile = new File(OLD_PROPERTIES_PATH);
			File oldFolder = oldFile.getParentFile();
			if (oldFolder.exists()) {
				Path newFolderPath = Paths.get(NEW_PROPERTIES_PATH).getParent();
				Files.move(oldFolder.toPath(), newFolderPath, StandardCopyOption.REPLACE_EXISTING);
			}
			try (InputStream input = new FileInputStream(NEW_PROPERTIES_PATH)) {
				properties.load(input);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void saveSettings() {
		try (OutputStream output = new FileOutputStream(NEW_PROPERTIES_PATH)) {
			properties.store(output, null);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static String getSetting(String key) {
		return properties.getProperty(key);
	}

	public static void setSetting(String key, String value) {
		properties.setProperty(key, value);
	}
}