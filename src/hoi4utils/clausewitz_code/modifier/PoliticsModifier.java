package hoi4utils.clausewitz_code.modifier;

public class PoliticsModifier implements Modifier {
	public PoliticsModifier() {
//		super(Scope.politics);
	}

	@Override
	public ModifierCategory getCategory() {
		return ModifierCategory.politics;
	}

	public enum modifiers {
		drift_defence_factor,
		guarantee_cost,
		master_ideology_drift,
	}
}
