package ui.hoi4localization;

import java.io.File;
import java.io.IOException;

import hoi4utils.HOIIVFile;
import hoi4utils.HOIIVUtils;
import hoi4utils.Settings;
import hoi4utils.clausewitz_coding.focus.FixFocus;
import hoi4utils.clausewitz_coding.focus.Focus;
import hoi4utils.clausewitz_coding.focus.FocusTree;
import hoi4utils.clausewitz_coding.localization.FocusLocalizationFile;
import hoi4utils.clausewitz_coding.tooltip.CustomTooltip;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import ui.HOIUtilsWindow;
import ui.javafx.table.TableViewWindow;
import ui.message_popup.MessagePopupWindow;

public class FocusLocalizationWindow extends HOIUtilsWindow implements TableViewWindow {

    @FXML private Label numLocAddedLabel;
    @FXML private TextField focusTreeFileTextField;
    @FXML private Button focusTreeFileBrowseButton;
    @FXML private Label focusTreeNameLabel;
    @FXML private TextField focusLocFileTextField;
    @FXML private Button focusLocFileBrowseButton;
    @FXML private Button loadButton;
    @FXML private TableView<Focus> focusListTable;
    @FXML private TableColumn<Focus, String> focusIDColumn;
    @FXML private TableColumn<Focus, String> focusNameColumn;
    @FXML private TableColumn<Focus, String> focusDescColumn;
    private FocusTree focusTree;
    private FocusLocalizationFile focusLocFile;

    private final ObservableList<Focus> focusObservableList;

    public FocusLocalizationWindow() {
        /* window */
        setFxmlResource("FocusLocalizationWindow.fxml");
        setTitle("HOIIVUtils Focus Localization");

        focusObservableList = FXCollections.observableArrayList();
    }

    /**
     * {@inheritDoc}
     *
     */
    @FXML
    void initialize() {
        /* table */
        loadTableView(this, focusListTable, focusObservableList, Focus.getDataFunctions());
    }

    public void handleFocusTreeFileBrowseButtonAction() {
        File initialFocusDirectory = HOIIVFile.focus_folder;
        File selectedFile = HOIUtilsWindow.openChooser(focusTreeFileBrowseButton, false, initialFocusDirectory);
        if (Settings.DEV_MODE.enabled()) {
            System.out.println(selectedFile);
        }
        if (selectedFile != null) {
            focusTreeFileTextField.setText(selectedFile.getAbsolutePath());
            focusTree = new FocusTree(selectedFile);
        }
    }

    public void handleFocusLocFileBrowseButtonAction() {
        File initialFocusLocDirectory = HOIIVFile.localization_eng_folder;
        File selectedFile = HOIUtilsWindow.openChooser(focusLocFileBrowseButton, false, initialFocusLocDirectory);
        if (Settings.DEV_MODE.enabled()) {
            System.out.println(selectedFile);
        }
        if (selectedFile != null) {
            focusLocFileTextField.setText(selectedFile.getAbsolutePath());
            focusLocFile = new FocusLocalizationFile(selectedFile);
        }
    }

    public void handleLoadButtonAction() {
        if (focusLocFile == null || focusTree == null) {
            // Handle the case where focusLocFile or focusTree is not properly initialized
            MessagePopupWindow window = new MessagePopupWindow();
            window.open("Error: Focus localization or focus tree not properly initialized.");
        }
		// Add further handling logic here // todo remove da comment when done

        // todo temp lazy flow control
        if (focusLocFile != null && focusTree != null) {
//            for (Focus focus : focusTree) {     // focusTree make implement Iterable or whatever
//                focusTree.setLocalization()
//            }

            /* load focus loc */
            try {
                int numLocalizedFocuses = FixFocus.addFocusLoc(focusTree, focusLocFile);
                // todo didnt happe?
                numLocAddedLabel.setText(numLocAddedLabel.getText()
                        .replace("x", String.valueOf(numLocalizedFocuses)));
            } catch (IOException e) {
                openError(e);
                return;
            }
        }

        focusObservableList.clear();
        focusObservableList.addAll(focusTree.listFocuses());
    }

    @Override
    public void setDataTableCellFactories() {
        // none necessary
    }
}
