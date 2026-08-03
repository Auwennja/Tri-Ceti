package auwennja.triceti.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.loading.WeaponSlotAPI;
import org.lazywizard.lazylib.MathUtils;

import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;

public class triceti_cetevska_arm_movement extends BaseHullMod {

    /**
     * Maximum deviation from the arm's original module-slot angle.
     */
    private static final float MAX_DEFLECTION = 20f;

    /**
     * How strongly Mati's angular velocity pushes the arms backwards.
     */
    private static final float TURN_LAG_MULT = 0.75f;

    /**
     * How quickly the slot angle approaches its target.
     */
    private static final float FOLLOW_SPEED = 4f;

    /**
     * Original module-slot angle for each arm.
     */
    private final Map<ShipAPI, Float> neutralAngles =
            new WeakHashMap<>();

    @Override
    public void advanceInCombat(ShipAPI parent, float amount) {
        if (parent == null || !parent.isAlive()) return;

        for (ShipAPI arm : parent.getChildModulesCopy()) {
            if (arm == null || !arm.isAlive()) continue;

            /*
             * Only affect your Cetevska arm modules.
             */
            String hullId = arm.getHullSpec().getHullId();

            if (!"triceti_cetevska_arm_L".equals(hullId)
                    && !"triceti_cetevska_arm_R".equals(hullId)) {
                continue;
            }

            /*
             * Module-slot specifications may otherwise be shared.
             */
            arm.ensureClonedStationSlotSpec();

            WeaponSlotAPI slot = arm.getStationSlot();
            if (slot == null) continue;

            Float neutralAngle = neutralAngles.get(arm);

            if (neutralAngle == null) {
                neutralAngle = slot.getAngle();
                neutralAngles.put(arm, neutralAngle);
            }

            /*
             * When Mati turns clockwise, the arms deflect anticlockwise,
             * and vice versa.
             */
            float desiredDeflection =
                    -parent.getAngularVelocity() * TURN_LAG_MULT;

            desiredDeflection = clamp(
                    desiredDeflection,
                    -MAX_DEFLECTION,
                    MAX_DEFLECTION
            );

            float desiredSlotAngle =
                    MathUtils.clampAngle(
                            neutralAngle + desiredDeflection
                    );

            float currentSlotAngle = slot.getAngle();

            float difference =
                    MathUtils.getShortestRotation(
                            currentSlotAngle,
                            desiredSlotAngle
                    );

            float step = Math.min(1f, FOLLOW_SPEED * amount);

            slot.setAngle(
                    MathUtils.clampAngle(
                            currentSlotAngle + difference * step
                    )
            );
        }
    }

    private static float clamp(
            float value,
            float minimum,
            float maximum
    ) {
        return Math.max(minimum, Math.min(maximum, value));
    }
    private static final String FANG_STATE_PREFIX =
            "$triceti_arm_fang_state_";

    private static final float FANG_MIN_OFFSET = -18f;
    private static final float FANG_MAX_OFFSET = 18f;

    private static final float FANG_MIN_SPEED = 7f;
    private static final float FANG_MAX_SPEED = 20f;

    private static final float FANG_MIN_PAUSE = 0.5f;
    private static final float FANG_MAX_PAUSE = 4.5f;


    private static void advanceArmDecorations(
            ShipAPI arm,
            float amount
    ) {
        for (WeaponAPI weapon : arm.getAllWeapons()) {
            if (weapon == null || weapon.getSpec() == null) continue;
            if (weapon.getSlot() == null) continue;

            String weaponId = weapon.getSpec().getWeaponId();

            /*
             * Only control the decorative fang mounted at the end
             * of the arm module.
             */
            if (!"triceti_cetevska_fang_L".equals(weaponId)
                    && !"triceti_cetevska_fang_R".equals(weaponId)) {
                continue;
            }

            String stateKey =
                    FANG_STATE_PREFIX + weapon.getSlot().getId();

            FangState state =
                    (FangState) arm.getCustomData().get(stateKey);

            if (state == null) {
                state = new FangState(arm, weapon);
                arm.setCustomData(stateKey, state);
            }

            float baseAngle =
                    arm.getFacing() + state.neutralSlotAngle;

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

            if (Math.abs(remainingRotation) <= 0.35f) {
                state.pauseRemaining -= amount;

                if (state.pauseRemaining <= 0f) {
                    state.chooseNextPose();
                }
            }

            weapon.setForceNoFireOneFrame(true);
        }
    }


    private static class FangState {

        private final Random random;
        private final float neutralSlotAngle;

        private float targetOffset;
        private float turnSpeed;
        private float pauseRemaining;

        private FangState(
                ShipAPI arm,
                WeaponAPI weapon
        ) {
            neutralSlotAngle = weapon.getSlot().getAngle();

            long seed = System.nanoTime();

            if (arm.getId() != null) {
                seed ^= arm.getId().hashCode();
            }

            if (weapon.getSlot().getId() != null) {
                seed ^= ((long) weapon.getSlot()
                        .getId()
                        .hashCode()) << 32;
            }

            seed ^= System.identityHashCode(weapon);

            random = new Random(seed);

            targetOffset = randomRange(
                    FANG_MIN_OFFSET,
                    FANG_MAX_OFFSET
            );

            turnSpeed = randomRange(
                    FANG_MIN_SPEED,
                    FANG_MAX_SPEED
            );

            pauseRemaining = randomRange(
                    0f,
                    FANG_MAX_PAUSE
            );
        }

        private void chooseNextPose() {
            float previousOffset = targetOffset;

            do {
                targetOffset = randomRange(
                        FANG_MIN_OFFSET,
                        FANG_MAX_OFFSET
                );
            } while (Math.abs(targetOffset - previousOffset) < 5f);

            turnSpeed = randomRange(
                    FANG_MIN_SPEED,
                    FANG_MAX_SPEED
            );

            pauseRemaining = randomRange(
                    FANG_MIN_PAUSE,
                    FANG_MAX_PAUSE
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
}