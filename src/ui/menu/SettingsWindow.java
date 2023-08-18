package ui.menu;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SettingsWindow extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("SettingsWindow.fxml"));
        primaryStage.setTitle("Settings");
        primaryStage.setScene((new Scene(root, 600, 400)));
        primaryStage.show();
        this.primaryStage = primaryStage;
    }

    public void launchSettingsWindow(String... var0) {
        super.launch(var0);
    }

     public Stage getStage() {
        return primaryStage;
    }
}