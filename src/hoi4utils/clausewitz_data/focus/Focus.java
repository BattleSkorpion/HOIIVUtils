package hoi4utils.clausewitz_data.focus;

import hoi4utils.Settings;
import hoi4utils.clausewitz_data.Localizable;
import hoi4utils.clausewitz_code.trigger.Trigger;
import hoi4utils.clausewitz_data.gfx.Interface;
import hoi4utils.clausewitz_data.localization.FocusLocalizationFile;
import hoi4utils.clausewitz_data.localization.Localization;
import hoi4utils.clausewitz_parser_deprecated.Expression;
import clausewitz_parser.Node;
import hoi4utils.ddsreader.DDSReader;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx_utils.JavaFXImageUtils;
import org.jetbrains.annotations.Range;
import ui.FXWindow;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/*
 * Focus just Focus
 */
public class Focus implements Localizable {
	private static final int FOCUS_COST_FACTOR = 7;
	private final int DEFAULT_FOCUS_COST = 10; // default cost (in weeks by default) when making a new focus.
	private static final HashSet<String> focusIDs = new HashSet<>();

	/* attributes */
	protected FocusTree focusTree;
	Expression focusExp;
	protected SimpleStringProperty id;
	protected Localization nameLocalization;
	protected Localization descLocalization;
	protected SimpleStringProperty icon;
	protected Image ddsImage;
	protected Set<Set<Focus>> prerequisite; // can be multiple, but hoi4 code is simply "prerequisite"
	protected Set<Focus> mutually_exclusive;
	protected Trigger available;
	protected int x; // if relative, relative x
	protected int y; // if relative, relative y
	protected String relative_position_id; // if null, position is not relative
	protected double cost; // cost of focus (typically in weeks unless changed in defines)
	protected Set<FocusSearchFilter> focus_search_filters;
	protected boolean available_if_capitulated;
	protected boolean cancel_if_invalid;
	protected boolean continue_if_invalid;
	// private AIWillDo ai_will_do; // todo
	// select effect
	// completion award

	/* variables */

	/**
	 * Set of the focus id's this focus is still attempting to reference (but may not be loaded/created yet).
	 */
//	private final Map<String, Consumer<List<Node>>> pendingFocusReferences = new HashMap<>();
	private final PendingFocusReferenceList pendingFocusReferences
			= new PendingFocusReferenceList();

	public Focus(String focus_id, FocusTree focusTree) {
		if (focusIDs.contains(focus_id)) {
			System.err.println("Error: focus id " + focus_id + " already exists."); // todo throw exception instead?
			return;
		}
		this.id = new SimpleStringProperty(focus_id);
		this.setNameLocalization();

		this.focusTree = focusTree;
		focusIDs.add(focus_id);
	}

	public Focus(String focusId, FocusTree focusTree, Node node) {
		this(focusId, focusTree);

		loadAttributes(node);
	}

	public static List<Function<Focus,?>> getDataFunctions() {
		List<Function<Focus, ?>> dataFunctions = new ArrayList<>(3);         // for optimization, limited number of data functions.

		dataFunctions.add(Focus::id);
		dataFunctions.add(Focus::nameLocalization);
		dataFunctions.add(Focus::descLocalization);

		return dataFunctions;
	}

	public String id() {
		if (id == null) {
			return null;
		}
		return id.get();
	}

	public SimpleStringProperty idProperty() { return id; }

	/**
	 * if relative, relative x
	 * 
	 * @return
	 */
	public int x() {
		return x;
	}

	/**
	 * if relative, relative y
	 * 
	 * @return
	 */
	public int y() {
		return y;
	}

	public int absoluteX() {
		if (relative_position_id == null) {
			return x;
		} else {
			return (int) absolutePosition().getX();
		}
	}

	public int absoluteY() {
		if (relative_position_id == null) {
			return y;
		} else {
			return (int) absolutePosition().getY();
		}
	}

	/**
	 * if relative, relative position
	 * 
	 * @return point representing xy location, or relative xy if relative.
	 */
	public Point2D position() {
		return new Point2D(x, y);
	}

