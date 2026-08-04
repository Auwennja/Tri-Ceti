package auwennja.triceti;

import auwennja.triceti.world.tricetigen;
import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.FullName;
import exerelin.campaign.SectorManager;

public class ModPlugin extends BaseModPlugin {

    public static boolean nexerelinEnabled = false;

    public static final String cetora = "cetora";
    public static final String cetevska = "cetevska";
    public static final String cetudan = "cetudan";

    public static final String AI_LOGISTICS_CONDITION =
            "triceti_ai_logistics_network";

    private static final String AI_LOGISTICS_RETROGEN_KEY =
            "$triceti_ai_logistics_network_retrogen_v1";

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

        applyAILogisticsNetworkRetrogen();

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


    private void applyAILogisticsNetworkRetrogen() {
        SectorAPI sector = Global.getSector();

        if (sector == null || sector.getEconomy() == null) {
            return;
        }

        if (sector.getMemoryWithoutUpdate()
                .getBoolean(AI_LOGISTICS_RETROGEN_KEY)) {
            return;
        }

        boolean redaFound =
                addAILogisticsConditionByEntityId(sector, REDA_ENTITY_ID);

        boolean kamaFound =
                addAILogisticsConditionByEntityId(sector, KAMA_ENTITY_ID);

        boolean utrobaFound =
                addAILogisticsConditionByEntityId(sector, UTROBA_ENTITY_ID);

        if (redaFound && kamaFound && utrobaFound) {
            sector.getMemoryWithoutUpdate().set(
                    AI_LOGISTICS_RETROGEN_KEY,
                    true
            );

            Global.getLogger(getClass()).info(
                    "AI-Built Logistics Network retrogen completed."
            );
        } else {
            Global.getLogger(getClass()).warn(
                    "AI-Built Logistics Network retrogen incomplete; "
                            + "will retry on the next load."
            );
        }
    }


    private boolean addAILogisticsConditionByEntityId(
            SectorAPI sector,
            String entityId
    ) {
        MarketAPI target = null;

        for (MarketAPI market :
                sector.getEconomy().getMarketsCopy()) {

            if (market == null || market.getPrimaryEntity() == null) {
                continue;
            }

            if (entityId.equals(market.getPrimaryEntity().getId())) {
                target = market;
                break;
            }
        }

        if (target == null) {
            Global.getLogger(getClass()).warn(
                    "Could not find market with primary entity ID ["
                            + entityId
                            + "] for AI logistics retrogen."
            );

            return false;
        }

        if (!target.hasCondition(AI_LOGISTICS_CONDITION)) {
            target.addCondition(AI_LOGISTICS_CONDITION);

            Global.getLogger(getClass()).info(
                    "Added AI-Built Logistics Network to market ["
                            + target.getId()
                            + "] on entity ["
                            + entityId
                            + "]."
            );
        }

        return true;
    }
}