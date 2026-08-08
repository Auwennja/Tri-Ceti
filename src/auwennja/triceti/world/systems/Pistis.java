package auwennja.triceti.world.systems;

import auwennja.triceti.ModPlugin;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;

import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.RemnantStationFleetManager;
import com.fs.starfarer.api.impl.campaign.procgen.themes.RemnantThemeGenerator;

import com.fs.starfarer.api.util.DelayedActionScript;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


public class Pistis implements SectorGeneratorPlugin {

    private final float width =
            Global.getSettings().getFloat("sectorWidth");

    private final float height =
            Global.getSettings().getFloat("sectorHeight");


    @Override
    public void generate(SectorAPI sector) {

        StarSystemAPI system_PIS =
                sector.createStarSystem("Pistis");

        /*
         * Position this wherever you prefer.
         * This puts it in roughly the same general region
         * as your other Tri-Ceti systems.
         */
        system_PIS.getLocation().set(
                ((width / 2f) * 0.8f) - 23000f,
                ((-height / 2f) * 0.95f)
        );

        system_PIS.setBackgroundTextureFilename(
                "graphics/backgrounds/background4.jpg"
        );


        /*
         * ============================================================
         * STAR
         * ============================================================
         */

        PlanetAPI star = system_PIS.initStar(
                "pistis",
                StarTypes.RED_DWARF,
                450f,
                350f
        );

        system_PIS.setLightColor(
                new java.awt.Color(170, 125, 100)
        );


        /*
         * ============================================================
         * CAERUS
         * ============================================================
         */

        PlanetAPI caerus = system_PIS.addPlanet(
                "caerus",
                star,
                "Caerus",
                Planets.ROCKY_UNSTABLE,
                30f,
                130f,
                1800f,
                120f
        );

        caerus.getMarket().addCondition(
                Conditions.NO_ATMOSPHERE
        );

        caerus.getMarket().addCondition(
                Conditions.VERY_HOT
        );

        caerus.getMarket().addCondition(
                Conditions.TECTONIC_ACTIVITY
        );

        caerus.getMarket().addCondition(
                Conditions.HIGH_GRAVITY
        );

        caerus.getMarket().addCondition(
                Conditions.ORE_MODERATE
        );


        /*
         * ============================================================
         * PEPROMENE
         * ============================================================
         */

        PlanetAPI pepromene = system_PIS.addPlanet(
                "pepromene",
                star,
                "Pepromene",
                Planets.BARREN,
                170f,
                160f,
                3300f,
                240f
        );

        pepromene.getMarket().addCondition(
                Conditions.NO_ATMOSPHERE
        );

        pepromene.getMarket().addCondition(
                Conditions.COLD
        );

        pepromene.getMarket().addCondition(
                Conditions.ORE_ABUNDANT
        );

        pepromene.getMarket().addCondition(
                Conditions.RARE_ORE_MODERATE
        );

        PlanetAPI feronia = system_PIS.addPlanet(
                "feronia",
                star,
                "Feronia",
                Planets.ARID,
                270f,
                190f,
                5200f,
                430f
        );

        feronia.setCustomDescriptionId(
                "feroniadesc"
        );


        MarketAPI feroniamarket =
                auwennja.triceti.world.systems.triceti_AddMarket
                        .addMarketplace(
                                ModPlugin.cetora,
                                feronia,
                                null,
                                "Feronia",
                                4,

                                new ArrayList<>(Arrays.asList(
                                        Conditions.POPULATION_4,
                                        Conditions.HABITABLE,
                                        Conditions.HOT,
                                        Conditions.ORE_ABUNDANT,
                                        Conditions.RARE_ORE_SPARSE,
                                        "triceti_ai_logistics_network"
                                )),

                                new ArrayList<>(Arrays.asList(
                                        Submarkets.SUBMARKET_OPEN,
                                        Submarkets.SUBMARKET_STORAGE,
                                        Submarkets.SUBMARKET_BLACK
                                )),

                                new ArrayList<>(Arrays.asList(
                                        Industries.POPULATION,
                                        Industries.SPACEPORT,
                                        Industries.STARFORTRESS_MID,
                                        Industries.GROUNDDEFENSES,
                                        Industries.PATROLHQ,
                                        Industries.MINING,
                                        Industries.REFINING
                                )),

                                true,
                                false
                        );


        StarSystemGenerator.addStableLocations(
                system_PIS,
                3
        );


        /*
         * ============================================================
         * REMNANT NEXUS
         *
         * Orbits Pepromene.
         * ============================================================
         */

        createRemnantNexus(
                system_PIS,
                pepromene
        );


        /*
         * ============================================================
         * JUMP POINTS
         * ============================================================
         */

        system_PIS.autogenerateHyperspaceJumpPoints(
                true,
                true
        );
    }


    /**
     * Creates a fully operational Remnant Nexus and gives it
     * a fleet manager so it can generate Remnant patrols.
     */
    private void createRemnantNexus(
            final StarSystemAPI system,
            PlanetAPI orbitTarget
    ) {

        final Random random =
                StarSystemGenerator.random;


        /*
         * Create an empty Remnant station fleet.
         */
        final CampaignFleetAPI nexus =
                FleetFactoryV3.createEmptyFleet(
                        Factions.REMNANTS,
                        FleetTypes.BATTLESTATION,
                        null
                );


        /*
         * Fully operational Remnant Nexus.
         */
        FleetMemberAPI member =
                Global.getFactory().createFleetMember(
                        FleetMemberType.SHIP,
                        "remnant_station2_Standard"
                );

        nexus.getFleetData().addFleetMember(member);


        /*
         * Station behavior.
         */
        nexus.setStationMode(true);

        nexus.getMemoryWithoutUpdate().set(
                MemFlags.MEMORY_KEY_MAKE_AGGRESSIVE,
                true
        );

        nexus.getMemoryWithoutUpdate().set(
                MemFlags.MEMORY_KEY_NO_JUMP,
                true
        );

        nexus.getMemoryWithoutUpdate().set(
                MemFlags.MEMORY_KEY_MAKE_ALLOW_DISENGAGE,
                true
        );

        nexus.addTag(Tags.NEUTRINO_HIGH);


        /*
         * Gives it the normal Remnant Nexus interaction behavior.
         */
        RemnantThemeGenerator
                .addRemnantStationInteractionConfig(nexus);


        /*
         * Remnant stations normally don't fly around like fleets.
         */
        nexus.clearAbilities();

        nexus.addAbility(
                Abilities.TRANSPONDER
        );

        nexus.getAbility(
                Abilities.TRANSPONDER
        ).activate();

        nexus.getDetectedRangeMod().modifyFlat(
                "gen",
                1000f
        );

        nexus.setAI(null);


        /*
         * Add it to Pistis before assigning the orbit.
         */
        system.addEntity(nexus);


        /*
         * Orbit Pepromene.
         */
        nexus.setCircularOrbitWithSpin(
                orbitTarget,
                90f,
                450f,
                120f,
                5f,
                5f
        );



        /*
         * Nexus patrol generation.
         *
         * The manager is added after a short delay,
         * matching the way Remnant Nexus logic is
         * normally initialized.
         */
        system.addScript(
                new DelayedActionScript(1f) {

                    @Override
                    public void doAction() {

                        int maxFleets = 6;

                        RemnantStationFleetManager manager =
                                new RemnantStationFleetManager(
                                        nexus,
                                        1f,
                                        0,
                                        maxFleets,
                                        15f,
                                        8,
                                        24
                                );

                        system.addScript(manager);
                    }
                }
        );
    }
}