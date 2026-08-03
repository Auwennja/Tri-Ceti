package auwennja.triceti.combat;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import org.lazywizard.lazylib.MathUtils;


import java.util.Random;

public class triceti_cetevska_mandible
        implements EveryFrameWeaponEffectPlugin {

    private static final float MIN_OFFSET = -18f;
    private static final float MAX_OFFSET = 18f;

    private static final float MIN_TURN_SPEED = 7f;
    private static final float MAX_TURN_SPEED = 20f;

    private static final float MIN_PAUSE = 0.5f;
    private static final float MAX_PAUSE = 4.5f;

    private static final String STATE_KEY_PREFIX =
            "$triceti_mandible_state_";

    @Override
    public void advance(
            float amount,
            CombatEngineAPI engine,
            WeaponAPI weapon
    ) {
        if (engine == null || engine.isPaused()) return;
        if (weapon == null) return;

        ShipAPI ship = weapon.getShip();

        if (ship == null || !ship.isAlive()) return;
        if (weapon.getSlot() == null) return;

        /*
         * The slot ID uniquely identifies each appendage on this ship.
         *
         * Including the weapon ID prevents collisions if another decorative
         * weapon later uses the same slot ID in a different configuration.
         */
        String stateKey =
                STATE_KEY_PREFIX
                        + weapon.getSlot().getId()
                        + "_"
                        + weapon.getSpec().getWeaponId();

        MandibleState state =
                (MandibleState) ship.getCustomData().get(stateKey);

        if (state == null) {
            state = new MandibleState(weapon);
            ship.setCustomData(stateKey, state);
        }

        float baseAngle =
                ship.getFacing() + state.neutralSlotAngle;

        float desiredAngle =
                MathUtils.clampAngle(
                        baseAngle + state.targetOffset
                );

        float currentAngle = weapon.getCurrAngle();

        float remainingRotation =
                MathUtils.getShortestRotation(
                        currentAngle,
                        desiredAngle
                );

        float maximumMovement =
                state.turnSpeed * amount;

        float movement = clamp(
                remainingRotation,
                -maximumMovement,
                maximumMovement
        );

        weapon.setCurrAngle(
                MathUtils.clampAngle(
                        currentAngle + movement
                )
        );

        /*
         * Once the appendage reaches its pose, it remains there for a
         * randomized interval before selecting another pose.
         */
        if (Math.abs(remainingRotation) <= 0.35f) {
            state.pauseRemaining -= amount;

            if (state.pauseRemaining <= 0f) {
                state.chooseNextPose();
            }
        }

        weapon.setForceNoFireOneFrame(true);
    }

    private static class MandibleState {

        private final Random random;
        private final float neutralSlotAngle;

        private float targetOffset;
        private float turnSpeed;
        private float pauseRemaining;

        private MandibleState(WeaponAPI weapon) {
            neutralSlotAngle = weapon.getSlot().getAngle();

            /*
             * The ship ID, slot ID, weapon ID, and current time produce
             * independent sequences for every physical appendage.
             */
            long seed = System.nanoTime();

            ShipAPI ship = weapon.getShip();

            if (ship != null && ship.getId() != null) {
                seed ^= ship.getId().hashCode();
            }

            if (weapon.getSlot().getId() != null) {
                seed ^= ((long) weapon.getSlot()
                        .getId()
                        .hashCode()) << 32;
            }

            if (weapon.getSpec() != null
                    && weapon.getSpec().getWeaponId() != null) {
                seed ^= weapon.getSpec()
                        .getWeaponId()
                        .hashCode();
            }

            random = new Random(seed);

            chooseInitialPose();
        }

        private void chooseInitialPose() {
            targetOffset = randomRange(
                    MIN_OFFSET,
                    MAX_OFFSET
            );

            turnSpeed = randomRange(
                    MIN_TURN_SPEED,
                    MAX_TURN_SPEED
            );

            /*
             * This delay is used after reaching the initial pose.
             * The initial movement itself begins immediately.
             */
            pauseRemaining = randomRange(
                    0f,
                    MAX_PAUSE
            );
        }

        private void chooseNextPose() {
            float previousOffset = targetOffset;

            /*
             * Avoid selecting a new angle nearly identical to the previous
             * one, which could make an appendage appear inactive.
             */
            do {
                targetOffset = randomRange(
                        MIN_OFFSET,
                        MAX_OFFSET
                );
            } while (Math.abs(targetOffset - previousOffset) < 5f);

            turnSpeed = randomRange(
                    MIN_TURN_SPEED,
                    MAX_TURN_SPEED
            );

            pauseRemaining = randomRange(
                    MIN_PAUSE,
                    MAX_PAUSE
            );
        }

        private float randomRange(
                float minimum,
                float maximum
        ) {
            return minimum
                    + random.nextFloat()
                    * (maximum - minimum);
        }
    }

    private static float clamp(
            float value,
            float minimum,
            float maximum
    ) {
        return Math.max(
                minimum,
                Math.min(maximum, value)
        );
    }
}