	/**
	 * Absolute focus xy-position.
	 *
	 * @return Point representing absolute position of focus.
	 * @implNote Should only be called after all focuses in focus tree are
	 * instantiated.
	 */
	public Point2D absolutePosition() {
		if (relative_position_id == null) {
			return position();
		}
		if (relative_position_id.equals(this.id())) {
			System.err.println("Relative position id same as focus id for " + this);        // todo not an error of this program necessarily, issue should be handled differently?
			return position();
		}

		Focus relative_position_focus = focusTree.getFocus(relative_position_id);
		if (relative_position_focus == null) {
			System.err.println("focus id " + relative_position_id + " not a focus");
			return position();
		}
		Point2D adjPoint = relative_position_focus.absolutePosition();
		adjPoint = new Point2D(adjPoint.getX() + x, adjPoint.getY() + y);
		// System.out.println(adjPoint + ", " + id + ", " + relative_position_focus.id +
		// ", " + relative_position_focus.position());

		return adjPoint;
	}

	public SimpleStringProperty icon() {
		return icon;
	}

	public String toString() {
		return id();
	}

	/**
	 * Adds focus attributes (prerequisite, mutually exclusive, etc...) to focus
	 * by parsing expressions for each potential attribute.
	 * 
	 * @param exp Node representing focus - must include "focus".
	 */
	public void loadAttributes(Node exp) {
		if (!exp.name.equals("focus")) {
			System.err.println(this + " - Not valid focus expression/definition.");
			return;
		}

//		focusExp = exp.get("focus=");

		setID(exp.getValue("id").string());
		setXY(exp.getValue("x").integer(), exp.getValue("y").integer());
		setRelativePositionID(exp.getValue("relative_position_id").string()); // todo need Optional for easier null handling probably
		// setFocusLoc();
		setIcon(exp.getValue("icon").string());
		setCost(exp.getValue("cost").rational());
		setPrerequisite(exp.filterName("prerequisite").toList());
		setMutuallyExclusive(exp.filterName("mutually_exclusive").toList());
		setAvailable(exp.findFirst("available"));
	}

	public Expression getFocusExpression() {
		return focusExp;
	}

	public void setID(String id) {
		this.id.set(id);
	}

	private void setID(Expression exp) {
		if (exp == null) {
			id = null;
			FXWindow.openGlobalErrorWindow("Expression was null for setting focus ID.");
			return;
		}

		id = new SimpleStringProperty(exp.getText());
	}

	/**
	 * Sets new xy-coordinates, returns previous xy-coordinates.
	 * 
	 * @param x focus new x-coordinate
	 * @param y focus new y-coordinate
	 * @return previous x and y
	 */
	public Point setXY(int x, int y) {
		Point prev = new Point(this.x, this.y);
		this.x = x;
		this.y = y;
		return prev;
	}

	public Point setXY(Point xy) {
		return setXY(xy.x, xy.y);
	}

	private Point setXY(Expression x, Expression y) {
		if (x == null && y == null) {
			return setXY(0, 0);
		}
		if (x == null) {
			return setXY(0, y.getValue());
		}
		if (y == null) {
			return setXY(x.getValue(), 0);
		}
		return setXY(x.getValue(), y.getValue());
	}

	private void setRelativePositionID(String exp) {
		if (exp == null) {
			relative_position_id = null; // perfectly acceptable
			return;
		}
		relative_position_id = exp;
	}

	public void setCost() {
		setCost(DEFAULT_FOCUS_COST);
	}

	public void setCost(Number cost) {
		this.cost = cost.doubleValue();
	}

	public void setCost(Expression exp) {
		if (exp == null) {
			cost = 0;
			return;
		}

		this.cost = exp.getValue();
	}

	public String nameLocalization() {
		if (nameLocalization == null) {
			return "[null]";
		}
		return nameLocalization.text();
	}


	/**
	 * Default method for setting localization name of focus. Sets the localization name
	 * to the focus id.
	 */
	public void setNameLocalization() {
		setNameLocalization(id(), Localization.Status.DEFAULT);
	}
	public void setNameLocalization(Localization localization) {
		nameLocalization = localization;
	}

	/**
	 * Sets name localization and decides the status.
	 * @param text
	 */
	public void setNameLocalization(String text) {
		if (nameLocalization == null) {
			setNameLocalization(text, Localization.Status.DEFAULT);
			return;
		}

		Localization.Status status;

		if (nameLocalization.status() == Localization.Status.NEW)
		{
			status = Localization.Status.NEW;
		}
		else {
			// including if nameLocalization.status() == Localization.Status.DEFAULT, itll now be updated
			status = Localization.Status.UPDATED;
		}

		setNameLocalization(text, status);
	}

	/**
	 * Sets name localization with a specific status. Only use if specifying status is necessary.
	 * @param text
	 * @param status
	 */
	public void setNameLocalization(String text, Localization.Status status) {
		if (nameLocalization == null) {
			nameLocalization = new Localization(id(), text, status);
			return;
		}

		String id = nameLocalization.ID();
		nameLocalization = new Localization(id, text, status);
		focusTree.updateLocalization(nameLocalization);
	}

