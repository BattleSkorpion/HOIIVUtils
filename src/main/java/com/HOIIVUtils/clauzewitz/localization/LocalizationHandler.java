package com.HOIIVUtils.clauzewitz.localization;

import com.HOIIVUtils.clauzewitz.HOIIVFile;
import com.HOIIVUtils.clauzewitz.exceptions.IllegalLocalizationFileTypeException;

import java.beans.PropertyChangeListenerProxy;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LocalizationHandler {
    public static final String LOCALIZATION_FILE_EXTENSION = ".yml";
    private List<LocalizationFile> locFiles;

    private LocalizationHandler() {
        locFiles = new ArrayList<>();
        addLocalizationFolderChangeListener();
    }

    private static final class InstanceHolder {
        private static final LocalizationHandler instance = new LocalizationHandler();
    }

    public static LocalizationHandler getInstance() {
        return InstanceHolder.instance;
    }

    private void readLocalization(File localizationFolder) {
        if (!localizationFolder.exists() || !localizationFolder.isDirectory()) {
            System.err.println("Localization folder is not a directory or does not exist: " + localizationFolder.getAbsolutePath());
            return;
        }

        File[] files = localizationFolder.listFiles((dir, name) -> name.endsWith(LOCALIZATION_FILE_EXTENSION));
        if (files == null) {
            System.err.println("Localization folder is not a directory: " + localizationFolder.getAbsolutePath());
            return;
        }

        for (File file : files) {
            try {
                LocalizationFile localizationFile = new LocalizationFile(file);
                localizationFile.read();
                //List<Localization> localizations = localizationFile.localizationList;
                locFiles.add(localizationFile);
            } catch (IllegalLocalizationFileTypeException e) {
                System.err.println(
                        "Possibly invalid localization file type: " + file.getName());
            }
        }
    }

    private void addLocalizationFolderChangeListener() {
        HOIIVFile.addPropertyChangeListener(new PropertyChangeListenerProxy(HOIIVFile.mod_localization_folder_field_name, (evt) -> {
            File localizationFolder = (File) evt.getNewValue();
            if (localizationFolder != null) {
                readLocalization(localizationFolder);
            }
        }));
    }

    /**
     * Adds localization for each localizable object in the list for which
     * correct localization data is found for.
     * @param list list of localizable objects
     * @param regex regex pattern to filter localization keys
     * @param regexArgs arguments to replace the regex patterns in the regex
     * @param unique if true, each localization key can be associated with only one
     *               Localizable object.
     */
    public void addLocalization(List<? extends Localizable> list, String regex, boolean unique, Object... regexArgs) {
        // first filter with the base regex to narrow down fthe possible localization
        List<Localization> potentialLocalization = new ArrayList<>();
        for (LocalizationFile f : locFiles) {
            //potentialLocalization.addAll(f.filter(regex));
        }

        // now pattern match (replace any regex patterns with the args)
        for (Localizable obj : list) {
            for (Localization loc : potentialLocalization) {

            }
        }
    }

//    public String getLocalizedString(String key) {
//        return localizationData.getOrDefault(key, key);
//    }
//
//    public Map<String, String> getAllLocalizationData() {
//        return new HashMap<>(localizationData);
//    }

}

