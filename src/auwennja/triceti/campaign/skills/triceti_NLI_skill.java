package auwennja.triceti.campaign.skills;

import com.fs.starfarer.api.characters.AfterShipCreationSkillEffect;
import com.fs.starfarer.api.characters.CharacterStatsSkillEffect;
import com.fs.starfarer.api.characters.LevelBasedEffect;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class triceti_NLI_skill {

    public static final float COMMAND_POINT_REGEN_PERCENT = 100f;
    public static final float COMMAND_POINT_BONUS = 2f;
    public static final float TERRAIN_PENALTY_REDUCTION_PERCENT = 30f;
    public static final float PEAK_TIME_REDUCTION = 60f;

    public static class Level1 implements CharacterStatsSkillEffect, AfterShipCreationSkillEffect {

        /*
         * Character and campaign-level effects.
         */
        @Override
        public void apply(
                MutableCharacterStatsAPI stats,
                String id,
                float level
        ) {
            /*
             * This stat uses 1f as +100% command-point regeneration.
             */
            stats.getDynamic()
                    .getStat(Stats.COMMAND_POINT_RATE_COMMANDER)
                    .modifyFlat(
                            id,
                            COMMAND_POINT_REGEN_PERCENT / 100f
                    );

            stats.getCommandPoints().modifyFlat(
                    id,
                    COMMAND_POINT_BONUS
            );

            /*
             * A 30% reduction means the terrain penalty is multiplied by 0.7.
             */
            float terrainPenaltyMult =
                    1f - TERRAIN_PENALTY_REDUCTION_PERCENT / 100f;

            stats.getDynamic()
                    .getStat(Stats.NAVIGATION_PENALTY_MULT)
                    .modifyMult(
                            id,
                            terrainPenaltyMult
                    );
        }

        @Override
        public void unapply(
                MutableCharacterStatsAPI stats,
                String id
        ) {
            stats.getDynamic()
                    .getStat(Stats.COMMAND_POINT_RATE_COMMANDER)
                    .unmodify(id);

            stats.getCommandPoints().unmodify(id);

            stats.getDynamic()
                    .getStat(Stats.NAVIGATION_PENALTY_MULT)
                    .unmodify(id);
        }

        /*
         * Fleetwide ship effect.
         */
        @Override
        public void apply(
                MutableShipStatsAPI stats,
                ShipAPI.HullSize hullSize,
                String id,
                float level
        ) {
            stats.getPeakCRDuration().modifyFlat(
                    id,
                    -PEAK_TIME_REDUCTION
            );
        }

        @Override
        public void unapply(
                MutableShipStatsAPI stats,
                ShipAPI.HullSize hullSize,
                String id
        ) {
            stats.getPeakCRDuration().unmodify(id);
        }

        @Override
        public String getEffectDescription(float level) {
            return "+"
                    + (int) COMMAND_POINT_REGEN_PERCENT
                    + "% command point regeneration rate, +"
                    + (int) COMMAND_POINT_BONUS
                    + " starting command points, -"
                    + (int) TERRAIN_PENALTY_REDUCTION_PERCENT
                    + "% terrain movement penalty, and -"
                    + (int) PEAK_TIME_REDUCTION
                    + " seconds peak operating time";
        }

        @Override
        public String getEffectPerLevelDescription() {
            return null;
        }

        @Override
        public LevelBasedEffect.ScopeDescription getScopeDescription() {
            return LevelBasedEffect.ScopeDescription.ALL_SHIPS;
        }

        @Override
        public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {

        }

        @Override
        public void unapplyEffectsAfterShipCreation(ShipAPI ship, String id) {

        }
    }
}