	public SimpleStringProperty nameLocalizationProperty() {
		return new SimpleStringProperty(nameLocalization());
	}

	public SimpleStringProperty descLocalizationProperty() {
		return new SimpleStringProperty(descLocalization());
	}

	public void setDescLocalization(Localization localization) {
		descLocalization = localization;
	}

	public void setDescLocalization(String text) {
		if (descLocalization == null) {
			descLocalization = new Localization(id() + "_desc", text, Localization.Status.DEFAULT);
			return;
		}

		Localization.Status status;

		if (descLocalization.status() == Localization.Status.NEW)
		{
			status = Localization.Status.NEW;
		}
		else {
			// including if nameLocalization.status() == Localization.Status.DEFAULT, itll now be updated
			status = Localization.Status.UPDATED;
		}

//		descLocalization = new Localization(id, text, status);
		setDescLocalization(text, status);
		// todo?
	}

	public void setDescLocalization(String text, Localization.Status status) {
		if (descLocalization == null) {
			descLocalization = new Localization(id(), text, status);
			return;
		}

		String id = descLocalization.ID();
		descLocalization = new Localization(id, text, status);
		focusTree.updateLocalization(descLocalization);
	}


	public String descLocalization() {
		if (descLocalization == null) {
			return "[null]";
		}
		return descLocalization.text();
	}

	public Localization getDescLocalization() {
		return descLocalization;
	}

	// todo implement icon lookup
	public void setIcon(Expression exp) {
		if (exp == null) {
			// icon = null;
			// return;
		}

		setIcon(exp.getText());
	}

	/**
	 * Sets focus icon id
	 * 
	 * @param icon
	 */
	public void setIcon(String icon) {
		// null string -> set no (null) icon
		// icon == null check required to not throw access exception
		if (icon == null || icon.equals("")) {
			// this.icon = null;
			// return;
		}

		this.icon = new SimpleStringProperty(icon);

		/* dds binary data buffer */
		/* https://github.com/npedotnet/DDSReader */
		try {
			String gfx = Interface.getGFX(icon);

			FileInputStream fis;
			if (gfx == null) {
				System.err.println("GFX was not found for " + icon);
				fis = new FileInputStream(Settings.MOD_PATH + "\\gfx\\interface\\goals\\focus_ally_cuba.dds");
			} else {
				fis = new FileInputStream(Interface.getGFX(icon));
			}
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			fis.close();
			int[] ddspixels = DDSReader.read(buffer, DDSReader.ARGB, 0);
			int ddswidth = DDSReader.getWidth(buffer);
			int ddsheight = DDSReader.getHeight(buffer);

//			ddsImage = new BufferedImage(ddswidth, ddsheight, BufferedImage.TYPE_INT_ARGB);
//			ddsImage.setRGB(0, 0, ddswidth, ddsheight, ddspixels, 0, ddswidth);
			ddsImage = JavaFXImageUtils.imageFromDDS(ddspixels, ddswidth, ddsheight);
		} catch (IOException exc) {
			exc.printStackTrace();
		}
	}

	public void setPrerequisite(Node exp) {
		setPrerequisite(List.of(exp));
	}

	/**
	 * accepts groups of prerequisites
	 * 
	 * @param exps
	 */
	public void setPrerequisite(List<Node> exps) {
		if (exps == null) {
			prerequisite = null;
			return;
		}
		removePrerequisites();
//		for (Node exp : exps) {
////			for (Expression subexp : exp.getSubexpressions()) {
////				if (subexp.getText() == null) {
////					prerequisite = null;
////					System.err.println("Focus prerequisite invalid, " + this.id + ", " + exp);
////					return;
////				}
////			}
//		}

		Set<Set<Focus>> prerequisites = new HashSet<>();

		/* sort through prerequisite={ expressions */
		for (Node prereqExp : exps) {
			if (prereqExp == null || !prereqExp.contains("focus")) {
				continue;
			}

			HashSet<Focus> subset = new HashSet<>();
			for (Node prereqNode : prereqExp.filter("focus").toList()) {
				if (prereqNode.value() == null) {
					// todo better error reporting
					System.err.println("Expected a value associated with prerequisite focus assignment");
					continue;
				}

				String prereq = prereqNode.value().string();
//				prereqStr = prereqStr.trim();
//				if (!prereqStr.matches("[\\S]+")) {
//					System.err.println("Focus prerequisite is invalid, " + this.id + ", " + prereqStr);
//					prerequisite = null;
//					return;
//				}

				if (focusTree.getFocus(prereq) != null) {
					subset.add(focusTree.getFocus(prereq)); // todo error check someday
				} else {
					addPendingFocusReference(prereq, this::setPrerequisite, exps);
				}
			}

			if (!subset.isEmpty()) {
				prerequisites.add(subset);
			}
		}

		setPrerequisite(prerequisites);
	}


