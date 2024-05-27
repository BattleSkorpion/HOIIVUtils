package com.HOIIVUtils.hoi4utils;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.*;
import java.util.*;

import com.HOIIVUtils.hoi4utils.ioexceptions.SettingsFileException;
import com.HOIIVUtils.hoi4utils.ioexceptions.SettingsWriteException;

/**
 * The SettingsManager class is responsible for managing the HOIIVUtils properties file. It provides
 * methods to read and write settings from/to the file.
 *
 * @author battleskorpion
 */
public class SettingsManager {

	public static final String OLD_PROPERTIES_PATH = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "HOIIVUtils"
			+ File.separator + "HOIIVUtils_properties.txt";
	public static final String NEW_PROPERTIES_PATH = System.getProperty("os.name").startsWith("Windows")
			? System.getenv("APPDATA") + File.separator + "HOIIVUtils" + File.separator + "hoi4utils.properties"
			: System.getProperty("user.home") + File.separator + ".hoi4utils" + File.separator + "hoi4utils.properties";
	public static final String OLD_PROPERTIES_PATH_PARENT =
			System.getProperty("user.home") + File.separator + "Documents" + File.separator + "HOIIVUtils";
	public static final String NEW_PROPERTIES_PATH_PARENT =
			System.getProperty("os.name").startsWith("Windows") ? System.getenv("APPDATA") + File.separator + "HOIIVUtils"
					: System.getProperty("user.home") + File.separator + ".hoi4utils";

	private static File settingsFile;
	private static FileWriter settingsWriter;
	private static BufferedWriter settingsBWriter;
	private static PrintWriter settingsPWriter;// = new PrintWriter(settingsBWriter); // for println syntax
	static HashMap<Settings, String> settingValues = new HashMap<>();
	public static SettingsManager settings;

