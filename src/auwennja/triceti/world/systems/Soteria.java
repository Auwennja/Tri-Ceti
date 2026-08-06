package auwennja.triceti.world.systems;

import auwennja.triceti.ModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Soteria implements SectorGeneratorPlugin {
    float width = Global.getSettings().getFloat("sectorWidth");
    float height = Global.getSettings().getFloat("sectorHeight");

    @Override
    public void generate(SectorAPI sector) {
        StarSystemAPI systemSOT = sector.createStarSystem("Soteria");
        LocationAPI hyper = Global.getSector().getHyperspace();

        systemSOT.setBackgroundTextureFilename("graphics/backgrounds/background2.jpg");
        Random random = StarSystemGenerator.random;
        systemSOT.getLocation().set(((width/2) * 0.8f) - 19000, -height/2 * 0.9f);

        PlanetAPI star = systemSOT.initStar( //stars and planets are technically the same category of object, so stars use PlanetAPI
                "Soteria", //set star id, this should be unique
                "star_orange", //set star type, the type IDs come from starsector-core/data/campaign/procgen/star_gen_data.csv
                1400, //set radius, 900 is a typical radius size
                ((width/2) * 0.8f) - 19000, //sets the location of the star's one-way jump point in hyperspace, since it is the center of the star system, we want it to be in the center of the star system jump points in hyperspace
                -height/2 * 0.9f,
                900 //radius of corona terrain around star
        );

        float innerOrbitDistance = StarSystemGenerator.addOrbitingEntities(
                systemSOT, //star system variable, used to add entities
                star, //focus object for entities to orbit
                StarAge.AVERAGE, //used by generator to decide which kind of planets to add
                0, //minimum number of entities
                0, //maximum number of entities
                6500, //the radius between the first generated entity and the focus object, in this case the star
                1, //used to assign roman numerals to the generated entities if not given special names
                true //generator will give unique names like "Ordog" instead of "Example Star System III"
        );

        PlanetAPI planetOne = systemSOT.addPlanet( //assigns instance of newly created planet to variable planetOne
                "Ollon", //unique id string
                star, //orbit focus for planet
                "Ollon", //display name of planet
                "ice_giant", //planet type id, comes from starsector-core/data/campaign/procgen/planet_gen_data.csv
                190f, //starting angle in orbit
                300f, //planet size
                4100, //1500 radius gap from the outer randomly generated entity created above
                1095 //number of in-game days for it to orbit once
        );
        planetOne.setCustomDescriptionId("ollondesc");
        planetOne.getMarket().addCondition(Conditions.VOLATILES_PLENTIFUL);

        SectorEntityToken stationabandoned_SOT = systemSOT.addCustomEntity("stationabandoned_SOT", "Abandoned Siphon-Station", "station_mining00", Factions.NEUTRAL);
        stationabandoned_SOT.setCircularOrbit(planetOne, 180f, 430f, 156f);
        stationabandoned_SOT.setCustomDescriptionId("stationabandoneddesc");
        stationabandoned_SOT.setInteractionImage("illustrations", "orbital_construction");
        Misc.setAbandonedStationMarket("stationabandoned_SOT_market", stationabandoned_SOT);


        PlanetAPI planetTwo = systemSOT.addPlanet( //assigns instance of newly created planet to variable planetOne
                "Briga", //unique id string
                planetOne, //orbit focus for planet
                "Briga", //display name of planet
                "barren", //planet type id, comes from starsector-core/data/campaign/procgen/planet_gen_data.csv
                55f, //starting angle in orbit
                80f, //planet size
                680, //1500 radius gap from the outer randomly generated entity created above
                365 //number of in-game days for it to orbit once
        );
        planetTwo.setCustomDescriptionId("brigadesc");
        planetTwo.getMarket().addCondition(Conditions.RUINS_EXTENSIVE);

        SectorEntityToken stationOne = systemSOT.addCustomEntity("reda", "Reda", "station_midline1", ModPlugin.cetora);
        stationOne.setCircularOrbit(planetTwo, 10f, 200, 145);
        stationOne.setCustomDescriptionId("redadesc");
        stationOne.setInteractionImage("illustrations", "reda");

        MarketAPI redamarket = auwennja.triceti.world.systems.triceti_AddMarket.addMarketplace(
                ModPlugin.cetora,
                stationOne,
                null,
                "Reda",
                5,
                new ArrayList<>(Arrays.asList( //List of conditions for this method to iterate through and add to the market
                        Conditions.POPULATION_5,
                        Conditions.HABITABLE,
                        Conditions.VOLATILES_TRACE,
                        Conditions.ORE_SPARSE,
                        "triceti_ai_logistics_network"
                )),
                new ArrayList<>(Arrays.asList( //list of submarkets for this method to iterate through and add to the market. if a military base industry was added to this market, it would be consistent to add a military submarket too
                        Submarkets.SUBMARKET_OPEN, //add a default open market
                        Submarkets.SUBMARKET_STORAGE, //add a player storage market
                        Submarkets.SUBMARKET_BLACK, //add a black market
                        Submarkets.GENERIC_MILITARY
                )),
                new ArrayList<>(Arrays.asList( //list of industries for this method to iterate through and add to the market
                        Industries.POPULATION, //population industry is required for weirdness to not happen
                        Industries.MEGAPORT,
                        Industries.HEAVYBATTERIES,
                        Industries.WAYSTATION,
                        Industries.STARFORTRESS_MID,
                        Industries.MINING,
                        Industries.REFINING,
                        Industries.HEAVYINDUSTRY,
                        Industries.PATROLHQ,
                        "triceti_soylent_paste_facility"

                )),
                true,
                false
        );

        systemSOT.addAsteroidBelt(
                planetOne, //orbit focus
                30, //number of asteroid entities
                954, //orbit radius is 500 gap for outer randomly generated entity above
                255, //width of band
                35, //minimum and maximum visual orbit speeds of asteroids
                130,
                Terrain.ASTEROID_BELT, //ID of the terrain type that appears in the section above the abilities bar
                "Ogros Belt" //display name
        );

        //add a ring texture. it will go under the asteroid entities generated above
        systemSOT.addRingBand(planetOne,
                "misc", //used to access band texture, this is the name of a category in settings.json
                "rings_asteroids0", //specific texture id in category misc in settings.json
                256f, //texture width, can be used for scaling shenanigans
                2,
                Color.white, //colour tint
                256f, //band width in game
                954, //same as above
                200f,
                null,
                null
        );


        systemSOT.addAsteroidBelt(
                star, //orbit focus
                140, //number of asteroid entities
                innerOrbitDistance + 1000, //orbit radius is 500 gap for outer randomly generated entity above
                255, //width of band
                190, //minimum and maximum visual orbit speeds of asteroids
                220,
                Terrain.ASTEROID_BELT, //ID of the terrain type that appears in the section above the abilities bar
                "Vidu Belt" //display name
        );

        //add a ring texture. it will go under the asteroid entities generated above
        systemSOT.addRingBand(star,
                "misc", //used to access band texture, this is the name of a category in settings.json
                "rings_asteroids0", //specific texture id in category misc in settings.json
                256f, //texture width, can be used for scaling shenanigans
                2,
                Color.white, //colour tint
                256f, //band width in game
                innerOrbitDistance + 1000, //same as above
                200f,
                null,
                null
        );

        PlanetAPI planetThree = systemSOT.addPlanet( //assigns instance of newly created planet to variable planetOne
                "deiwo", //unique id string
                star, //orbit focus for planet
                "Deiwo", //display name of planet
                "tundra", //planet type id, comes from starsector-core/data/campaign/procgen/planet_gen_data.csv
                15f, //starting angle in orbit
                130f, //planet size
                innerOrbitDistance, //1500 radius gap from the outer randomly generated entity created above
                439 //number of in-game days for it to orbit once
        );
        planetThree.setCustomDescriptionId("deiwodesc");

        MarketAPI deiwomarket = auwennja.triceti.world.systems.triceti_AddMarket.addMarketplace(
                Factions.INDEPENDENT,
                planetThree,
                null,
                "Deiwo",
                3,
                new ArrayList<>(Arrays.asList( //List of conditions for this method to iterate through and add to the market
                        Conditions.POPULATION_3,
                        Conditions.HABITABLE,
                        Conditions.COLD,
                        Conditions.LOW_GRAVITY,
                        Conditions.METEOR_IMPACTS,
                        Conditions.ORE_SPARSE,
                        Conditions.FARMLAND_ADEQUATE
                )),
                new ArrayList<>(Arrays.asList( //list of submarkets for this method to iterate through and add to the market. if a military base industry was added to this market, it would be consistent to add a military submarket too
                        Submarkets.SUBMARKET_OPEN, //add a default open market
                        Submarkets.SUBMARKET_STORAGE, //add a player storage market
                        Submarkets.SUBMARKET_BLACK //add a black market
                )),
                new ArrayList<>(Arrays.asList( //list of industries for this method to iterate through and add to the market
                        Industries.POPULATION, //population industry is required for weirdness to not happen
                        Industries.SPACEPORT,
                        Industries.GROUNDDEFENSES,
                        Industries.WAYSTATION,
                        Industries.MINING,
                        Industries.FARMING
                )),
                true,
                false
        );

        //add makeshift comm relay entity to system
        SectorEntityToken relay = systemSOT.addCustomEntity(
                "makeshiftrelay_SOT",
                "Makeshift Relay",
                Entities.COMM_RELAY_MAKESHIFT,
                ModPlugin.cetora
        );
        //assign an orbit
        relay.setCircularOrbit(star, 270f, innerOrbitDistance + 700f, 400f); //assign an orbit

        //add domain sensor array
        SectorEntityToken sensorArray = systemSOT.addCustomEntity(
                "makeshiftsensor_SOT",
                "Sensor Array",
                Entities.SENSOR_ARRAY_MAKESHIFT,
                ModPlugin.cetora
        );
        //assign an orbit, point down ensures it rotates to point towards center while orbiting
        sensorArray.setCircularOrbitPointingDown(star, 90f, innerOrbitDistance - 500, 150f);

        //domain nav buoy
        SectorEntityToken navBuoy = systemSOT.addCustomEntity(
                "makeshiftnavbuoy_SOT",
                "Navigation Beacon",
                Entities.NAV_BUOY_MAKESHIFT,
                ModPlugin.cetora
        );
        //assign orbit, this time it is orbiting planetOne
        navBuoy.setCircularOrbitPointingDown(star, 0f, 1950f, 160f);

        JumpPointAPI SOTJumpPoint =
                Global.getFactory().createJumpPoint(
                        "triceti_SOT_jump_point",
                        "Inner System Jump-point"
                );

        SOTJumpPoint.setCircularOrbit(
                star,
                180f,
                1100f,
                80f
        );

        SOTJumpPoint.setStandardWormholeToHyperspaceVisual();

        systemSOT.addEntity(SOTJumpPoint);

        //autogenerate jump points that will appear in hyperspace and in system
        systemSOT.autogenerateHyperspaceJumpPoints(true, true);

        //the following is hyperspace cleanup code that will remove hyperstorm clouds around this system's location in hyperspace
        //don't need to worry about this, it's more or less copied from vanilla

        //set up hyperspace editor plugin
        HyperspaceTerrainPlugin hyperspaceTerrainPlugin = (HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin(); //get instance of hyperspace terrain
        NebulaEditor nebulaEditor = new NebulaEditor(hyperspaceTerrainPlugin); //object used to make changes to hyperspace nebula

        //set up radiuses in hyperspace of system
        float minHyperspaceRadius = hyperspaceTerrainPlugin.getTileSize() * 2f; //minimum radius is two 'tiles'
        float maxHyperspaceRadius = systemSOT.getMaxRadiusInHyperspace();

        //hyperstorm-b-gone (around system in hyperspace)
        nebulaEditor.clearArc(systemSOT.getLocation().x, systemSOT.getLocation().y, 0, minHyperspaceRadius + maxHyperspaceRadius, 0f, 360f, 0.25f);

        if (!redamarket.hasCondition(ModPlugin.AI_LOGISTICS_CONDITION)) {
            redamarket.addCondition(ModPlugin.AI_LOGISTICS_CONDITION);
        }

    }
}
