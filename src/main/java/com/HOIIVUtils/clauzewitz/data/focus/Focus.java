package com.HOIIVUtils.clauzewitz.data.focus;

import com.HOIIVUtils.Settings;
import com.HOIIVUtils.clauzewitz.code.effect.Effect;
import com.HOIIVUtils.clauzewitz.code.scope.NotPermittedInScopeException;
import com.HOIIVUtils.clauzewitz.code.scope.Scope;
import com.HOIIVUtils.clauzewitz.code.scope.ScopeCategory;
import com.HOIIVUtils.clauzewitz.DataFunctionProvider;
import com.HOIIVUtils.clauzewitz.localization.Localizable;
import com.HOIIVUtils.clauzewitz.data.country.CountryTags;
import com.HOIIVUtils.clauzewitz.data.gfx.Interface;
import com.HOIIVUtils.clausewitz_parser.Node;
import com.HOIIVUtils.ddsreader.DDSReader;
import com.HOIIVUtils.clauzewitz.exceptions.InvalidEffectParameterException;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import com.HOIIVUtils.ui.javafx.image.JavaFXImageUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Focus class represents an individual focus of a National Focus (Focus Tree).
 */
public class Focus implements Localizable, Comparable<Focus>, DataFunctionProvider<Focus> {
	private static final int FOCUS_COST_FACTOR = 7; // turn into from defines, or default 7. (get default vanilla define
	// instead?)
	private final int DEFAULT_FOCUS_COST = 10; // default cost (in weeks by default) when making a new focus.
	private static final HashSet<String> focusIDs = new HashSet<>();

	/* attributes */
	protected FocusTree focusTree;
	// Expression focusExp;
	protected SimpleStringProperty id;
	protected SimpleStringProperty icon;
	protected Image ddsImage;
	protected Set<Set<Focus>> prerequisite; // can be multiple, but hoi4 code is simply "prerequisite"
	protected Set<Focus> mutually_exclusive;
	// protected Trigger available;
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

	/**
	 * completion raward
	 * null: no definition within focus = {...} block
	 * empty: definition exists, but contents is empty, focus = { }
	 */
	protected List<Effect> completionReward;

	/**
	 * Set of the focus id's this focus is still attempting to reference (but may
	 * not be loaded/created yet).
	 */
	private final PendingFocusReferenceList pendingFocusReferences = new PendingFocusReferenceList();

	public Focus(String focus_id, FocusTree focusTree) throws DuplicateFocusException {
		if (focusIDs.contains(focus_id)) {
			throw new DuplicateFocusException("Error: focus id " + focus_id + " already exists.");
		}
		this.id = new SimpleStringProperty(focus_id);

		this.focusTree = focusTree;
		focusIDs.add(focus_id);
	}

	public Focus(String focusId, FocusTree focusTree, Node node) throws DuplicateFocusException {
		this(focusId, focusTree);

		loadAttributes(node);
	}

	/**
	 * Obtain data functions intended for displaying focus properties across a table
	 *
	 * @return
	 */
	public static List<Function<Focus, ?>> getDataFunctions() {
		// for optimization, set number of data functions (why not)
		List<Function<Focus, ?>> dataFunctions = new ArrayList<>(3);

		dataFunctions.add(Focus::id);
		dataFunctions.add(focus -> focus.localizationText(Property.NAME));
		dataFunctions.add(focus -> focus.localizationText(Property.DESCRIPTION));

		return dataFunctions;
	}

	/**
	 * Obtains focus id
	 * 
	 * @return
	 */
	public String id() {
		if (id == null) {
			return null;
		}
		return id.get();
	}

	/**
	 * Obtains focus id simple string property
	 * 
	 * @return
	 */
	public SimpleStringProperty idProperty() {
		return id;
	}

	/**
	 * if the focus has a relative position, this will give the relative x position.
	 * <p>
	 * Otherwise, equivalent to <code>absoluteX()</code>.
	 * </p>
	 * 
	 * @return
	 */
	public int x() {
		return x;
	}

	/**
	 * if the focus has a relative position, this will give the relative y position.
	 * <p>
	 * Otherwise, equivalent to <code>absoluteY()</code>.
	 * </p>
	 * 
	 * @return
	 */
	public int y() {
		return y;
	}

