package ui.main_menu;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import hoi4utils.Settings;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.control.Button;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import hoi4utils.HOIIVUtils;
import hoi4utils.SettingsManager;

import static hoi4utils.Settings.MOD_PATH;

public class SettingsWindowController {
	
	HashMap<Settings, String> tempSettings;

	@FXML
	public Pane idPane;
	public Label idVersionLabel;
	public TextField idModPathTextField;
	public Label idHOIIVModFolderLabel;
	public Button idBrowseButton;
	public CheckBox idDevModeCheckBox;
	public Button idOkButton;

	public File selectedDirectory;
	
	/* Constructor */
	public SettingsWindowController() {
		tempSettings = new HashMap<>();
	}

	@FXML
	void initialize() {
		includeVersion();
		setupFirstTime();
	}

	private void includeVersion() {
		idVersionLabel.setText(HOIIVUtils.hoi4utilsVersion);
	}
	private void setupFirstTime() {
		boolean isFirstTime = HOIIVUtils.firstTimeSetup;
		if (!isFirstTime) {
			setModPathTextFieldFromSettings();
			setDevModeCheckBoxOnOrOff();
			enableOkButton();
		}
	}
	private void setModPathTextFieldFromSettings() {
		String inlcudeSetting = (String) MOD_PATH.getSetting();
		if (inlcudeSetting != "null") {
			idModPathTextField.setText(inlcudeSetting);
		}
	}
	private void setDevModeCheckBoxOnOrOff() {
		boolean getDevModeSetting = Settings.DEV_MODE.enabled();
		idDevModeCheckBox.setSelected(getDevModeSetting);
	}
	private void enableOkButton() {
		idOkButton.setDisable(false);
	}
	private void disableOkButton() {
		idOkButton.setDisable(true);
	}

	public void updateTempSetting(Settings setting, Object property) {
		tempSettings.put(setting, String.valueOf(property));
	}
	public void handleDevModeCheckBoxAction() {
		updateTempSetting(Settings.DEV_MODE, idDevModeCheckBox.isSelected());
	}
	
	
	public void handleBrowseAction() {
			getDirectoryChooser();
			idModPathTextField.setText(selectedDirectory.getAbsolutePath());
			updateModPath(selectedDirectory);
	}

	private void getDirectoryChooser() {
		// Opens Windows Default Directory Chooser
		Stage primaryStage = (Stage) (idBrowseButton.getScene().getWindow());
		DirectoryChooser directoryChooser = new DirectoryChooser();
		selectedDirectory = directoryChooser.showDialog(primaryStage);
		if (selectedDirectory == null) {
			return;
		}
	}

	public void handleModPathTextField() {
		getIsDirectory();

		String pathText = idModPathTextField.getText();
		if (pathText == null || pathText.isEmpty()) {
			pathText = null;
		}
		tempSettings.put(MOD_PATH, pathText);
	}

	private void updateModPath(File selectedDirectory) {
		getIsDirectory();
		tempSettings.put(MOD_PATH, selectedDirectory.getAbsolutePath());
	}

	private void getIsDirectory() {
		File fileModPath = new File(idModPathTextField.getText());

		boolean exists = fileModPath.exists();

		boolean isDirectory = fileModPath.isDirectory();
				
		if (idOkButton.isDisabled() && exists && isDirectory) {
			disableOkButton();
		} else {
			enableOkButton();
		}
	}

	public void handleOkButtonAction() {
		boolean settingsSaved = updateSettings();
		if (!settingsSaved) {
			return;
		}
		hideCurrentWindow();
		openMenuWindow();
	}
	private boolean updateSettings() {
		try {
			if (HOIIVUtils.firstTimeSetup) {
				HOIIVUtils.settings = new SettingsManager(tempSettings);
			} else {
				SettingsManager.saveSettings(tempSettings);
			}
		} catch (IOException exception) {
			HOIIVUtils.openError("Settings failed to save.");
			return false;
		}
		createHOIIVFilePaths();
		return true;
	}
	private void hideCurrentWindow() {
		HOIIVUtils.hideWindow(idPane);
	}
	private void openMenuWindow() {
		MenuWindow menuWindow = new MenuWindow();
		menuWindow.open();
	}
	private void createHOIIVFilePaths() {
		String modPath = SettingsManager.get(MOD_PATH);
		System.out.println(modPath);
		HOIIVUtils.states_folder = new File(modPath + "\\history\\states");
		HOIIVUtils.strat_region_dir =  new File(modPath + "\\map\\strategicregions");
		HOIIVUtils.localization_eng_folder =  new File(modPath + "\\localisation\\english");
		HOIIVUtils.focus_folder = new File(modPath + "\\common\\national_focus");
	}
}
