package com.hoi4utils.ui.statistics;

import com.hoi4utils.clausewitz.HOIIVUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import com.hoi4utils.ui.HOIIVUtilsWindow;

public class StatisticsController extends HOIIVUtilsWindow {

	@FXML
	public Label idVersion;
	@FXML
	public Label idWindowName;

	public StatisticsController() {
		setFxmlResource("Statistics.fxml");
		setTitle("HOIIVUtils Statistics");
	}

	@FXML
	void initialize() {
		includeVersion();
		idWindowName.setText("StatisticsWindow" + " WIP");
	}

	private void includeVersion() {
		idVersion.setText(HOIIVUtils.HOIIVUTILS_VERSION);
	}
}