	/**
	 * Calculates the focus X position, if the focus has a relative position.
	 * <p>
	 * Otherwise, equivalent to <code>x()</code>.
	 * Use when knowing the real xy-position of the focus in the focus tree is
	 * necessary.
	 * </p>
	 * 
	 * @return
	 */
	public int absoluteX() {
		if (relative_position_id == null) {
			return x;
		} else {
			return (int) absolutePosition().getX();
		}
	}

	/**
	 * Calculates the focus Y position, if the focus has a relative position.
	 * <p>
	 * Otherwise, equivalent to <code>y()</code>.
	 * Use when knowing the real xy-position of the focus in the focus tree is
	 * necessary.
	 * </p>
	 * 
	 * @return
	 */
	public int absoluteY() {
		if (relative_position_id == null) {
			return y;
		} else {
			return (int) absolutePosition().getY();
		}
	}

	/**
	 * Returns the defined focus xy-position.
	 * <p>
	 * If focus position is relative, returns the relative position.
	 * Otherwise, equivalent to <code>absolutePosition()</code>.
	 * </p>
	 *
	 * @return point representing xy location, or relative xy if relative.
	 */
	public Point2D position() {
		return new Point2D(x, y);
	}

	/**
	 * Absolute focus xy-position.
	 * <p>
	 * If the defined focus position is relative to another focus,
	 * calculates and returns the real xy-position in the focus tree.
	 * </p>
	 *
	 * @return Point representing absolute position of focus.
	 * @implNote Should only be called after all focuses in focus tree are
	 *           instantiated.
	 */
	public Point2D absolutePosition() {
		if (relative_position_id == null) {
			return position();
		}
		if (relative_position_id.equals(this.id())) {
			System.err.println("Relative position id same as focus id for " + this); // todo not an error of this
																						// program necessarily, issue
																						// should be handled
																						// differently?
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

	/**
	 * Obtains focus icon id
	 * 
	 * @return focus icon id
	 */
	public SimpleStringProperty icon() {
		return icon;
	}

	/**
	 * When printing this focus, or otherwise using {@code toString()}, return the
	 * focus ID.
	 * 
	 * @return
	 */
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
		if (!exp.name().equals("focus")) {
			System.err.println(this + " - Not valid focus expression/definition.");
			return;
		}

		// focusExp = exp.get("focus=");

		setID(exp.getValue("id").string());
		try {
			setXY(exp.getValue("x").integer(), exp.getValue("y").integer());
		} catch (IllegalStateException e) {
			System.err.println(e.getMessage());
			setXY(0, 0); // todo better error reporting
		}
		try {
			setRelativePositionID(exp.getValue("relative_position_id").string());
		} catch (IllegalStateException e) {
			System.err.println(e.getMessage());
			setRelativePositionID(null); // todo better error reporting
		}
		// setFocusLoc();
		try {
			setIcon(exp.getValue("icon").string());
		} catch (IllegalStateException e) {
			System.err.println(e.getMessage());
			setIcon(null); // todo better error reporting
		}
		try {
			setCost(exp.getValue("cost").rational());
		} catch (IllegalStateException e) {
			System.err.println(e.getMessage());
			setCost(DEFAULT_FOCUS_COST); // todo better error reporting
		}
		setPrerequisite(exp.filterName("prerequisite").toList());
		setMutuallyExclusive(exp.filterName("mutually_exclusive").toList());
		setAvailable(exp.findFirst("available"));
		setCompletionReward(exp.findFirst("completion_reward"));
	}

	/**
	 * Sets focus id
	 * 
	 * @param id focus id
	 */
	public void setID(String id) {
		this.id.set(id);
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

	/**
	 * Sets new xy-coordinates, returns previous xy-coordinates.
	 * 
	 * @param xy focus new xy-coordinates
	 * @return previous xy-coordinates
	 */
	public Point setXY(Point xy) {
		return setXY(xy.x, xy.y);
	}

	/**
	 * Sets new xy-coordinates, returns previous xy-coordinates.
	 * 
	 * @param x focus new x-coordinate
	 * @param y focus new y-coordinate
	 * @return previous xy-coordinates
	 */
	private Point setXY(Node x, Node y) {
		if (x == null && y == null) {
			return setXY(0, 0);
		}
		if (x == null) {
			return setXY(0, y.value().integer());
		}
		if (y == null) {
			return setXY(x.value().integer(), 0);
		}
		return setXY(x.value().integer(), y.value().integer());
	}

	/**
	 * Sets relative position id
	 * 
	 * @param exp relative position id
	 */
	private void setRelativePositionID(String exp) {
		if (exp == null) {
			relative_position_id = null; // perfectly acceptable
			return;
		}
		relative_position_id = exp;
	}

	/**
	 * Sets focus cost to default cost
	 */
	public void setCost() {
		setCost(DEFAULT_FOCUS_COST);
	}

	/**
	 * Sets focus cost (in weeks by default)
	 * 
	 * @param cost focus cost
	 *             todo: add defines support
	 */
	public void setCost(Number cost) {
		this.cost = cost.doubleValue();
	}

	// public void setCost(Expression exp) {
	// if (exp == null) {
	// cost = 0;
	// return;
	// }
	//
	// this.cost = exp.getValue();
	// }

	// // todo implement icon lookup
	// public void setIcon(Expression exp) {
	// if (exp == null) {
	// // icon = null;
	// // return;
	// }
	//
	// setIcon(exp.getText());
	// }

	/**
	 * Sets focus icon id
	 * 
	 * @param icon focus icon id
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
				// System.err.println("GFX was not found for " + icon); // too much right now
				try {
					fis = new FileInputStream(Settings.MOD_PATH + "\\gfx\\interface\\goals\\focus_ally_cuba.dds");
				} catch (FileNotFoundException exc) {
					ddsImage = null;
					return;
				}
			} else {
				fis = new FileInputStream(Interface.getGFX(icon));
			}
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			fis.close();
			int[] ddspixels = DDSReader.read(buffer, DDSReader.ARGB, 0);
			int ddswidth = DDSReader.getWidth(buffer);
			int ddsheight = DDSReader.getHeight(buffer);

			// ddsImage = new BufferedImage(ddswidth, ddsheight,
			// BufferedImage.TYPE_INT_ARGB);
			// ddsImage.setRGB(0, 0, ddswidth, ddsheight, ddspixels, 0, ddswidth);
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
	 * @param exps list of prerequisite={...} expressions
	 */
	public void setPrerequisite(List<Node> exps) {
		if (exps == null) {
			prerequisite = null;
			return;
		}
		removePrerequisites();
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

				if (focusTree.getFocus(prereq) != null) {
					subset.add(focusTree.getFocus(prereq)); // todo 'error' (not us?) check someday
				} else {
					addPendingFocusReference(prereq, this::setPrerequisite, exps);
				}
			}

			if (!subset.isEmpty())
				prerequisites.add(subset);
		}

		setPrerequisite(prerequisites);
	}

