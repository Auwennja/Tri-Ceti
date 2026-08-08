package auwennja.triceti.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class triceti_energy_recirculation_network extends BaseHullMod {
    public static float VENT_RATE_BONUS = 50f;
    public static float PIERCE_MULT = 0f;
    public static float SHIELD_BONUS = 25f;

    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getVentRateMult().modifyPercent(id, VENT_RATE_BONUS);
        stats.getShieldDamageTakenMult().modifyMult(id, 1f - SHIELD_BONUS * 0.01f);
        stats.getDynamic().getStat(Stats.SHIELD_PIERCED_MULT).modifyMult(id, PIERCE_MULT);
    }

    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0) return "" + (int) VENT_RATE_BONUS + "%";
        if (index == 1) return "" + (int) SHIELD_BONUS + "%";
        return null;
    }
}
