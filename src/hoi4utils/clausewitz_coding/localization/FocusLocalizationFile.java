package hoi4utils.clausewitz_coding.localization;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.IOException;
import ui.HOIUtilsWindow;
/**
 * This is the FocusLocalization file.
 */
public class FocusLocalizationFile extends LocalizationFile {

	protected List<Localization[]> focusLocalizationList;

	public FocusLocalizationFile(File file) throws IllegalArgumentException {
		super(file);

		focusLocalizationList = new ArrayList<>();
	}


	public String toString() {
		if (super.isFile()) {
			return super.toString();
		}
		return super.toString();
	}

	public void writeLocalization() {
		/* load file data, before writer init so data not disappeared */
		StringBuilder fileBuffer;
		try {
			Scanner scanner = new Scanner(this);
			fileBuffer = new StringBuilder();
			while (scanner.hasNextLine()) {
				fileBuffer.append(scanner.nextLine()).append(System.lineSeparator());
			}
			scanner.close();

//	  System.out.println(fileBuffer);

//	  FileWriter writer = new FileWriter(this, false);		// true = append
			FileWriter writer = new FileWriter(this, false);
			BufferedWriter BWriter = new BufferedWriter(writer);
			PrintWriter PWriter = new PrintWriter(BWriter);				// for println syntax

//	  PWriter.println(language);

			for (Localization[] localization : focusLocalizationList) {
				if (localization[0].status() == Localization.Status.UPDATED) {
					/* replace loc */
					int start = fileBuffer.indexOf(localization[0].ID());
					if (start < 0) {
						System.err.println("Start of localization id is negative!");
					}
					int temp = fileBuffer.indexOf("\"", start);
					int end = 1;
					// end char must be literally " and not \"
					do {
						end = fileBuffer.indexOf("\"", temp + 1);
					} while (fileBuffer.charAt(end - 1) == '\\');
					if (end < 0) {
						System.err.println("end of localization id is negative!");
					}

					String loc = localization[0].toString();
					loc = loc.replaceAll("§", "Â§");		// necessary with UTF-8 BOM
					fileBuffer.replace(start, end + 1, loc);
					System.out.println("replaced " + localization[0].ID());
				} else if (localization[0].status() == Localization.Status.NEW) {
					/* append loc */
					String loc = localization[0].toString();
					loc = loc.replaceAll("§", "Â§");		// necessary with UTF-8 BOM
					fileBuffer.append("\t").append(loc).append(System.lineSeparator());
					System.out.println("append " + localization[0].ID());
				} else {

				}

				if (localization[1].status() == Localization.Status.UPDATED) {
					/* replace loc */
					int start = fileBuffer.indexOf(localization[1].ID());
					if (start < 0) {
						System.err.println("Start of localization id is negative!");
					}
					int temp = fileBuffer.indexOf("\"", start);
					int end = 1;
					// end char must be literally " and not \"
					do {
						end = fileBuffer.indexOf("\"", temp + 1);
					} while (fileBuffer.charAt(end - 1) == '\\');
					if (end < 0) {
						System.err.println("end of localization id is negative!");
					}

					String loc = localization[1].toString();
					loc = loc.replaceAll("§", "Â§");		// necessary with UTF-8 BOM
					fileBuffer.replace(start, end + 1, loc);
					System.out.println("replaced " + localization[1].ID());
				} else if (localization[1].status() == Localization.Status.NEW) {
					/* append loc */
					String loc = localization[1].toString();
					loc = loc.replaceAll("§", "Â§");		// necessary with UTF-8 BOM
					fileBuffer.append("\t").append(loc).append(System.lineSeparator());
					System.out.println("append " + localization[1].ID());
				} else {

				}
			}

			/* print */
			PWriter.print(fileBuffer);
			PWriter.close();
		}
		catch (Exception exception) {
			HOIUtilsWindow.openError(exception);
		}
	}

	public void addLocalization(Localization newLocalization, Localization newLocalizationDesc) {
		super.addLocalization(newLocalization);

		Localization[] list = {newLocalization, newLocalizationDesc};
		focusLocalizationList.add(list);
	}

	/**
	 * Gets localization from this file corresponding to the ID
	 * @param ID
	 * @return Localization corresponding with ID, or null if not found
	 */
	public Localization getLocalization(String ID) {
		if(ID == null) {
			System.err.println("Null localization ID in " + this);
			return null;
		}

		for (Localization loc : localizationList) {
			if (loc.ID().equals(ID)) {
				return loc;
			}
			System.out.println(loc.ID());
		}

		return null;
	}

	@Override
	public Localization setLocalization(String key, String text) {
		ArrayList<Localization> listTemp = new ArrayList<>(localizationList);

		for (Localization[] loc : focusLocalizationList) {
			if (loc[0].ID().equals(key)) {
				Localization newLoc = new Localization(key, text, Localization.Status.UPDATED);
				Localization[] newLocList = new Localization[]{newLoc, loc[1]};


				listTemp.remove(loc[0]);	  // record is final
				listTemp.add(newLoc);
				localizationList = listTemp;

				focusLocalizationList.remove(loc);
				focusLocalizationList.add(newLocList);
				return newLoc;
			}
		}

		Localization newLoc = new Localization(key, text, Localization.Status.NEW);
		Localization newLocDesc = new Localization(key + "_desc", "[null]", Localization.Status.NEW);
		Localization[] newLocList = new Localization[]{newLoc, newLocDesc};

		listTemp.add(newLoc);
		localizationList = listTemp;

		focusLocalizationList.add(newLocList);

		return newLoc;
	}

	public Localization setLocalizationDesc(String key, String text) {
		for (Localization[] loc : focusLocalizationList) {
			if (loc[1].ID().equals(key)) {
				focusLocalizationList.remove(loc);
				Localization localization = new Localization(key, text, Localization.Status.UPDATED);
				focusLocalizationList.add(new Localization[]{loc[0], localization});
				return localization;
			}
		}
		return null;
	}

}