	/**
	 * Add reference of a property of this focus to an unloaded focus; as it
	 * has not yet been loaded, is not actually defined, or is otherwise unknown.
	 * 
	 * @param pendingFocusId id of focus that is not yet loaded or defined
	 * @param pendingAction  operation to perform when focus is loaded
	 * @param args           arguments to pass to pendingAction
	 */
	private void addPendingFocusReference(String pendingFocusId, Consumer<List<Node>> pendingAction, List<Node> args) {
		pendingFocusReferences.addReference(pendingFocusId, pendingAction, args);
	}

	/**
	 * Removes all defined focus prerequisites
	 */
	private void removePrerequisites() {
		if (this.prerequisite != null) {
			this.prerequisite.clear();
		}
	}

	/**
	 * Removes all defined focus mutually exclusive focuses
	 */
	private void removeMutuallyExclusive() {
		if (this.mutually_exclusive != null) {
			this.mutually_exclusive.clear();
		}
	}

	/**
	 * sets focus prerequisite focuses
	 *
	 * @param prerequisite Set of prerequisite focuses. Can not include this focus.
	 */
	public void setPrerequisite(Set<Set<Focus>> prerequisite) {
		// focus can not be its own prerequisite
		// todo
		// if (prerequisite.contains(this)) {
		// throw new IllegalArgumentException("Focus can not be its own prerequisite");
		// }

		this.prerequisite = prerequisite;
	}