	public SettingsManager(HashMap<Settings, String> settingsHash) {
		try {
			Files.createDirectories(Paths.get(NEW_PROPERTIES_PATH).getParent());
			settingsFile = new File(NEW_PROPERTIES_PATH);

			if (settingsHash == null) {
				writeBlankSettings();
			} else if (settingsFile.createNewFile()) {
				writeBlankSettings();
				writeSettings(settingsHash);
			} else {
				readSettings();
				writeSettings(settingsHash);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void convertOldPropertiesFile() {
		if (new File(OLD_PROPERTIES_PATH).exists()) {
			try {
				Files.createDirectories(Paths.get(NEW_PROPERTIES_PATH).getParent());
				Files.copy(Paths.get(OLD_PROPERTIES_PATH), Paths.get(NEW_PROPERTIES_PATH));
				Files.walkFileTree(Paths.get(OLD_PROPERTIES_PATH_PARENT), new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
						Files.delete(file);
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
						Files.delete(dir);
						return FileVisitResult.CONTINUE;
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Reads HOIIVUtils Settings from hoi4utils.properties file.
	 *
	 * This function reads the settings from the hoi4utils.properties file and stores them in the
	 * settingValues HashMap. If the file is empty, it writes blank HOIIVUtils Settings to a new
	 * hoi4utils.properties file. It also checks if all the settings are present in the file and writes
	 * any missing settings using the writeBlankSetting function.
	 *
	 * @throws SettingsFileException if there is an error reading the settings file
	 */
	public static void readSettings() {
		try {
			Scanner settingReader = new Scanner(settingsFile);

			/*
			 * if file is empty then write blank HOIIVUtils Settings to new hoi4utils.properties file
			 */
			if (!settingReader.hasNext()) {
				writeBlankSettings();
				settingReader.close();
				return;
			}

			/* read HOIIVUtils Settings */
			while (settingReader.hasNextLine()) {
				String[] readSetting = settingReader.nextLine().split(";");
				Settings setting = Settings.valueOf(readSetting[0]);

				settingValues.put(setting, readSetting[1]);
			}

			for (Settings setting : Settings.values()) {
				if (!settingValues.containsKey(setting)) {
					writeBlankSetting(setting);
				}
			}
			settingReader.close();

		} catch (IOException e) {
			throw new SettingsFileException("Error reading settings", e);
		}

	}

	/**
	 * Saves setting with specified value
	 */
	public static void writeSetting(Settings setting, String settingValue) throws IOException {

		try {
			settingsWriter = new FileWriter(settingsFile, false);
		} catch (IOException e) {
			throw new SettingsWriteException("Error writing settings", e);
		}

		settingsBWriter = new BufferedWriter(settingsWriter);
		settingsPWriter = new PrintWriter(settingsBWriter);

		settingValues.put(setting, settingValue);
		for (Settings s : Settings.values()) {
			settingsPWriter.println(s.name() + ";" + settingValues.get(s));
		}

		settingsPWriter.close();
	}

	/**
	 * Saves a list of HOIIVUtils Settings to hoi4utils.properties file, all other HOIIVUtils Settings
	 * remain the same.
	 * 
	 * @param newSettings list of updated HOIIVUtils Settings to save
	 */
	public static void writeSettings(HashMap<Settings, String> newSettings) {
		if (newSettings == null) {
			return;
		}

		try {
			settingsWriter = new FileWriter(settingsFile, false);
		} catch (IOException e) {
			throw new SettingsWriteException("Error writing settings", e);
		}

		settingsBWriter = new BufferedWriter(settingsWriter);
		settingsPWriter = new PrintWriter(settingsBWriter);

		settingValues.putAll(newSettings);
		for (Settings s : Settings.values()) {
			settingsPWriter.println(s.name() + ";" + settingValues.get(s));
		}

		settingsPWriter.close();
	}

	/**
	 * Saves all HOIIVUtils Settings to hoi4utils.properties file.
	 *
	 * @throws SettingsWriteException if there is an error saving the settings
	 */
	public static void writeSettings() {
		try {
			settingsWriter = new FileWriter(settingsFile, false);
		} catch (IOException e) {
			throw new SettingsWriteException("Error writing settings", e);
		}

		settingsBWriter = new BufferedWriter(settingsWriter);
		settingsPWriter = new PrintWriter(settingsBWriter);

		for (Settings s : Settings.values()) {
			settingsPWriter.println(s.name() + ";" + settingValues.get(s));
		}

		settingsPWriter.close();
	}

	/**
	 * Writes a blank setting to the hoi4utils.properties file
	 */
	private static void writeBlankSetting(Settings setting) throws IOException {
		try {
			settingsWriter = new FileWriter(settingsFile, true);
		} catch (IOException e) {
			throw new SettingsWriteException("Error writing settings", e);
		}
		settingsWriter = new FileWriter(settingsFile, true); // true = append
		settingsBWriter = new BufferedWriter(settingsWriter);
		settingsPWriter = new PrintWriter(settingsBWriter);

		settingsPWriter.println(setting.name() + ";" + setting.defaultProperty());

		settingsPWriter.close();
	}

	/**
	 * Writes default HOIIVUtils Settings to the hoi4utils.properties file
	 */
	public static void writeBlankSettings() {
		try {
			settingsWriter = new FileWriter(settingsFile, false);
		} catch (IOException e) {
			throw new SettingsWriteException("Error writing settings", e);
		}

		settingsBWriter = new BufferedWriter(settingsWriter);
		settingsPWriter = new PrintWriter(settingsBWriter);

		for (Settings setting : Settings.values()) {
			settingsPWriter.println(setting.name() + ";" + setting.defaultProperty());

			settingValues.put(setting, setting.defaultProperty());
		}

		settingsPWriter.close();
	}

	/**
	 * Deletes all HOIIVUtils Settings from the HOIIVUtils Settings directory.
	 *
	 * @throws IOException if an I/O error occurs
	 */
	public static void deleteAllSettings() throws IOException {
		try (Stream<Path> paths = Files.walk(Paths.get(NEW_PROPERTIES_PATH))) {
			// Sort the paths in reverse order so that the
			// directories are deleted first
			paths.sorted(Comparator.reverseOrder()).forEach(path -> {
				try {
					System.out.println("Deleting: " + path);
					Files.delete(path);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
	}

	public static String get(Settings setting) {
		return settingValues.get(setting);
	}

	public static boolean isNull(Settings setting) {
		return settingValues.get(setting).equals("null");
	}
}
