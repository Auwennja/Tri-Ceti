package auwennja.triceti.hullmods;

import com.fs.starfarer.api.combat.*;

public class triceti_myosynth extends BaseHullMod {

    public static float MIN_CREW_MULT = 0.5f;
    public static float CASUALTY_INCREASE = 60f;

    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getMinCrewMod().modifyMult(id, (MIN_CREW_MULT));
        stats.getCrewLossMult().modifyMult(id, 1f + CASUALTY_INCREASE * 0.01f);

    }
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize, ShipAPI ship) {
        if (index == 0) return Math.round((1f - MIN_CREW_MULT) * 100f) + "%";
        if (index == 1) return "" + (int) CASUALTY_INCREASE + "%";
        return null;
    }
}