	/**
	 * sets focus prerequisite focus
	 * 
	 * @param exp
	 */
	public void setMutuallyExclusive(String exp) {
		if (exp == null) {
			mutually_exclusive = null;
			return;
		}
	}

	/**
	 * <p>
	 * From <a href="https://hoi4.paradoxwikis.com/National_focus_modding">National
	 * Focus Modding</a>:
	 * </p>
	 * <p>
	 * "Mutual exclusivity to multiple focuses is usually done by putting several of
	 * focus = TAG_focusname
	 * in the same mutually_exclusive, but defining several of mutually_exclusive is
	 * also possible."
	 * </p>
	 * 
	 * @param exps
	 */
	public void setMutuallyExclusive(List<Node> exps) {
		if (exps == null) {
			mutually_exclusive = null;
			return;
		}
		removeMutuallyExclusive();
		mutually_exclusive = new HashSet<>();

		/* sort through prerequisite={ expressions */
		for (Node exclusiveExp : exps) {
			if (exclusiveExp == null || !exclusiveExp.contains("focus")) {
				continue;
			}

			for (Node exclusiveNode : exclusiveExp.filter("focus").toList()) {
				if (exclusiveNode.value() == null) {
					// todo better error reporting
					System.err.println("Expected a value associated with mutually exclusive focus assignment");
					continue;
				}

				String exclusive_id = exclusiveNode.value().string();

				if (focusTree.getFocus(exclusive_id) != null) {
					mutually_exclusive.add(focusTree.getFocus(exclusive_id)); // todo error check someday
				} else {
					addPendingFocusReference(exclusive_id, this::setMutuallyExclusive, exps);
				}
			}
		}
	}

	/**
	 * Sets mutually exclusive focuses
	 *
	 * @param mutually_exclusive Set of mutually exclusive focus(es) with this
	 *                           focus. Should not include this focus.
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

	/**
	 * Sets focus available trigger
	 * 
	 * @param mutually_exclusive mutually exclusive focus
	 */
	public void addMutuallyExclusive(Focus mutually_exclusive) {
		if (this.mutually_exclusive == null) {
			this.mutually_exclusive = new HashSet<>();
			this.mutually_exclusive.add(mutually_exclusive);
		}
	}

	/**
	 * Sets focus available trigger
	 * 
	 * @param exp available trigger
	 */
	public void setAvailable(Node exp) {
		if (exp == null) {
			// available = null;
			return;
		}
	}

	// /**
	// * Sets available trigger of focus
	// *
	// * @param availableTrigger trigger which controls focus availability
	// */
	// public void setAvailable(Trigger availableTrigger) {
	// this.available = availableTrigger;
	// }

	/**
	 * Gets focus icon DDS image
	 * 
	 * @return
	 */
	public Image getDDSImage() {
		return ddsImage;
	}

	/**
	 * Returns true if the focus has a prerequisite focus.
	 * 
	 * @return true if the focus has a prerequisite focus
	 */
	public boolean hasPrerequisites() {
		return !(prerequisite == null || prerequisite.isEmpty());
	}

	/**
	 * Returns true if the focus has a mutually exclusive focus.
	 * 
	 * @return true if the focus has a mutually exclusive focus
	 */
	public boolean isMutuallyExclusive() {
		return !(mutually_exclusive == null || mutually_exclusive.isEmpty());
	}

	/**
	 * Returns true if the focus has a completion reward.
	 * 
	 * @return true if the focus has a completion reward
	 */
	public Set<Set<Focus>> getPrerequisites() {
		return prerequisite;
	}

	/**
	 * Returns true if the focus has a completion reward.
	 * 
	 * @return true if the focus has a completion reward
	 */
	public double cost() {
		return cost;
	}

	/**
	 * Returns the clausewitz displayed/gameplay focus completion time, which is the
	 * truncate/floor of the
	 * defined completion time (cost * weeks (default).
	 * 
	 * @return
	 */
	public double displayedCompletionTime() {
		return Math.floor(preciseCompletionTime());
	}

	/**
	 * Returns the precise focus completion time, which is the defined completion
	 * time (cost * weeks (default)).
	 * 
	 * @return precise focus completion time
	 */
	public double preciseCompletionTime() {
		return cost * FOCUS_COST_FACTOR;
	}

