package auwennja.triceti.hullmods;

import auwennja.triceti.combat.triceti_cetoran_salvage;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.hullmods.BaseLogisticsHullMod;

public class triceti_riveted_plating extends BaseLogisticsHullMod {
    public static float SUPPLY_USE_MULT = 0.5f;
    public static float REPAIR_RATE_BONUS = 50f;
    public static float REPAIR_BONUS = 50f;
    public static float CR_RECOVERY_BONUS = 50f;
    public static float HEAL_MULT = 0.5f;

    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        float bonus = REPAIR_BONUS;
        stats.getSuppliesPerMonth().modifyPercent(id, (SUPPLY_USE_MULT));
        stats.getBaseCRRecoveryRatePercentPerDay().modifyPercent(id, CR_RECOVERY_BONUS);
        stats.getRepairRatePercentPerDay().modifyPercent(id, REPAIR_RATE_BONUS);
        stats.getCombatEngineRepairTimeMult().modifyMult(id, 1f - bonus * 0.01f);
        stats.getCombatWeaponRepairTimeMult().modifyMult(id, 1f - bonus * 0.01f);
        stats.getDynamic().getMod(triceti_cetoran_salvage.CETORAN_HEAL_MULT_STAT).modifyFlat(id, HEAL_MULT);

    }
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize, ShipAPI ship) {
        if (index == 0) return "" + (SUPPLY_USE_MULT * 100f) + "%";
        if (index == 1) return "" + (int) Math.round(REPAIR_RATE_BONUS) + "%";
        if (index == 2) return "" + (int) Math.round(CR_RECOVERY_BONUS) + "%";
        if (index == 3) return "" + (int) Math.round(HEAL_MULT * 100f) + "%";
        return null;
    }
}
