package auwennja.triceti.hullmods;

import com.fs.starfarer.api.GameState;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.HullModFleetEffect;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.hullmods.BaseLogisticsHullMod;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.util.EnumMap;
import java.util.Map;


@SuppressWarnings("unchecked")
public class triceti_adaptive_sensor_suite
        extends BaseLogisticsHullMod
        implements HullModFleetEffect {

    public static final String MOD_KEY =
            "triceti_adaptive_sensor_suite_fleet_bonus";

    /**
     * Campaign sensor contribution provided by each hull size.
     */
    private static final Map<ShipAPI.HullSize, Float>
            SENSOR_BONUS_BY_HULL_SIZE =
            new EnumMap<>(ShipAPI.HullSize.class);

    static {
        SENSOR_BONUS_BY_HULL_SIZE.put(
                ShipAPI.HullSize.FRIGATE,
                30f
        );

        SENSOR_BONUS_BY_HULL_SIZE.put(
                ShipAPI.HullSize.DESTROYER,
                40f
        );

        SENSOR_BONUS_BY_HULL_SIZE.put(
                ShipAPI.HullSize.CRUISER,
                60f
        );

        SENSOR_BONUS_BY_HULL_SIZE.put(
                ShipAPI.HullSize.CAPITAL_SHIP,
                100f
        );
    }

    @Override
    public void applyEffectsBeforeShipCreation(
            ShipAPI.HullSize hullSize,
            MutableShipStatsAPI stats,
            String id
    ) {
        float sensorBonus = getSensorBonus(hullSize);
        float weaponRangeBonus = sensorBonus * 0.5f;

        /*
         * Stores this ship's campaign sensor contribution.
         *
         * The fleet effect reads this value from every fleet member
         * and combines the contributions with diminishing returns.
         */
        stats.getDynamic()
                .getMod(Stats.HRS_SENSOR_RANGE_MOD)
                .modifyFlat(id, sensorBonus);

        /*
         * Combat weapon-range bonus.
         */
        stats.getBallisticWeaponRangeBonus()
                .modifyPercent(id, weaponRangeBonus);

        stats.getEnergyWeaponRangeBonus()
                .modifyPercent(id, weaponRangeBonus);
    }

    @Override
    public String getDescriptionParam(
            int index,
            ShipAPI.HullSize hullSize
    ) {
        if (index == 0) {
            return formatPercent(
                    getWeaponRangeBonus(ShipAPI.HullSize.FRIGATE)
            );
        }

        if (index == 1) {
            return formatPercent(
                    getWeaponRangeBonus(ShipAPI.HullSize.DESTROYER)
            );
        }

        if (index == 2) {
            return formatPercent(
                    getWeaponRangeBonus(ShipAPI.HullSize.CRUISER)
            );
        }

        if (index == 3) {
            return formatPercent(
                    getWeaponRangeBonus(
                            ShipAPI.HullSize.CAPITAL_SHIP
                    )
            );
        }

        return null;
    }

    @Override
    public boolean withAdvanceInCampaign() {
        return false;
    }

    @Override
    public void advanceInCampaign(CampaignFleetAPI fleet) {
        /*
         * Not needed because the effect is recalculated during fleet sync.
         */
    }

    @Override
    public boolean withOnFleetSync() {
        return true;
    }

    @Override
    public void onFleetSync(CampaignFleetAPI fleet) {
        if (fleet == null) return;

        float sensorBonus =
                getAdjustedSensorModifier(fleet, null, 0f);

        if (sensorBonus <= 0f) {
            fleet.getSensorRangeMod().unmodify(MOD_KEY);
            return;
        }

        fleet.getSensorRangeMod().modifyFlat(
                MOD_KEY,
                sensorBonus,
                "Ships with Adaptive Sensor Suites"
        );
    }

    @Override
    public void addPostDescriptionSection(
            TooltipMakerAPI tooltip,
            ShipAPI.HullSize hullSize,
            ShipAPI ship,
            float width,
            boolean isForModSpec
    ) {
        float opad = 10f;

        tooltip.addPara(
                "Each ship equipped with an Adaptive Sensor Suite "
                        + "increases the fleet's sensor range. "
                        + "Additional suites provide diminishing returns. "
                        + "Larger ships provide a greater sensor contribution.",
                opad
        );

        if (isForModSpec || ship == null) return;
        if (Global.getSettings().getCurrentState()
                == GameState.TITLE) {
            return;
        }

        if (Global.getSector() == null) return;

        CampaignFleetAPI fleet =
                Global.getSector().getPlayerFleet();

        if (fleet == null) return;

        float currentFleetBonus =
                getAdjustedSensorModifier(fleet, null, 0f);

        float currentShipContribution =
                getSensorBonus(hullSize);

        float fleetBonusWithAnother =
                getAdjustedSensorModifier(
                        fleet,
                        null,
                        currentShipContribution
                );

        String fleetMemberId = ship.getFleetMemberId();

        float fleetBonusWithoutThisShip =
                getAdjustedSensorModifier(
                        fleet,
                        fleetMemberId,
                        0f
                );

        tooltip.addPara(
                "The current fleet sensor-range increase is %s.",
                opad,
                Misc.getHighlightColor(),
                formatFlatValue(currentFleetBonus)
        );

        /*
         * Only mention removal when this exact ship can be identified
         * as an existing member of the fleet.
         */
        if (fleetMemberId != null
                && fleetBonusWithoutThisShip < currentFleetBonus) {

            tooltip.addPara(
                    "Removing this ship would reduce the increase to "
                            + "%s. Adding another ship of the same size "
                            + "would increase it to %s.",
                    opad,
                    Misc.getHighlightColor(),
                    formatFlatValue(fleetBonusWithoutThisShip),
                    formatFlatValue(fleetBonusWithAnother)
            );
        } else {
            tooltip.addPara(
                    "Adding another ship of the same size would "
                            + "increase it to %s.",
                    opad,
                    Misc.getHighlightColor(),
                    formatFlatValue(fleetBonusWithAnother)
            );
        }
    }

    public static float getAdjustedSensorModifier(
            CampaignFleetAPI fleet,
            String skippedFleetMemberId,
            float addedContribution
    ) {
        if (fleet == null) return 0f;

        float largestContribution = 0f;
        float totalContribution = 0f;

        for (FleetMemberAPI member
                : fleet.getFleetData().getMembersListCopy()) {

            if (member == null) continue;
            if (member.isMothballed()) continue;

            if (skippedFleetMemberId != null
                    && skippedFleetMemberId.equals(member.getId())) {
                continue;
            }

            float contribution = member.getStats()
                    .getDynamic()
                    .getMod(Stats.HRS_SENSOR_RANGE_MOD)
                    .computeEffective(0f);

            if (contribution <= 0f) continue;

            largestContribution = Math.max(
                    largestContribution,
                    contribution
            );

            totalContribution += contribution;
        }

        if (addedContribution > 0f) {
            largestContribution = Math.max(
                    largestContribution,
                    addedContribution
            );

            totalContribution += addedContribution;
        }

        if (largestContribution <= 0f) return 0f;

        float equivalentUnits =
                totalContribution / largestContribution;

        if (equivalentUnits <= 1f) {
            return largestContribution;
        }

        /*
         * Preserves your original diminishing-returns formula.
         */
        float diminishingReturnsMultiplier =
                Misc.logOfBase(2.5f, equivalentUnits) + 1f;

        float result =
                totalContribution
                        * diminishingReturnsMultiplier
                        / equivalentUnits;

        result = Math.max(0f, result);

        return Math.round(result * 100f) / 100f;
    }

    private static float getSensorBonus(
            ShipAPI.HullSize hullSize
    ) {
        Float bonus = SENSOR_BONUS_BY_HULL_SIZE.get(hullSize);

        if (bonus == null) {
            return 0f;
        }

        return bonus;
    }

    private static float getWeaponRangeBonus(
            ShipAPI.HullSize hullSize
    ) {
        return getSensorBonus(hullSize) * 0.5f;
    }

    private static String formatPercent(float value) {
        return Math.round(value) + "%";
    }

    private static String formatFlatValue(float value) {
        return String.valueOf(Math.round(value));
    }
}