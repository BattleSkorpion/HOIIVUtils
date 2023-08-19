package clausewitz_coding.focus;

import clausewitz_coding.country.CountryTags;
import hoi4utils.HOIIVUtils;
import clausewitz_coding.localization.FocusLocalizationFile;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
/*
 * FixFoucus Documentation
 */
public class FixFocus extends HOIIVUtils {



	public static boolean addFocusLoc(File focus_file, File loc_file) throws IOException {

		//ArrayList<String> focuses_nonlocalized = new ArrayList<String>();
		FocusTree focusTree = new FocusTree(focus_file);
		FocusLocalizationFile localization = new FocusLocalizationFile(loc_file);
		localization.readLocalization();

		/* open the ui */
//		FocusTreeLocProgress focusLocProgress = new FocusTreeLocProgress(focusTree);
//		focusLocProgress.setVisible(true);
		// todo

		String focus_loc;

		ArrayList<Focus> focusesUnloc = new ArrayList<>();
		assert focusTree.listFocusNames() != null;
		for (Focus focus : focusTree.focuses())
		{
			// if focus id not localized
			if (!localization.isLocalized(focus.id))
			{ 
				// write to loc file 
				// separate words in focus name
				int i = 0;	//counter
				if (CountryTags.exists(focus.id().substring(0, 3))) {
					i += 3;
				}

				// localize focus name
				focus_loc = titleCapitalize(focus.id().substring(i).replaceAll("_+", " ").trim()); // regex

				// set focus loc
				focus.setFocusLoc(focus_loc);

			
				focusesUnloc.add(focus);

				localization.setLocalization(focus.id, focus_loc);
				localization.setLocalizationDesc(focus.id + "_desc", "added focus on " + LocalDateTime.now() + " by hoi4localizer.");
			}
		}

		/* ui */
//		focusLocProgress.incrementProgressBar();
//		focusLocProgress.setNumFocusesUnloc(numFocusesUnloc);
//		focusLocProgress.refreshUnlocFocusesTable(focusesUnloc);

		localization.writeLocalization();
		return true; 
	}

	// useful lines function

}
