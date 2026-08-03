package auwennja.triceti.hullmods;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import com.fs.starfarer.api.impl.hullmods.BaseLogisticsHullMod;

public class triceti_fibrosteel extends BaseLogisticsHullMod {

    /**
     * Fraction of maximum hull regenerated per second.
     *
     * 0.005f = 0.5% maximum hull per second.
     */
    public static final float REGEN_RATE = 0.005f;

    /**
     * Maximum hull level the ship can regenerate to.
     *
     * 1f allows regeneration to full hull.
     */
    public static final float MAX_REGEN_LEVEL = 1f;

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        if (!ship.hasListenerOfClass(MyoRegen.class)) {
            ship.addListener(new MyoRegen(ship));
        }
    }
    public void unapplyEffectsAfterShipCreation(ShipAPI ship, String id) {
        ship.removeListenerOfClass(MyoRegen.class);
    }

    public static class MyoRegen implements AdvanceableListener {

        private final ShipAPI ship;

        public MyoRegen(ShipAPI ship) {
            this.ship = ship;
        }

        @Override
        public void advance(float amount) {
            if (ship == null) return;
            if (!ship.isAlive()) return;
            if (ship.isHulk()) return;

            float maxHull = ship.getMaxHitpoints();
            float targetHull = maxHull * MAX_REGEN_LEVEL;
            float currentHull = ship.getHitpoints();

            if (currentHull >= targetHull) return;

            /*
             * Do not round this value.
             *
             * Rounding per frame would make regeneration dependent on
             * maximum hull, frame rate, and combat time acceleration.
             */
            float repairAmount = maxHull * REGEN_RATE * amount;

            /*
             * Prevent regeneration from exceeding the configured target.
             */
            repairAmount = Math.min(
                    repairAmount,
                    targetHull - currentHull
            );

            if (repairAmount <= 0f) return;

            ship.setHitpoints(currentHull + repairAmount);
        }
    }

    @Override
    public String getDescriptionParam(
            int index,
            ShipAPI.HullSize hullSize,
            ShipAPI ship
    ) {
        if (index == 0) {
            return formatPercent(REGEN_RATE);
        }

        return null;
    }

    private static String formatPercent(float fraction) {
        float percent = fraction * 100f;

        if (percent == Math.round(percent)) {
            return Math.round(percent) + "%";
        }

        return percent + "%";
    }
}
