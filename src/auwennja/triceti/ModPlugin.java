package auwennja.triceti;

import auwennja.triceti.world.tricetigen;
import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.characters.FullName;

public class ModPlugin extends BaseModPlugin {

    public static boolean nexerelinEnabled = false;

    public static final String cetora = "cetora";
    public static final String cetevska = "cetevska";
    public static final String cetudan = "cetudan";

    public static final String AI_LOGISTICS_CONDITION =
            "triceti_ai_logistics_network";

    private static final String AI_LOGISTICS_RETROGEN_KEY =
            "$triceti_ai_logistics_network_retrogen_v1";
    private static final String ABANDONED_STATION_RETROGEN_KEY =
            "$triceti_abandoned_station_market_retrogen_v1";
    /*
     * These must match the actual internal market IDs.
     */
    private static final String REDA_ENTITY_ID = "reda";
    private static final String KAMA_ENTITY_ID = "kama";
    private static final String UTROBA_ENTITY_ID = "utroba";


    @Override
    public void onApplicationLoad() throws Exception {
        super.onApplicationLoad();

        nexerelinEnabled = Global.getSettings()
                .getModManager()
                .isModEnabled("nexerelin");
    }


    @Override
    public void onNewGame() {
        super.onNewGame();

        /*
         * Both branches previously performed exactly the same action,
         * so the conditional was unnecessary.
         */
        new tricetigen().generate(Global.getSector());
    }


    @Override
    public void onGameLoad(boolean newGame) {
        super.onGameLoad(newGame);


        Global.getSector()
                .getPlayerFaction()
                .getPortraits(FullName.Gender.MALE)
                .remove("graphics/triceti/portraits/belenos.png");

        Global.getSector()
                .getPlayerFaction()
                .getPortraits(FullName.Gender.MALE)
                .remove("graphics/triceti/portraits/kremy.png");

        Global.getSector()
                .getPlayerFaction()
                .getPortraits(FullName.Gender.FEMALE)
                .remove("graphics/triceti/portraits/auwennja.png");

        Global.getSector()
                .getPlayerFaction()
                .getPortraits(FullName.Gender.FEMALE)
                .remove("graphics/triceti/portraits/lyubov.png");

        Global.getSector()
                .getPlayerFaction()
                .getPortraits(FullName.Gender.FEMALE)
                .remove("graphics/triceti/portraits/pisenica.png");

        Global.getSector()
                .getPlayerFaction()
                .getPortraits(FullName.Gender.FEMALE)
                .remove("graphics/triceti/portraits/tavorin.png");
    }


    public static void initFactionRelationships(SectorAPI sector) {
        FactionAPI cetoraFaction =
                sector.getFaction(ModPlugin.cetora);

        FactionAPI cetevskaFaction =
                sector.getFaction(ModPlugin.cetevska);

        FactionAPI cetudanFaction =
                sector.getFaction(ModPlugin.cetudan);

        /*
         * Relationship initialization can be added here later.
         */
    }
}