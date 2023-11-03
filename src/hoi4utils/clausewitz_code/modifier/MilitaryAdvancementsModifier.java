package hoi4utils.clausewitz_code.modifier;

public class MilitaryAdvancementsModifier implements Modifier {
	public MilitaryAdvancementsModifier() {
//		super(Scope.military_advancements);
	}

	@Override
	public ModifierCategory getCategory() {
		return ModifierCategory.military_advancements;
	}

	public enum modifiers {
		experience_gain_army,
		experience_gain_army_factor,
		experience_gain_army_unit,
		experience_gain_army_unit_factor,
		experience_gain_factor,
		experience_gain_navy,
		experience_gain_navy_factor,
		experience_gain_navy_unit,
		experience_gain_navy_unit_factor,
		research_speed_factor,
	}
}
