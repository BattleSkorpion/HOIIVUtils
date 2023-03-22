package clausewitz_coding.state;

import clausewitz_coding.code.ClausewitzDate;
import clausewitz_parser.Expression;
import clausewitz_parser.Parser;
import clausewitz_coding.HOI4Fixes;
import clausewitz_coding.state.buildings.Infrastructure;
import clausewitz_coding.state.buildings.Resources;
import clausewitz_coding.country.CountryTag;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class State {
    /* static */
    private static final ArrayList<State> states = new ArrayList<>();

    private File stateFile;
    private int stateID;
    private String name;
    private final Map<ClausewitzDate, Owner> owner;
    private StateCategory stateCategory;
    private Infrastructure stateInfrastructure;
    private Resources resourcesData;


    public State(File stateFile) {
        /* init */
        owner = new HashMap<>();

        this.stateFile = stateFile;
        this.name = stateFile.getName().replace(".txt", "");

        int infrastructure = 0;
        int population = 0;
        int civilianFactories = 0;
        int militaryFactories = 0;
        int dockyards = 0;
//        int navalPorts = 0;       has a province location
        int airfields = 0;
        int aluminum = 0;
        int chromium = 0;
        int oil = 0;
        int rubber = 0;
        int steel = 0;
        int tungsten = 0;

        /* parse state data */
        Parser stateParser = new Parser(stateFile);
        Expression exp = stateParser.expressions();

        // id
        if (exp.get("id") != null) {
            stateID = exp.get("id").getValue();
        }
        // owner
        if (exp.get("owner") != null) {
            // empty date constructor for default date
            owner.put(new ClausewitzDate(), new Owner(new CountryTag(exp.get("owner").getName())));
        }
        // population (manpower)
        if (exp.get("manpower") != null) {
            population = exp.get("manpower").getValue(); // TODO after here etc.
        }
        // state category
        if (exp.get("state_category") != null) {

        }

        /* buildings */
        Expression buildingsExp = exp.get("buildings = {");

        if (buildingsExp == null) {
            System.err.println("State error: buildings does not exist, " + stateFile.getName());
            stateInfrastructure = null;
        }
        else {
            // infrastructure
            if (buildingsExp.get("infrastructure") != null) {
                infrastructure = buildingsExp.get("infrastructure").getValue(); // TODO after here etc.
            }
            // civilian factories
            if (exp.get("industrial_complex") != null) {
                civilianFactories = exp.get("industrial_complex").getValue(); // TODO after here etc.
            }
            // military factories
            if (exp.get("arms_factory") != null) {
                militaryFactories = exp.get("arms_factory").getValue(); // TODO after here etc.
            }
            // dockyards
            if (exp.get("dockyard") != null) {
                dockyards = exp.get("dockyard").getValue(); // TODO after here etc.
            }
            // airfields
            if (exp.get("air_base") != null) {
                airfields = exp.get("air_base").getValue(); // TODO after here etc.
            }
        }

        /* resources */
        // aluminum (aluminium bri'ish spelling)
        if (exp.get("aluminium") != null) {
            aluminum = (int) exp.get("aluminium").getDoubleValue();
        }
        // chromium
        if (exp.get("chromium") != null) {
            chromium = (int) exp.get("chromium").getDoubleValue();
        }
        // rubber
        if (exp.get("rubber") != null) {
            rubber = (int) exp.get("rubber").getDoubleValue();
        }
        // oil
        if (exp.get("oil") != null) {
            oil = (int) exp.get("oil").getDoubleValue();
        }
        // steel
        if (exp.get("steel") != null) {
            steel = (int) exp.get("steel").getDoubleValue();
        }
        // tungsten
        if (exp.get("tungsten") != null) {
            tungsten = (int) exp.get("tungsten").getDoubleValue();
        }

        // data record
        stateInfrastructure = new Infrastructure(population, infrastructure, civilianFactories, militaryFactories,
                dockyards, 0, airfields);
        resourcesData = new Resources(aluminum, chromium, oil, rubber, steel, tungsten);
        // add to states list
        states.add(this);
    }

    public static void readStates() {
        File states_dir = new File(HOI4Fixes.hoi4_dir_name + HOI4Fixes.states_folder);

        if (!states_dir.exists() || !states_dir.isDirectory()) {
            System.err.println("In State.java - " + HOI4Fixes.states_folder + " is not a directory, or etc.");
            return;
        }
        if (states_dir.listFiles() == null || states_dir.listFiles().length == 0) {
            System.out.println("No states found in " + HOI4Fixes.states_folder);
            return;
        }

        for (File stateFile : states_dir.listFiles()) {
            new State(stateFile);
        }
    }

    public static ArrayList<State> list() {
        return states;
    }

    public static ArrayList<State> listFromCountry(CountryTag tag) {
        ArrayList<State> countryStates = new ArrayList<>();

        for(State state : states) {
            Owner owner = state.owner.get(ClausewitzDate.current());
            if (owner != null) {
                if (owner.isCountry(tag)) {
                    countryStates.add(state);
                }
            } else {
                System.out.println("No owner for state: " + state);
            }
        }

        return countryStates;
    }

    public static Infrastructure infrastructureOfStates(ArrayList<State> states) {
        int infrastructure = 0;
        int population = 0;
        int civilianFactories = 0;
        int militaryFactories = 0;
        int dockyards = 0;
        int airfields = 0;

        for (State state : states) {
            Infrastructure stateData = state.getStateInfrastructure();
            infrastructure += stateData.infrastructure();
            population += stateData.population();
            civilianFactories += stateData.civilianFactories();
            militaryFactories += stateData.militaryFactories();
            dockyards += stateData.navalDockyards();
            airfields += stateData.airfields();
        }

        return new Infrastructure(population, infrastructure, civilianFactories, militaryFactories, dockyards, 0, airfields);
    }

    public static Resources resourcesOfStates(ArrayList<State> states) {
        int aluminum = 0;
        int chromium = 0;
        int oil = 0;
        int rubber = 0;
        int steel = 0;
        int tungsten = 0;

        for (State state : states) {
            Resources resources = state.getResources();
            aluminum += resources.aluminum();
            chromium += resources.chromium();
            oil += resources.oil();
            rubber += resources.rubber();
            steel += resources.steel();
            tungsten += resources.tungsten();
        }

        return new Resources(aluminum, chromium, oil, rubber, steel, tungsten);
    }

    public static Resources resourcesOfStates() {
        return resourcesOfStates(states);
    }

    public static double numStates(CountryTag country) {
        return listFromCountry(country).size();
    }

    public Infrastructure getStateInfrastructure() {
        return stateInfrastructure;
    }

    public Resources getResources() {
        return resourcesData;
    }

    public String toString() {
        return name;
    }

    protected static boolean usefulData(String data) {
        if (!data.isEmpty()) {
            if (data.trim().charAt(0) == '#') {
                return false;
            }
            else {
                return true;
            }
        }
        else {
            return false;
        }
    }
}
