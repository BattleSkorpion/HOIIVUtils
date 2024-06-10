package com.HOIIVUtils.ui.hoi4localization;

import com.HOIIVUtils.clauzewitz.HOIIVFile;
import com.HOIIVUtils.Settings;
import com.HOIIVUtils.clauzewitz.data.idea.FixIdea;
import com.HOIIVUtils.clauzewitz.data.idea.Idea;
import com.HOIIVUtils.clauzewitz.data.idea.IdeaFile;
import com.HOIIVUtils.clauzewitz.localization.Localization;
import com.HOIIVUtils.clauzewitz.localization.LocalizationFile;
import com.HOIIVUtils.clauzewitz.exceptions.IllegalLocalizationFileTypeException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import com.HOIIVUtils.ui.HOIIVUtilsStageLoader;
import com.HOIIVUtils.ui.javafx.table.TableViewWindow;
import com.HOIIVUtils.ui.message.MessageController;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class IdeaLocalizationWindow extends HOIIVUtilsStageLoader implements TableViewWindow {

    @FXML
    private Label numLocAddedLabel;
    @FXML
    private TextField ideaFileTextField;
    @FXML
    private Button ideaFileBrowseButton;
    @FXML
    private Label ideaFileNameLabel;
    @FXML
    private TextField ideaLocFileTextField;
    @FXML
    private Button ideaLocFileBrowseButton;
    @FXML
    private Button loadButton;
    @FXML
    private Button saveButton;
    @FXML
    private TableView<Idea> ideaListTable;
    @FXML
    private TableColumn<Idea, String> ideaIDColumn;
    @FXML
    private TableColumn<Idea, String> ideaNameColumn;

    private IdeaFile ideaFile;
    private LocalizationFile ideaLocFile;

    private final ObservableList<Idea> ideaObservableList;

    public IdeaLocalizationWindow() {
        setFxmlResource("IdeaLocalizationWindow.fxml");
        setTitle("HOIIVUtils Idea Localization");

        ideaObservableList = FXCollections.observableArrayList();
    }

    /**
     * Initializes the window.
     * 
     * This method is automatically called after the FXML file has been loaded.
     * It sets up the table view and sets the save button to be disabled.
     */
    @FXML
    void initialize() {
        // Set up the table view
        loadTableView(this, ideaListTable, ideaObservableList, Idea.getDataFunctions());

        // Set the save button to be disabled
        saveButton.setDisable(true);
    }

    public void handleIdeaFileBrowseButtonAction() {
        File initialIdeaDirectory = HOIIVFile.mod_ideas_folder;
        File selectedFile = openChooser(ideaFileBrowseButton, initialIdeaDirectory, false);
        if (Settings.DEV_MODE.enabled()) {
            System.out.println(selectedFile);
        }

        if (selectedFile != null) {
            ideaFileTextField.setText(selectedFile.getAbsolutePath());
            ideaFile = new IdeaFile(selectedFile.getAbsolutePath()); // todo?
            ideaFileNameLabel.setText(ideaFile.toString());
        } else {
            ideaFileNameLabel.setText("[not found]");
        }
    }

    public void handleIdeaLocFileBrowseButtonAction() {
        File initialIdeaLocDirectory = HOIIVFile.mod_localization_folder;
        File selectedFile = openChooser(ideaLocFileBrowseButton, initialIdeaLocDirectory, false);
        if (Settings.DEV_MODE.enabled()) {
            System.out.println(selectedFile);
        }
        if (selectedFile != null) {
            ideaLocFileTextField.setText(selectedFile.getAbsolutePath());
            try {
                ideaLocFile = new LocalizationFile(selectedFile);
            } catch (IllegalLocalizationFileTypeException e) {
                openError(e);
                return;
            }
            // ideaFile.setLocalization(ideaLocFile); // todo ?
            System.out.println("Set localization file of " + ideaFile + " to " + ideaLocFile);
        }
    }

    /**
     * Handles the action of the load button.
     * Loads the localization file of the idea tree into the idea tree.
     * If the localization file or idea tree is not properly initialized, it will
     * open an error
     * window.
     */
    public void handleLoadButtonAction() {
        if (ideaLocFile == null || ideaFile == null) {
            // Handle the case where ideaLocFile or ideaFile is not properly initialized
            MessageController window = new MessageController();
            window.open("Error: Idea localization or idea tree not properly initialized.");
            return;
        }

        /* load idea loc */
        try {
            // Load the localization file into the idea tree
            int numLocalizedIdeas = FixIdea.addIdeaLoc(ideaFile, ideaLocFile);
            // Update the number of localized ideas displayed on the window
            updateNumLocalizedIdeaes(numLocalizedIdeas);
        } catch (IOException e) {
            // If the file cannot be loaded, open an error window
            openError(e);
            return;
        }

        // Update the observable list of ideas
        updateObservableIdeaList();

        /* enable saving of localization */
        // After the localization file has been loaded, enable the save button
        saveButton.setDisable(false);
    }

    /**
     * Updates the idea observable list by clearing the list and adding all
     * ideas from the idea file.
     */
    private void updateObservableIdeaList() {
        // Clear the list
        ideaObservableList.clear();

        // Add all ideas from the idea file to the list
        ideaObservableList.addAll(ideaFile.listIdeas());
    }

    /**
     * Updates the idea observable list by replacing the idea with the given id with
     * the given idea.
     * If the idea is not found in the list, it will be added to the list.
     * 
     * @param idea the idea to update in the list
     */
    @SuppressWarnings("unused")
    private void updateObservableIdeaList(Idea idea) {
        if (idea == null) {
            System.out.println("Idea was null and therefore did not update idea list.");
            return;
        }

        // Find the index of the idea in the list
        int i = 0;
        Iterator<Idea> iterator = ideaObservableList.iterator();
        while (iterator.hasNext()) {
            Idea f = iterator.next();
            if (f.id().equals(idea.id())) {
                break; // found the idea
            }
            i++;
        }

        // Update the idea in the list
        if (iterator.hasNext()) {
            iterator.remove(); // Remove the current element using the iterator
            ideaObservableList.add(i, idea);
        } else {
            // Idea not found in list, add it
            ideaObservableList.add(idea);
        }
    }

    private void updateNumLocalizedIdeaes(int numLocalizedIdeaes) {
        numLocAddedLabel.setText(numLocAddedLabel.getText()
                .replace("x", String.valueOf(numLocalizedIdeaes)));
    }

    /**
     * Handles the action of the save button.
     * Saves the localization file.
     * If the localization file is not properly initialized, it will open an error
     * window.
     */
    public void handleSaveButtonAction() {
        if (ideaLocFile == null) {
            // Handle the case where ideaLocFile is not properly initialized
            MessageController window = new MessageController();
            window.open("Error: Idea localization file not properly initialized.");
            return;
        }

        try {
            // Saves the localization file
            ideaLocFile.writeLocalization();
        } catch (IOException e) {
            // If the file cannot be saved, open an error window
            openError(e);
        }
    }

    @Override
    public void setDataTableCellFactories() {
        /* column factory */
        // ideaDescColumn.setCellFactory(TextFieldTableCell.forTableColumn()); // This
        // makes the column editable

        /* row factory */
        ideaListTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Idea idea, boolean empty) {
                super.updateItem(idea, empty);

                if (idea == null || empty) {
                    setGraphic(null); // Clear any previous content
                } else {
                    Localization.Status textStatus = idea.getLocalization().status(); // todo
                    boolean hasStatusUpdated = textStatus == Localization.Status.UPDATED;
                    boolean hasStatusNew = textStatus == Localization.Status.NEW;

                    // Apply text style
                    if (hasStatusUpdated || hasStatusNew) {
                        setTextFill(Color.BLACK); // Set text color to black
                        setStyle("-fx-font-weight: bold; -fx-background-color: #328fa8;"); // Apply bold text using CSS
                    } else {
                        setTextFill(Color.BLACK); // Set text color to black
                        setStyle("-fx-background-color: transparent;"); // Reset style
                    }
                }
            }
        });
    }

}
