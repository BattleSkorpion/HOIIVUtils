package ui.menu;

import java.io.File;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import settings.HOIIVUtilsProperties;
import hoi4utils.HOIIVUtils;

public class SettingsWindow extends Application {
    Stage primaryStage;
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("SettingsWindow.fxml"));
        primaryStage.setTitle("Settings");
        primaryStage.setScene((new Scene(root, 600, 400)));
        primaryStage.show();
        this.primaryStage = primaryStage;

        /* settings */
        String user_docs_path = System.getProperty("user.home") + File.separator + "Documents";
        String hoi4UtilsPropertiesPath = user_docs_path + File.separator + "hoi4utils.HOIIVUtils";
        if (new File(hoi4UtilsPropertiesPath).exists()) {
            /* standard setup */
            HOIIVUtils.firstTimeSetup = false;
            HOIIVUtils.settings = new HOIIVUtilsProperties();

            HOIIVUtils.decideScreen(primaryStage);
        } else {
            /* first-time setup */
            HOIIVUtils.firstTimeSetup = true;
        }
    }

    public void launchSettingsWindow(String... var0) {
        super.launch(var0);
    }

    public void open() {primaryStage.show(); }
}