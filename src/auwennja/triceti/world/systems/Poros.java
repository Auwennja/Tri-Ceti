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

public class Poros implements SectorGeneratorPlugin {
    float width = Global.getSettings().getFloat("sectorWidth");
    float height = Global.getSettings().getFloat("sectorHeight");


    @Override
    public void generate(SectorAPI sector) {
        StarSystemAPI systemPOR = sector.createStarSystem("Poros");
        LocationAPI hyper = Global.getSector().getHyperspace();

        systemPOR.setBackgroundTextureFilename("graphics/backgrounds/background2.jpg");
        Random random = StarSystemGenerator.random;
        systemPOR.getLocation().set(((width/2) * 0.8f) + 3000, (-height/2 * 0.9f) + 8500);

        PlanetAPI star = systemPOR.initStar( //stars and planets are technically the same category of object, so stars use PlanetAPI
                "Poros", //set star id, this should be unique
                "star_white", //set star type, the type IDs come from starsector-core/data/campaign/procgen/star_gen_data.csv
                600, //set radius, 900 is a typical radius size
                ((width/2) * 0.8f) + 3000, //sets the location of the star's one-way jump point in hyperspace, since it is the center of the star system, we want it to be in the center of the star system jump points in hyperspace
                (-height/2 * 0.9f) + 8500,
                150 //radius of corona terrain around star
        );
        star.setCustomDescriptionId("porosdesc");

        float innerOrbitDistance = StarSystemGenerator.addOrbitingEntities(
                systemPOR, //star system variable, used to add entities
                star, //focus object for entities to orbit
                StarAge.AVERAGE, //used by generator to decide which kind of planets to add
                0, //minimum number of entities
                0, //maximum number of entities
                4000, //the radius between the first generated entity and the focus object, in this case the star
                1, //used to assign roman numerals to the generated entities if not given special names
                true //generator will give unique names like "Ordog" instead of "Example Star System III"
        );

        PlanetAPI planetOne = systemPOR.addPlanet( //assigns instance of newly created planet to variable planetOne
                "Onpo", //unique id string
                star, //orbit focus for planet
                "Onpo", //display name of planet
                "ice_giant", //planet type id, comes from starsector-core/data/campaign/procgen/planet_gen_data.csv
                35f, //starting angle in orbit
                450f, //planet size
                3300, //1500 radius gap from the outer randomly generated entity created above
                531 //number of in-game days for it to orbit once
        );
        planetOne.setCustomDescriptionId("onpodesc");
        planetOne.setInteractionImage("illustrations", "onpo");

        MarketAPI onpomarket = auwennja.triceti.world.systems.triceti_AddMarket.addMarketplace(
                ModPlugin.cetudan,
                planetOne,
                null,
                "Onpo",
                4,
                new ArrayList<>(Arrays.asList( //List of conditions for this method to iterate through and add to the market
                        Conditions.POPULATION_4,
                        Conditions.EXTREME_WEATHER,
                        Conditions.VOLATILES_PLENTIFUL,
                        Conditions.HABITABLE,
                        "triceti_ai_logistics_network"

                )),
                new ArrayList<>(Arrays.asList( //list of submarkets for this method to iterate through and add to the market. if a military base industry was added to this market, it would be consistent to add a military submarket too
                        Submarkets.SUBMARKET_OPEN, //add a default open market
                        Submarkets.SUBMARKET_STORAGE, //add a player storage market
                        Submarkets.SUBMARKET_BLACK //add a black market
                )),
                new ArrayList<>(Arrays.asList( //list of industries for this method to iterate through and add to the market
                        Industries.POPULATION, //population industry is required for weirdness to not happen
                        Industries.SPACEPORT,
                        Industries.BATTLESTATION_HIGH,
                        Industries.WAYSTATION,
                        Industries.GROUNDDEFENSES,
                        Industries.MINING,
                        Industries.FUELPROD

                )),
                true,
                false
        );

        PlanetAPI planetTwo = systemPOR.addPlanet( //assigns instance of newly created planet to variable planetOne
                "Ninka", //unique id string
                planetOne, //orbit focus for planet
                "Ninka", //display name of planet
                "barren", //planet type id, comes from starsector-core/data/campaign/procgen/planet_gen_data.csv
                190f, //starting angle in orbit
                130f, //planet size
                980, //1500 radius gap from the outer randomly generated entity created above
                348 //number of in-game days for it to orbit once
        );
        planetTwo.getMarket().addCondition(Conditions.RARE_ORE_RICH);

        PlanetAPI planetThree = systemPOR.addPlanet( //assigns instance of newly created planet to variable planetOne
                "kama", //unique id string
                star, //orbit focus for planet
                "Kama", //display name of planet
                "tundra", //planet type id, comes from starsector-core/data/campaign/procgen/planet_gen_data.csv
                190f, //starting angle in orbit
                310f, //planet size
                5300, //1500 radius gap from the outer randomly generated entity created above
                485 //number of in-game days for it to orbit once
        );
        planetThree.setCustomDescriptionId("kamadesc");
        planetThree.setInteractionImage("illustrations", "kama");

        MarketAPI kamamarket = auwennja.triceti.world.systems.triceti_AddMarket.addMarketplace(
                ModPlugin.cetudan,
                planetThree,
                null,
                "Kama",
                6,
                new ArrayList<>(Arrays.asList( //List of conditions for this method to iterate through and add to the market
                        Conditions.POPULATION_6,
                        Conditions.HABITABLE,
                        Conditions.COLD,
                        Conditions.RARE_ORE_RICH,
                        Conditions.ORE_ABUNDANT,
                        Conditions.ORGANICS_TRACE,
                        Conditions.FARMLAND_POOR,
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
                        Industries.STARFORTRESS_HIGH,
                        Industries.PATROLHQ,
                        Industries.HEAVYBATTERIES,
                        Industries.WAYSTATION,
                        Industries.MINING,
                        Industries.REFINING,
                        Industries.HEAVYINDUSTRY,
                        Industries.FARMING

                )),
                true,
                false
        );

        systemPOR.addAsteroidBelt(
                star, //orbit focus
                230, //number of asteroid entities
                6400, //orbit radius is 500 gap for outer randomly generated entity above
                255, //width of band
                140, //minimum and maximum visual orbit speeds of asteroids
                200,
                Terrain.ASTEROID_BELT, //ID of the terrain type that appears in the section above the abilities bar
                "Sunti Belt" //display name
        );

        //add a ring texture. it will go under the asteroid entities generated above
        systemPOR.addRingBand(star,
                "misc", //used to access band texture, this is the name of a category in settings.json
                "rings_asteroids0", //specific texture id in category misc in settings.json
                256f, //texture width, can be used for scaling shenanigans
                2,
                Color.white, //colour tint
                256f, //band width in game
                6400, //same as above
                200f,
                null,
                null
        );

        PlanetAPI planetFive = systemPOR.addPlanet( //assigns instance of newly created planet to variable planetOne
                "Yoru", //unique id string
                star, //orbit focus for planet
                "Yoru", //display name of planet
                "rocky_ice", //planet type id, comes from starsector-core/data/campaign/procgen/planet_gen_data.csv
                190f, //starting angle in orbit
                450f, //planet size
                8900, //1500 radius gap from the outer randomly generated entity created above
                915 //number of in-game days for it to orbit once
        );
        planetFive.getMarket().addCondition(Conditions.VOLATILES_TRACE);

        //add makeshift comm relay entity to system
        SectorEntityToken relay = systemPOR.addCustomEntity(
                "makeshiftrelay_POR",
                "Makeshift Relay",
                Entities.COMM_RELAY_MAKESHIFT,
                ModPlugin.cetudan
        );
        //assign an orbit
        relay.setCircularOrbit(planetFive, 270f, 800f, 61f); //assign an orbit

        //add domain sensor array
        SectorEntityToken sensorArray = systemPOR.addCustomEntity(
                "makeshiftsensor_POR",
                "Sensor Array",
                Entities.SENSOR_ARRAY_MAKESHIFT,
                ModPlugin.cetudan
        );
        //assign an orbit, point down ensures it rotates to point towards center while orbiting
        sensorArray.setCircularOrbitPointingDown(star, 90f, 1000, 150f);


        //autogenerate jump points that will appear in hyperspace and in system
        systemPOR.autogenerateHyperspaceJumpPoints(true, true);

        //the following is hyperspace cleanup code that will remove hyperstorm clouds around this system's location in hyperspace
        //don't need to worry about this, it's more or less copied from vanilla

        //set up hyperspace editor plugin
        HyperspaceTerrainPlugin hyperspaceTerrainPlugin = (HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin(); //get instance of hyperspace terrain
        NebulaEditor nebulaEditor = new NebulaEditor(hyperspaceTerrainPlugin); //object used to make changes to hyperspace nebula

        //set up radiuses in hyperspace of system
        float minHyperspaceRadius = hyperspaceTerrainPlugin.getTileSize() * 2f; //minimum radius is two 'tiles'
        float maxHyperspaceRadius = systemPOR.getMaxRadiusInHyperspace();

        //hyperstorm-b-gone (around system in hyperspace)
        nebulaEditor.clearArc(systemPOR.getLocation().x, systemPOR.getLocation().y, 0, minHyperspaceRadius + maxHyperspaceRadius, 0f, 360f, 0.25f);

        if (!kamamarket.hasCondition(ModPlugin.AI_LOGISTICS_CONDITION)) {
            kamamarket.addCondition(ModPlugin.AI_LOGISTICS_CONDITION);
        }
    }
}
