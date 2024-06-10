package com.HOIIVUtils.hoi4utils;

import com.HOIIVUtils.hoi4utils.fileIO.FileListener.FileAdapter;
import com.HOIIVUtils.hoi4utils.fileIO.FileListener.FileEvent;
import com.HOIIVUtils.hoi4utils.fileIO.FileListener.FileWatcher;
import com.HOIIVUtils.hoi4utils.clausewitz_map.state.State;
import com.HOIIVUtils.ui.message.MessageController;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * HOIIV File A Everything to do with the files that are found in a HOI 4 mod - Paths - Creating
 * Files and Directories ? <insert about whatever the file watchers do> chris learning what is a
 * file watcher - Future things
 */
public class HOIIVFile implements FileUtils {

	public static final File usersParadoxHOIIVModFolder =
			new File(File.separator + "Paradox Interactive" + File.separator + "Hearts of Iron IV" + File.separator + "mod");

	@SuppressWarnings("exports")
	public static FileWatcher stateFilesWatcher;

	public static File modPathFile;

	public static File mod_focus_folder;
	public static File mod_ideas_folder;
	public static File mod_states_folder;
	public static File mod_strat_region_dir;
	public static File mod_localization_eng_folder;
	public static File mod_common_folder;
	public static File hoi4mods_folder;
	public static File mod_units_folder;
	public static File hoi4_units_folder;

	public static void createHOIIVFilePaths() {
		new SettingsManager(null);
		String modPath = SettingsManager.get(Settings.MOD_PATH);
		String hoi4Path = SettingsManager.get(Settings.HOI4_PATH);
		System.out.println("modPath: " + modPath);

		if (modPath == null) {
			MessageController window = new MessageController();
			window.open("Error: modPath was null and we can't create any files");
			return;
		}

		File modPathFile = new File(modPath);

		mod_common_folder = new File(modPath + "\\common");
		mod_states_folder = new File(modPath + "\\history\\states");
		mod_strat_region_dir = new File(modPath + "\\map\\strategicregions");
		mod_localization_eng_folder = new File(modPath + "\\localisation\\english");
		mod_focus_folder = new File(modPath + "\\common\\national_focus");
		mod_ideas_folder = new File(modPath + "\\common\\ideas");
		mod_units_folder = new File(modPath + "\\common\\units");
		hoi4_units_folder = new File(hoi4Path + "\\common\\units");
		hoi4mods_folder = modPathFile.getParentFile();
		System.out.println("HOIIVFile created paths");
	}

	/**
	 * Watch the state files in the given directory.
	 *
	 * @param stateFiles The directory containing state files.
	 * @throws IOException If an I/O error occurs.
	 */
	public static void watchStateFiles(File stateFiles) {
		if (stateFiles == null || !stateFiles.exists() || !stateFiles.isDirectory()) {
			System.err.println("State dir does not exist or is not a directory: " + stateFiles);
			return;
		}

		stateFilesWatcher = new FileWatcher(stateFiles);

		stateFilesWatcher.addListener(new FileAdapter() {
			@Override
			public void onCreated(FileEvent event) {
				// System.out.println("State created in states dir");
				// todo building view thing
				EventQueue.invokeLater(() -> {
					stateFilesWatcher.listenerPerformAction++;
					File file = event.getFile();
					State.readState(file);
					stateFilesWatcher.listenerPerformAction--;
					if (Settings.DEV_MODE.enabled()) {
						State state = State.get(file);
						System.out.println("State was created/loaded: " + state);
					}
				});
			}

			@Override
			public void onModified(FileEvent event) {
				// System.out.println("State modified in states dir");
				// todo building view thing
				EventQueue.invokeLater(() -> {
					stateFilesWatcher.listenerPerformAction++;
					File file = event.getFile();
					State.readState(file);
					if (Settings.DEV_MODE.enabled()) {
						State state = State.get(file);
						System.out.println("State was modified: " + state);
					}
					stateFilesWatcher.listenerPerformAction--;
				});
			}

			@Override
			public void onDeleted(FileEvent event) {
				// System.out.println("State deleted in states dir");
				// todo building view thing
				EventQueue.invokeLater(() -> {
					stateFilesWatcher.listenerPerformAction++;
					File file = event.getFile();
					State.deleteState(file);
					stateFilesWatcher.listenerPerformAction--;
					if (Settings.DEV_MODE.enabled()) {
						State state = State.get(file);
						System.out.println("State was deleted: " + state);
					}
				});
			}
		}).watch();
	}

}
