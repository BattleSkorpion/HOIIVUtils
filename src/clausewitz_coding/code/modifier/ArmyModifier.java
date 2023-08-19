package clausewitz_coding.code.modifier;

public class ArmyModifier implements Modifier {
	public ArmyModifier() {
//		super(Scope.army);
	}

	@Override
	public Scope getScope() {
		return Scope.army;
	}

	public enum modifiers {
		acclimatization_cold_climate_gain_factor,
		acclimatization_hot_climate_gain_factor,
		air_superiority_bonus_in_combat,
		armor_factor,
		army_advisor_cost_factor,
		army_armor_attack_factor,
		army_armor_defence_factor,
		army_armor_speed_factor,
		army_artillery_attack_factor,
		army_artillery_defence_factor,
		army_attack_against_major_factor,
		army_attack_against_minor_factor,
		army_attack_factor,
		army_attack_speed_factor,
		army_breakthrough_against_major_factor,
		army_breakthrough_against_minor_factor,
		army_core_attack_factor,
		army_core_defence_factor,
		army_defence_against_major_factor,
		army_defence_against_minor_factor,
		army_defence_factor,
		army_fuel_capacity_factor,
		army_fuel_consumption_factor,
		army_infantry_attack_factor,
		army_infantry_defence_factor,
		army_morale,
		army_morale_factor,
		army_org,
		army_org_factor,
		army_org_regain,
		army_speed_factor,
		army_speed_factor_for_controller,
		army_strength_factor,
		assign_army_leader_cp_cost,
		attack_bonus_against,
		attack_bonus_against_cores,
		attrition,
		attrition_for_controller,
		breakthrough_bonus_against,
		breakthrough_factor,
		cas_damage_reduction,
		cavalry_attack_factor,
		cavalry_defence_factor,
		combat_width_factor,
		coordination_bonus,
		defence,
		defense_bonus_against,
		dig_in_speed,
		dig_in_speed_factor,
		disable_strategic_redeployment,
		disable_strategic_redeployment_for_controller,
		dont_lose_dig_in_on_attack,
		experience_gain_army,
		experience_gain_army_factor,
		experience_gain_army_unit,
		experience_gain_army_unit_factor,
		extra_marine_supply_grace,
		extra_paratrooper_supply_grace,
		female_random_army_leader_chance,
		heat_attrition,
		heat_attrition_factor,
		land_night_attack,
		local_org_regain,
		max_commander_army_size,
		max_dig_in,
		max_dig_in_factor,
		max_planning,
		max_planning_factor,
		max_training,
		mechanized_attack_factor,
		mechanized_defence_factor,
		motorized_attack_factor,
		motorized_defence_factor,
		naval_invasion_planning_bonus_speed,
		no_supply_grace,
		offence,
		org_loss_at_low_org_factor,
		org_loss_when_moving,
		out_of_supply_factor,
		planning_speed,
		pocket_penalty,
		recon_factor,
		recon_factor_while_entrenched,
		rocket_attack_factor,
		shore_bombardment_bonus,
		special_forces_attack_factor,
		special_forces_cap,
		special_forces_defence_factor,
		special_forces_min,
		special_forces_no_supply_grace,
		special_forces_out_of_supply_factor,
		special_forces_training_time_factor,
		supply_combat_penalties_on_core_factor,
		supply_consumption_factor,
		terrain_penalty_reduction,
		terrain_trait_xp_gain_factor,
		training_time_army,
		training_time_army_factor,
		training_time_factor,
		unit_upkeep_attrition_factor,
		winter_attrition,
		winter_attrition_factor,
	}
}
