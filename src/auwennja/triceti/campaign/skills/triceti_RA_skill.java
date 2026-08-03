package auwennja.triceti.campaign.skills;

import com.fs.starfarer.api.characters.AfterShipCreationSkillEffect;
import com.fs.starfarer.api.characters.LevelBasedEffect;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;

public class triceti_RA_skill {

    public static final float WEAPON_TURN_RATE_BONUS = 10f;
    public static final float SYSTEM_CHARGE_REGEN_BONUS = 50f;
    public static final float SYSTEM_COOLDOWN_REDUCTION_PERCENT = 50f;
    public static final float MAX_CR_REDUCTION_PERCENT = 15f;

    public static class Level1 implements AfterShipCreationSkillEffect {

        @Override
        public void apply(
                MutableShipStatsAPI stats,
                ShipAPI.HullSize hullSize,
                String id,
                float level
        ) {
            stats.getWeaponTurnRateBonus().modifyPercent(
                    id,
                    WEAPON_TURN_RATE_BONUS
            );

            stats.getBeamWeaponTurnRateBonus().modifyPercent(
                    id,
                    WEAPON_TURN_RATE_BONUS
            );

            /*
             * Applies to systems that regenerate charges.
             */
            stats.getSystemRegenBonus().modifyPercent(
                    id,
                    SYSTEM_CHARGE_REGEN_BONUS
            );

            /*
             * A multiplier of 0.5 produces a 50% cooldown reduction.
             */
            float cooldownMultiplier =
                    1f - SYSTEM_COOLDOWN_REDUCTION_PERCENT / 100f;

            stats.getSystemCooldownBonus().modifyMult(
                    id,
                    cooldownMultiplier
            );

            /*
             * Maximum CR is represented as a decimal:
             * -0.15f means -15 percentage points.
             */
            stats.getMaxCombatReadiness().modifyFlat(
                    id,
                    -MAX_CR_REDUCTION_PERCENT / 100f
            );
        }

        @Override
        public void unapply(
                MutableShipStatsAPI stats,
                ShipAPI.HullSize hullSize,
                String id
        ) {
            stats.getWeaponTurnRateBonus().unmodify(id);
            stats.getBeamWeaponTurnRateBonus().unmodify(id);
            stats.getSystemRegenBonus().unmodify(id);
            stats.getSystemCooldownBonus().unmodify(id);
            stats.getMaxCombatReadiness().unmodify(id);
        }

        @Override
        public String getEffectDescription(float level) {
            return "+"
                    + (int) WEAPON_TURN_RATE_BONUS
                    + "% weapon turn rate, +"
                    + (int) SYSTEM_CHARGE_REGEN_BONUS
                    + "% ship-system charge regeneration, -"
                    + (int) SYSTEM_COOLDOWN_REDUCTION_PERCENT
                    + "% ship-system cooldown, and -"
                    + (int) MAX_CR_REDUCTION_PERCENT
                    + "% maximum combat readiness";
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
