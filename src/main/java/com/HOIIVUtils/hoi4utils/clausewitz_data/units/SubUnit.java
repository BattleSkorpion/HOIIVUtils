
package com.HOIIVUtils.hoi4utils.clausewitz_data.units;

import com.HOIIVUtils.clausewitz_parser.Node;
import com.HOIIVUtils.clausewitz_parser.Parser;
import com.HOIIVUtils.clausewitz_parser.ParserException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/*
 * Unit File
 */
public record SubUnit(
		String abbreviation,
		String sprite,
		String mapIconCategory,
		Integer priority,
		Integer aiPriority,
		boolean active,
		String group,
		Integer combatWidth,
		Integer manpower,
		Integer maxOrganization,
		Integer defaultMorale,
		Integer maxStrength,
		Integer trainingTime,
		Double weight,
		Double supplyConsumption
) {
	public static List<SubUnit> read(File dir) {
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException("this is not a directory");
		}
		if (dir.listFiles().length == 0) {
			throw new IllegalArgumentException("This is empty");
		}

		List<SubUnit> subUnits = new ArrayList<>();
		for (File f : dir.listFiles()) {
			if (f.isDirectory()) continue;
			Parser parser = new Parser(f);
			Node rootNode;
			try {
				rootNode = parser.parse();
			} catch (ParserException e) {
				throw new RuntimeException(e);
			}

			var l = rootNode.filterName("sub_units");
			// loop through each sub unit definition in this file
			for (Node subUnitNode : l.toList()) {
				SubUnit subUnit = new SubUnit(
						subUnitNode.getValue("abbreviation").string(),
						subUnitNode.getValue("sprite").string(),
						subUnitNode.getValue("map_icon_category").string(),
						subUnitNode.getValue("priority").intClass(),
						subUnitNode.getValue("ai_priority").intClass(),
						subUnitNode.getValue("active").bool(Node.BoolType.YES_NO),
						//subUnit.type = subUnitNode.getValue("type").string(),
						subUnitNode.getValue("group").string(),
						//subUnit.categories
						subUnitNode.getValue("combat_width").intClass(),
						//subUnit.need
						subUnitNode.getValue("manpower").intClass(),
						subUnitNode.getValue("max_organization").intClass(),
						subUnitNode.getValue("default_morale").intClass(),
						subUnitNode.getValue("max_strength").intClass(),
						subUnitNode.getValue("training_time").intClass(),
						subUnitNode.getValue("weight").doubleClass(),
						subUnitNode.getValue("supply_consumption").doubleClass()
				);

				subUnits.add(subUnit);
			}
		}
		return subUnits;
	}

	public static List<Function<SubUnit, ?>> getDataFunctions() {
		List<Function<SubUnit, ?>> dataFunctions = new ArrayList<>(15);

		dataFunctions.add(SubUnit::abbreviation);
		dataFunctions.add(SubUnit::sprite);
		dataFunctions.add(SubUnit::mapIconCategory);
		dataFunctions.add(SubUnit::priority);
		dataFunctions.add(SubUnit::aiPriority);
		dataFunctions.add(SubUnit::active);
		dataFunctions.add(SubUnit::group);
		dataFunctions.add(SubUnit::combatWidth);
		dataFunctions.add(SubUnit::manpower);
		dataFunctions.add(SubUnit::maxOrganization);
		dataFunctions.add(SubUnit::defaultMorale);
		dataFunctions.add(SubUnit::maxStrength);
		dataFunctions.add(SubUnit::trainingTime);
		dataFunctions.add(SubUnit::weight);
		dataFunctions.add(SubUnit::supplyConsumption);

		return dataFunctions;
	}
}
