package com.hoi4utils.ui.hoi4localization;

import com.hoi4utils.clausewitz.HOIIVFile;
import com.hoi4utils.clausewitz.HOIIVUtils;

import com.hoi4utils.clausewitz.data.tooltip.CustomTooltip;

import com.hoi4utils.ui.FXWindow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.hoi4utils.ui.HOIIVUtilsWindow;
import com.hoi4utils.ui.javafx.table.TableViewWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * todo: have to redo some functionality to work with new localization system
 */
public class CustomTooltipWindow extends HOIIVUtilsWindow implements TableViewWindow {

	@FXML
	public Label idVersion;
	@FXML
	TableColumn<CustomTooltip, String> tooltipIdTableColumn;
	@FXML
	TableColumn<CustomTooltip, String> tooltipTextTableColumn;
	@FXML
	ComboBox<File> tooltipFileComboBox;
	@FXML
	ComboBox<File> tooltipLocalizationFileComboBox;
	@FXML
	Button tooltipFileBrowseButton;
	@FXML
	Button tooltipLocalizationFileBrowseButton;
	@FXML
	TableView<CustomTooltip> customTooltipTableView;
	private File tooltipFile;

	private ObservableList<CustomTooltip> customTooltipList;

	public CustomTooltipWindow() {
		/* window */
		setFxmlResource("CustomTooltipWindow.fxml");
		setTitle("HOIIVUtils Custom ToolTip Localization Window");

		customTooltipList = FXCollections.observableArrayList();
	}

	/**
	 * {@inheritDoc}
	 *
	 */
	@FXML
	void initialize() {
		includeVersion();

		/* table */
		loadTableView(this, customTooltipTableView, customTooltipList, CustomTooltip.getDataFunctions());
	}

	private void includeVersion() {
		idVersion.setText(HOIIVUtils.HOIIVUTILS_VERSION);
	}

	@Override
	public void setDataTableCellFactories() {
	}

	/**
	 * @param customTooltipList
	 */
	@Deprecated
	public void setCustomTooltipList(ObservableList<CustomTooltip> customTooltipList) {
		this.customTooltipList = customTooltipList;
	}

	/**
	 * @param customTooltips
	 */
	@Deprecated
	public void setCustomTooltipList(Collection<CustomTooltip> customTooltips) {
		this.customTooltipList = FXCollections.observableArrayList();
		this.customTooltipList.addAll(customTooltips);
	}

	public void updateCustomTooltipList(Collection<CustomTooltip> customTooltips) {
		this.customTooltipList.clear();
		this.customTooltipList.addAll(customTooltips);
	}

	public void addCustomTooltips(Collection<CustomTooltip> customTooltips) {
		this.customTooltipList.addAll(customTooltips);
	}

	/* action handlers */
	public void handleTooltipFileBrowseAction() {
		File initialFocusDirectory = HOIIVFile.mod_common_folder;
		File selectedFile = FXWindow.openChooser(tooltipFileBrowseButton, initialFocusDirectory, false);
		if (HOIIVUtils.getBoolean("dev.mode")) {
			System.out.println(selectedFile);
		}
		if (selectedFile != null) {
			tooltipFileComboBox.setValue(selectedFile); // selectedFile.getAbsolutePath()
			tooltipFile = selectedFile;
		}

		/* load custom tooltips from selected file */
		CustomTooltip.loadTooltips(tooltipFile);
		ArrayList<CustomTooltip> tooltips = CustomTooltip.getTooltips();
		if (tooltips == null) {
			System.out.println("No custom tooltips found");
			return;
		}
		updateCustomTooltipList(tooltips);
	}

	public void handleTooltipLocalizationFileBrowseAction() {
//		File initialFocusLocDirectory = HOIIVFile.mod_localization_folder;
//		File selectedFile = openChooser(tooltipLocalizationFileBrowseButton, initialFocusLocDirectory, false);
//		if (HOIIVUtils.getBoolean("dev.mode")) {
//			System.out.println(selectedFile);
//		}
//		if (selectedFile != null) {
//			tooltipLocalizationFileComboBox.setValue(selectedFile); // selectedFile.getAbsolutePath()
//			try {
//				localizationFile = new LocalizationFile(selectedFile);
//			} catch (IllegalLocalizationFileTypeException e) {
//				e.printStackTrace();
//				return;
//			}
//		}
//
//		/* load custom tooltip associated localization from selected file */
//		if (tooltipFile == null) {
//			return;
//		}
//		localizationFile.read();
//
//		for (CustomTooltip tooltip : customTooltipList) {
//			String tooltipID = tooltip.id();
//			Localization tooltipLocalization = localizationFile.getLocalization(tooltipID);
//			if (tooltipLocalization == null) {
//				return;
//			}
//
//			tooltip.setLocalization(tooltipLocalization);
//		}
//
//		customTooltipTableView.refresh(); // this is important to ensure the table is refreshed when the list data
//											// is modified, otherwise the localization won't appear in the table.
	}
}
