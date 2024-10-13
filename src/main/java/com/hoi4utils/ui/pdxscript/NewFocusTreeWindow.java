package com.hoi4utils.ui.pdxscript;

import com.hoi4utils.clausewitz.data.country.CountryTag;
import com.hoi4utils.clausewitz.data.country.CountryTag$;
import com.hoi4utils.clausewitz.data.focus.FocusTree;
import com.hoi4utils.ui.HOIIVUtilsWindow;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import scala.jdk.javaapi.CollectionConverters;

import javax.swing.*;
import java.io.File;
import java.util.function.Consumer;

import static com.hoi4utils.Settings.MOD_PATH;

public class NewFocusTreeWindow extends HOIIVUtilsWindow {

    @FXML
    AnchorPane rootAnchorPane;
    @FXML
    TextField idTextField;
    @FXML
    ComboBox<CountryTag> countryTagComboBox;
    @FXML
    Button cancelButton;
    @FXML
    Button createFocusTreeButton;

    private Consumer<FocusTree> onCreate = null;

    public NewFocusTreeWindow() {
        setFxmlResource("NewFocusTreeWindow.fxml");
        setTitle("New Focus Tree");
    }

    @FXML
    void initialize() {
        countryTagComboBox.getItems().addAll(CollectionConverters.asJava(CountryTag$.MODULE$.toList()));
    }

    @FXML
    void onCreate() {
        String id = idTextField.getText();
        CountryTag countryTag = countryTagComboBox.getValue();
        if (id.isEmpty() || countryTag == null) {
            JOptionPane.showMessageDialog(null, "Please fill out all fields", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        File focusFile = new File(MOD_PATH + "/common/national_focus/" + id + "_" + "temp_HOIIVUtils"+ ".txt");
        FocusTree focusTree = new FocusTree();
        focusTree.setID(id);
        focusTree.setCountryTag(countryTag);
        focusTree.setFile(focusFile);
        if (onCreate != null) {
            onCreate.accept(focusTree);
        }
        closeWindow(createFocusTreeButton);
    }

    @FXML
    void onCancel() {
        closeWindow(cancelButton);
    }

    public void setOnCreateConsumerAction(Consumer<FocusTree> onCreate) {
       this.onCreate = onCreate;
    }
}