	private void addPendingFocusReference(String pendingFocusId, Consumer<List<Node>> pendingAction, List<Node> args) {
		pendingFocusReferences.addReference(pendingFocusId, pendingAction, args);
	}

	private void removePrerequisites() {
		if (this.prerequisite != null) {
			this.prerequisite.clear();
		}
	}

	/**
	 * sets focus prerequisite focuses
	 * 
	 * @param prerequisite Set of prerequisite focuses. Can not include this focus.
	 */
	public void setPrerequisite(Set<Set<Focus>> prerequisite) { // todo can have prerequisites where 1 necessary, all
																// necessary, etc.
		// focus can not be its own prerequisite
		// todo
		// if (prerequisite.contains(this)) {
		// throw new IllegalArgumentException("Focus can not be its own prerequisite");
		// }

		this.prerequisite = prerequisite;
	}

	public void setMutuallyExclusive(String exp) {
		if (exp == null) {
			mutually_exclusive = null;
			return;
		}
	}

	public void setMutuallyExclusive(List<Node> exp) {
		if (exp == null) {
			mutually_exclusive = null;
			return;
		}
		// todo
	}

	/**
	 * Sets mutually exclusive focuses
	 * 
	 * @param mutually_exclusive Set of mutually exclusive focus(es) with this
	 *						   focus. Should not include this focus.
	 */
	public void setMutuallyExclusive(Set<Focus> mutually_exclusive) {
		// focus can not be mutually exclusive with itself
		if (mutually_exclusive.contains(this)) {
			throw new IllegalArgumentException("Focus can not be mutually exclusive with itself");
		}

		this.mutually_exclusive = mutually_exclusive;
	}

	/**
	 * Sets mutually exclusive focus
	 * 
	 * @param mutually_exclusive mutually exclusive focus
	 */
	public void setMutuallyExclusive(Focus mutually_exclusive) {
		HashSet<Focus> set = new HashSet<>();
		set.add(mutually_exclusive);
		setMutuallyExclusive(set);
	}

	public void addMutuallyExclusive(Focus mutually_exclusive) {
		if (this.mutually_exclusive == null) {
			this.mutually_exclusive = new HashSet<>();
			this.mutually_exclusive.add(mutually_exclusive);
		}
	}

	public void setAvailable(Node exp) {
		if (exp == null) {
			available = null;
			return;
		}
	}

	/**
	 * Sets available trigger of focus
	 * 
	 * @param availableTrigger trigger which controls focus availability
	 */
	public void setAvailable(Trigger availableTrigger) {
		this.available = availableTrigger;
	}

	public Image getDDSImage() {
		return ddsImage;
	}

	public boolean hasPrerequisites() {
		return !(prerequisite == null || prerequisite.isEmpty());
	}

	public Set<Set<Focus>> getPrerequisites() {
		return prerequisite;
	}

	public double cost() {
		return cost;
	}

	public double completionTime() {
		return cost * FOCUS_COST_FACTOR;
	}

	public Localization getNameLocalization() {
		return nameLocalization;
	}

	public void setLocalization(FocusLocalizationFile localization) {
		Localization nameLoc = localization.getLocalization(id());
		Localization descLoc = localization.getLocalizationDesc(id() + "_desc");
		setNameLocalization(nameLoc);
		setDescLocalization(descLoc);
	}

	public void setLocalization(FocusLocalizationFile localization, String focusNameLoc, String focusDescLoc) {
		Localization nameLoc = localization.setLocalization(id(), focusNameLoc);
		Localization descLoc = localization.setLocalizationDesc(id() + "_desc", focusDescLoc);
		setNameLocalization(nameLoc);
		setDescLocalization(descLoc);
	}

	@Override
	public int numLocalizableProperties() {
		return 2;
	}

	public PendingFocusReferenceList getPendingFocusReferences() {
		return pendingFocusReferences;
	}

	public boolean removePendingFocusReference(String reference) {
		return pendingFocusReferences.removeReference(reference);
	}

	public boolean removePendingFocusReference(Focus reference) {
		return removePendingFocusReference(reference.id());
	}

	public String name() {
		if (nameLocalization() == null) {
			return id();
		} else {
			return nameLocalization();
		}
	}
}
