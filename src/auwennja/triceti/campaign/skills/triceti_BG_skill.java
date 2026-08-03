package auwennja.triceti.campaign.skills;

import com.fs.starfarer.api.characters.AfterShipCreationSkillEffect;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;

public class triceti_BG_skill {

    public static final float PEAK_TIME_BONUS = 60f;

    public static class Level1 implements AfterShipCreationSkillEffect {

        @Override
        public void apply(
                MutableShipStatsAPI stats,
                ShipAPI.HullSize hullSize,
                String id,
                float level
        ) {
            stats.getPeakCRDuration().modifyFlat(id, PEAK_TIME_BONUS);
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
            return "+" + (int) PEAK_TIME_BONUS
                    + " seconds peak operating time";
        }

        @Override
        public String getEffectPerLevelDescription() {
            return null;
        }

        @Override
        public ScopeDescription getScopeDescription() {
            return ScopeDescription.ALL_SHIPS;
        }

        @Override
        public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {

        }

        @Override
        public void unapplyEffectsAfterShipCreation(ShipAPI ship, String id) {

        }
    }
}
