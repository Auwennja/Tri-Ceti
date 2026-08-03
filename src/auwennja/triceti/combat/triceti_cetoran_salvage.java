package auwennja.triceti.combat;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.ShipAPI;

public class triceti_cetoran_salvage implements BeamEffectPlugin {
    public static final String CETORAN_HEAL_MULT_STAT = "cetoran_heal_mult_stat";
    public static float HEAL_AMOUNT = 1000f;

    private IntervalUtil fireInterval = new IntervalUtil(0.1f, 0.5f);
    private boolean wasZero = true;

    public static float getHealMult(ShipAPI ship) {
        return ship.getMutableStats()
                .getDynamic()
                .getValue(CETORAN_HEAL_MULT_STAT, 0f);
    }

    public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
        CombatEntityAPI target = beam.getDamageTarget();
        if (target instanceof ShipAPI targetShip && beam.getBrightness() >= 1f) {
            float dur = beam.getDamage().getDpsDuration();
            // needed because when the ship is in fast-time, dpsDuration will not be reset every frame as it should be
            if (!wasZero) dur = 0;
            wasZero = beam.getDamage().getDpsDuration() <= 0;
            fireInterval.advance(dur);

            if (fireInterval.intervalElapsed()) {
                boolean hitShield = targetShip.getShield() != null
                        && targetShip.getShield().isOn()
                        && targetShip.getShield().isWithinArc(beam.getRayEndPrevFrame());

                if (!hitShield) {
                    ShipAPI source = beam.getSource();

                    WeightedRandomPicker<ShipAPI> healTargets = new WeightedRandomPicker<>();
                    WeightedRandomPicker<ShipAPI> healNeedLess = new WeightedRandomPicker<>();
                    for (ShipAPI other : Global.getCombatEngine().getShips()) {
                        if (other.isHulk()) continue;
                        if (other.isFighter()) continue;
                        if (other.getOwner() != source.getOwner()) continue;

                        if (getHealMult(other) <= 0) continue;

                        float missingHp = other.getMaxHitpoints() - other.getHitpoints();
                        if (missingHp <= 0f) continue;

                        if (missingHp < HEAL_AMOUNT * 0.7f) {
                            healNeedLess.add(other, missingHp);
                        } else {
                            healTargets.add(other, missingHp);
                        }
                    }

                    ShipAPI toHeal = healTargets.pick();
                    if (toHeal == null) toHeal = healNeedLess.pick();
                    if (toHeal != null) {
                        float healAmount = HEAL_AMOUNT;
                        healAmount *= getHealMult(toHeal);
                        toHeal.setHitpoints(Math.min(toHeal.getMaxHitpoints(), toHeal.getHitpoints() + healAmount));
                    }

                }
            }
        }
    }
}
