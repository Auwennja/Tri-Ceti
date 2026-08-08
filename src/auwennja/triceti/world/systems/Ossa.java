package auwennja.triceti.world.systems;

import auwennja.triceti.ModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Ossa implements SectorGeneratorPlugin {
    float width = Global.getSettings().getFloat("sectorWidth");
    float height = Global.getSettings().getFloat("sectorHeight");
    public static Color OSS_AMBIENT_LIGHT_COLOR = new Color(60,65,80,150);

    @Override
    public void generate(SectorAPI sector) {
        StarSystemAPI systemOSS = sector.createStarSystem("Ossa");
        LocationAPI hyper = Global.getSector().getHyperspace();

        systemOSS.setBackgroundTextureFilename("graphics/triceti/backgrounds/background_ossa.jpg");
        Random random = StarSystemGenerator.random;
        systemOSS.getLocation().set(((width/2) * 0.8f) - 8000, -height/2 - 1000);

        SectorEntityToken center = systemOSS.initNonStarCenter();
        systemOSS.setLightColor(OSS_AMBIENT_LIGHT_COLOR);
        center.addTag(Tags.AMBIENT_LS);

        PlanetAPI planetOne = systemOSS.addPlanet(
                "Zemla",
                null,
                "Zemla",
                "rocky_ice",
                0,
                350f,
                0,
                0
        );

        planetOne.getMemoryWithoutUpdate().set("$gateHaulerIceGiant", true);
        planetOne.getMarket().addCondition(Conditions.COLD);
        planetOne.getMarket().addCondition(Conditions.DARK);
        planetOne.getMarket().addCondition(Conditions.THIN_ATMOSPHERE);
        planetOne.getMarket().addCondition(Conditions.ORE_MODERATE);
        planetOne.getMarket().addCondition(Conditions.RARE_ORE_RICH);

        planetOne.setOrbit(null);
        planetOne.setLocation(0, 0);
        planetOne.setCustomDescriptionId("zemladesc");

        SectorEntityToken stationOne = systemOSS.addCustomEntity("utroba", "Utroba", "station_lowtech2", ModPlugin.cetevska);
        stationOne.setCircularOrbit(planetOne, 90f, 600, 65);
        stationOne.setCustomDescriptionId("utrobadesc");
        stationOne.setInteractionImage("illustrations", "utroba");

        MarketAPI utrobamarket = auwennja.triceti.world.systems.triceti_AddMarket.addMarketplace(
                ModPlugin.cetevska,
                stationOne,
                null,
                "Utroba",
                4, //population size
                new ArrayList<>(Arrays.asList( //List of conditions for this method to iterate through and add to the market
                        Conditions.POPULATION_4,
                        Conditions.HABITABLE,
                        Conditions.VOLATILES_ABUNDANT,
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
                        Industries.STARFORTRESS,
                        Industries.HEAVYBATTERIES,
                        Industries.WAYSTATION,
                        Industries.MINING,
                        Industries.HEAVYINDUSTRY,
                        "triceti_ichorene_plant"
                )),
                true,
                false
        );
        utrobamarket.addTag(Tags.STATION);

        PlanetAPI planetTwo = systemOSS.addPlanet( //assigns instance of newly created planet to variable planetOne
                "Mesik", //unique id string
                planetOne, //orbit focus for planet
                "Mesik", //display name of planet
                "barren", //planet type id, comes from starsector-core/data/campaign/procgen/planet_gen_data.csv
                10f, //starting angle in orbit
                100f, //planet size
                1350, //1500 radius gap from the outer randomly generated entity created above
                365 //number of in-game days for it to orbit once
        );
        planetTwo.setCustomDescriptionId("mesikdesc");
        MarketAPI mesikmarket = auwennja.triceti.world.systems.triceti_AddMarket.addMarketplace(
                ModPlugin.cetevska,
                planetTwo,
                null,
                "Mesik",
                4, //population size
                new ArrayList<>(Arrays.asList( //List of conditions for this method to iterate through and add to the market
                        Conditions.POPULATION_4,
                        Conditions.DARK,
                        Conditions.THIN_ATMOSPHERE,
                        Conditions.ORE_SPARSE,
                        Conditions.RARE_ORE_SPARSE,
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
                        Industries.GROUNDDEFENSES,
                        Industries.MINING,
                        Industries.REFINING
                )),
                true,
                false
        );


        SectorEntityToken oldstation_OSS = systemOSS.addCustomEntity("oldstation_OSS", "Abandoned Station", "station_side00", Factions.NEUTRAL);
        oldstation_OSS.setCircularOrbit(planetTwo, 95f, 190f, 162f);
        oldstation_OSS.setCustomDescriptionId("oldstationdesc");
        oldstation_OSS.setInteractionImage("illustrations", "abandoned_station3");
        Misc.setAbandonedStationMarket("oldstation_market", oldstation_OSS);

        //add makeshift comm relay entity to system
        SectorEntityToken relay = systemOSS.addCustomEntity(
                "makeshiftrelay_OSS",
                "Makeshift Relay",
                Entities.COMM_RELAY_MAKESHIFT,
                ModPlugin.cetevska
        );
        //assign an orbit
        relay.setCircularOrbit(planetOne, 270f, 2550f, 400f); //assign an orbit

        StarSystemGenerator.addStableLocations(
                systemOSS,
                1
        );

        systemOSS.autogenerateHyperspaceJumpPoints(true, true);

        if (!utrobamarket.hasCondition(ModPlugin.AI_LOGISTICS_CONDITION)) {
            utrobamarket.addCondition(ModPlugin.AI_LOGISTICS_CONDITION);
        }
    }
}
