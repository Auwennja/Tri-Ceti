package auwennja.triceti.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;

public class triceti_amplified_weapon_capacitors extends BaseHullMod {
    public static float TURRET_SPEED_BONUS = 50f;
    public static float RECOIL_BONUS = 25f;
    public static float BEAM_DAMAGE_INCREASE = 25f;
    public static float Missile_Turn = 100f;

    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getWeaponTurnRateBonus().modifyPercent(id, TURRET_SPEED_BONUS);
        stats.getBeamWeaponTurnRateBonus().modifyPercent(id, TURRET_SPEED_BONUS);
        stats.getMaxRecoilMult().modifyMult(id, 1f - (0.01f * RECOIL_BONUS));
        stats.getBeamWeaponDamageMult().modifyPercent(id, + BEAM_DAMAGE_INCREASE);
        stats.getMissileMaxTurnRateBonus().modifyPercent(id, -Missile_Turn);
    }

    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0) return "" + (int) TURRET_SPEED_BONUS + "%";
        if (index == 1) return "" + (int) RECOIL_BONUS + "%";
        if (index == 2) return "" + (int) BEAM_DAMAGE_INCREASE + "%";
        return null;
    }
}
