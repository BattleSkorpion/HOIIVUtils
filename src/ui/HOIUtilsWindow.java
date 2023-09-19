package ui;

import hoi4utils.Settings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

public abstract class HOIUtilsWindow implements FXWindow {
	private String fxmlResource;
	private String title = "HOIIVUtils (default title)";
	private String styleSheetURL = "resources/javafx_dark.css";
	/**
	 * Allows windows to get the controller
	 */
	protected FXMLLoader loader;
	
	Stage stage;

	/**
	 * Opens the window
	 */
	@Override
	public void open() {
		try {
			if (stage != null) {
				stage.show();
			} else if (fxmlResource == null) {
				openError("FXML Resource does not exist, Window Title: " + title);
			} else {
				loader = new FXMLLoader(getClass()
						.getResource(fxmlResource));

				Stage launchStage = new Stage();
				Scene scene = new Scene(loader.load());
				launchStage.setScene(scene);
				launchStage.setTitle(title);
				

				/* style */
				if (Settings.DEV_MODE.enabled()) {
					System.out.println("use stylesheet: " + new File(styleSheetURL).getAbsolutePath());
				}
				scene.getStylesheets().add(styleSheetURL);

				decideScreen(launchStage);
				launchStage.show();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Opens window and updates fxmlResource and title
	 * @param fxmlResource window .fxml resource
	 * @param title window title
	 */
	@Override
	public void open(String fxmlResource, String title) {
		this.fxmlResource = fxmlResource;
		this.title = title;
	}

	@Override
	public String getFxmlResource() {
		return fxmlResource;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getStyleSheetURL() {
		return styleSheetURL;
	}

	@Override
	public void setFxmlResource(String fxmlResource) {
		this.fxmlResource = fxmlResource;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public void setStyleSheetURL(String styleSheetURL) {
		this.styleSheetURL = styleSheetURL;
	}

}