	@Override
	public @NotNull Map<Property, String> getLocalizableProperties() {
		return Map.of(Property.NAME, id(), Property.DESCRIPTION, id() + "_desc");
	}

	@Override
	public @NotNull Collection<? extends Localizable> getLocalizableGroup() {
		if (focusTree == null) {
			return List.of(this);
		}
		return focusTree.getLocalizableGroup();
	}

	/**
	 * Returns the list of pending focus references
	 *
	 * @return list of pending focus references
	 */
	public PendingFocusReferenceList getPendingFocusReferences() {
		return pendingFocusReferences;
	}

	/**
	 * Removes pending focus reference
	 * 
	 * @param reference focus id reference
	 * @return true if reference was removed
	 */
	public boolean removePendingFocusReference(String reference) {
		return pendingFocusReferences.removeReference(reference);
	}

	/**
	 * Removes pending focus reference
	 * 
	 * @param reference focus reference
	 * @return true if reference was removed
	 */
	public boolean removePendingFocusReference(Focus reference) {
		return removePendingFocusReference(reference.id());
	}

	/**
	 *
	 * @return String representing focus completion reward effects,
	 *         with custom tooltips, not including hidden effects,
	 */
	@NotNull
	public String getFocusDetails() {
		NumberFormat df = DecimalFormat.getIntegerInstance();

		StringBuilder details = new StringBuilder();
		/* id */
		details.append("ID: ");
		details.append(id());
		details.append("\n");
		/* completion time */
		details.append("Completion time: ");
		details.append(df.format(displayedCompletionTime()));
		details.append("\n");

		/* prerequisites */
		for (Set<Focus> prereqSet : this.getPrerequisites()) {
			if (prereqSet.size() > 1) {
				details.append("Requires one of the following: \n");
				for (Focus f : prereqSet) {
					details.append("- ");
					details.append(f.localization(Property.NAME));
					details.append("\n");
				}
			} else {
				details.append("Requires: ");
				details.append(prereqSet.iterator().next().localization(Property.DESCRIPTION));
				details.append("\n");
			}
		}

		if (hasCompletionReward()) {
			/* completion reward */
			details.append("\nEffect: \n");
			for (Effect effect : completionReward) {
				// details.append("\t");
				// // todo why would why huh? idk.
				//// if (effect.hasTarget() &&
				// !effect.isScope(Scope.of(this.focusTree.country()))) {
				//// details.append(effect.target());
				//// details.append("\t");
				//// }
				// details.append(effect.name());
				// if (effect.hasParameterBlock()) {
				// details.append(" = {\n");
				// for (EffectParameter n : effect.parameterList()) {
				// details.append("\t\t");
				// if (n == null) {
				// details.append("[effect parameter was null]");
				// } else {
				// details.append(n.displayParameter());
				// }
				// details.append("\n");
				// }
				// details.append("\t}");
				// }
				// else if (effect.value() != null) {
				// details.append(" = ");
				// details.append(effect.value());
				// }
				// details.append("\n");
				details.append(effect.displayScript());
				details.append("\n");
			}
		}
		return details.toString();
	}

	public Set<Focus> getMutuallyExclusive() {
		return mutually_exclusive;
	}

	public List<Effect> completionReward() {
		return completionReward;
	}

	public void setCompletionReward(List<Effect> completionReward) {
		this.completionReward = completionReward;
	}

	/**
	 * Sets completion reward of focus
	 * 
	 * @param completionRewardNode format: "completion_reward = { ... }"
	 *                             (assumed to be correct)
	 */
	public void setCompletionReward(Node completionRewardNode) {
		completionReward = new ArrayList<>();
		if (completionRewardNode.valueIsNull()) {
			return; // keep newly set empty list, showing completion reward
					// was defined but had no effects
		}

		String cr_identifier = completionRewardNode.name();

		setCompletionRewardsOfNode(completionRewardNode);
	}

	private void setCompletionRewardsOfNode(Node completionRewardNode) {
		setCompletionRewardsOfNode(completionRewardNode, Scope.of(this.focusTree.country()));
	}

	/**
	 * todo stuff
	 * 
	 * @param completionRewardNode
	 * @param scope
	 */
	private void setCompletionRewardsOfNode(Node completionRewardNode, Scope scope) {
		for (Node n : completionRewardNode.value().list()) {
			/* check if its a deeper scope first */
			if (n.value().isList()) {
				// todo
				// // todo could this be handled better more generically with some other unique
				// scoping stuff like country tags like.... ? ....
				Scope s = null;
				if (CountryTags.exists(n.name())) {
					s = Scope.of(CountryTags.get(n.name()));
				} else {
					try {
						s = Scope.of(n.name(), scope);
					} catch (NotPermittedInScopeException e) {
						System.out.println(e.getMessage());
						break;
					}
				}

				if (s == null || s.scopeCategory == ScopeCategory.EFFECT) {
					/* if its not a scope may be an effect */
					// todo refactor area
					Effect effect = null;
					try {
						effect = Effect.of(n.name(), scope, n.value());
					} catch (InvalidEffectParameterException exc) {
						System.out.println(exc.getMessage());
					} catch (NotPermittedInScopeException exc) {
						System.out.println(exc.getMessage() + ", scope: " + scope + ", list? " + n.name());
					}
					if (effect != null) {
						completionReward.add(effect);
					} else {
						System.out.println("Scope " + n.name() + " unknown.");
					}
				} else {
					setCompletionRewardsOfNode(n, s);
				}
				// System.out.println(n.name);
			} else if (!n.valueIsNull()) {
				/* else if target, add effect with target */
				Effect effect = null; // todo
				try {
					effect = Effect.of(n.name(), scope, n.value());
				} catch (InvalidEffectParameterException e) {
					System.out.println(e.getMessage());
				} catch (NotPermittedInScopeException e) {
					System.out.println(e.getMessage() + ", scope: " + scope);
				}
				if (effect == null) {
					System.err.println("effect not found: " + n.name());
					continue;
				}
				if (effect.hasSupportedTargets()) {
					try {
						effect.setTarget(n.value().string(), scope);
					} catch (IllegalStateException e) {
						e.printStackTrace();
						continue; // todo idk tbh.
					} catch (Exception e) {
						throw new RuntimeException(e); // todo
					}
				}
				// effect.setParameters(n.value()); use new of() func.
				completionReward.add(effect);
			} else {
				/* else add effect */
				Effect effect = null; // todo
				try {
					effect = Effect.of(n.name(), scope);
				} catch (NotPermittedInScopeException e) {
					System.out.println(e.getMessage());
					continue;
				}
				if (effect == null) {
					System.err.println("effect not found: " + n.name());
					continue;
				}
				effect.setTarget(scope);
				completionReward.add(effect);
			}
		}
	}

	/**
	 * Returns true if the focus has a completion reward.
	 * 
	 * @return true if the focus has a completion reward
	 */
	private boolean hasCompletionReward() {
		return (completionReward != null && !completionReward.isEmpty());
	}

	@Override
	public int compareTo(@NotNull Focus o) {
		return this.id().compareTo(o.id());
	}

	/**
	 * Returns true if the focus has a completion reward.
	 * 
	 * @return true if the focus has a completion reward
	 */
	public Set<FocusSearchFilter> getFocusSearchFilters() {
		return focus_search_filters;
	}

	/**
	 * Sets focus search filters
	 * 
	 * @param focus_search_filters focus search filters
	 */
	public void setFocusSearchFilters(Set<FocusSearchFilter> focus_search_filters) {
		this.focus_search_filters = focus_search_filters;
	}

	public boolean isAvailableIfCapitulated() {
		return available_if_capitulated;
	}

	public void setAvailableIfCapitulated(boolean available_if_capitulated) {
		this.available_if_capitulated = available_if_capitulated;
	}

	public void setAvailableIfCapitulated() {
		this.available_if_capitulated = true;
	}

	public boolean willCancelIfInvalid() {
		return cancel_if_invalid;
	}

	public void setCancelIfInvalid(boolean cancel_if_invalid) {
		this.cancel_if_invalid = cancel_if_invalid;
	}

	public void setCancelIfInvalid() {
		this.cancel_if_invalid = true;
	}

	public boolean willContinueIfInvalid() {
		return continue_if_invalid;
	}

	public void setContinueIfInvalid(boolean continue_if_invalid) {
		this.continue_if_invalid = continue_if_invalid;
	}

	public void setContinueIfInvalid() {
		this.continue_if_invalid = true;
	}
}
