package auwennja.triceti;

import auwennja.triceti.world.tricetigen;
import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.terrain.StarCoronaTerrainPlugin;
import com.fs.starfarer.api.util.Misc;

public class ModPlugin extends BaseModPlugin {

    public static boolean nexerelinEnabled = false;

    public static final String cetora = "cetora";
    public static final String cetevska = "cetevska";
    public static final String cetudan = "cetudan";

    public static final String AI_LOGISTICS_CONDITION =
            "triceti_ai_logistics_network";


    private static final String OSSA_SYSTEM_RETROGEN_KEY =
            "$triceti_ossa_system_retrogen_v1";

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


        if (newGame) {
            Global.getSector()
                    .getMemoryWithoutUpdate()
                    .set(OSSA_SYSTEM_RETROGEN_KEY, true);
        } else {
            applyOssaSystemRetrogen();
        }
    }


    public static void initFactionRelationships(SectorAPI sector) {
        FactionAPI cetoraFaction =
                sector.getFaction(ModPlugin.cetora);

        FactionAPI cetevskaFaction =
                sector.getFaction(ModPlugin.cetevska);

        FactionAPI cetudanFaction =
                sector.getFaction(ModPlugin.cetudan);

    }

    private void applyOssaSystemRetrogen() {

        SectorAPI sector = Global.getSector();

        if (sector == null) {
            return;
        }

        if (sector.getMemoryWithoutUpdate()
                .getBoolean(OSSA_SYSTEM_RETROGEN_KEY)) {
            return;
        }

        StarSystemAPI ossa =
                sector.getStarSystem("Ossa");

        if (ossa == null) {
            Global.getLogger(getClass()).warn(
                    "Ossa retrogen: Ossa system could not be found."
            );
            return;
        }

        SectorEntityToken zemlaEntity =
                sector.getEntityById("Zemla");

        if (!(zemlaEntity instanceof PlanetAPI)) {
            Global.getLogger(getClass()).warn(
                    "Ossa retrogen: Zemla could not be found."
            );
            return;
        }

        PlanetAPI zemla =
                (PlanetAPI) zemlaEntity;



        PlanetAPI gravityWell = ossa.getStar();

        if (gravityWell == null) {

            gravityWell = ossa.initStar(
                    "ossa_gravity_well",
                    "zemla_gravity_well",
                    0f,
                    0f
            );

            gravityWell.setCustomDescriptionId(
                    "zemladesc"
            );

            gravityWell.addTag(
                    Tags.AMBIENT_LS
            );

            gravityWell.setSkipForJumpPointAutoGen(
                    true
            );


            StarCoronaTerrainPlugin corona =
                    Misc.getCoronaFor(gravityWell);

            if (corona != null) {
                ossa.removeEntity(
                        corona.getEntity()
                );
            }
        }

        SectorEntityToken center =
                ossa.getCenter();

        if (center != null) {
            zemla.setCircularOrbit(
                    center,
                    0f,
                    0f,
                    1000f
            );
        }


        if (gravityWell.getContainingLocation() == ossa) {
            ossa.removeEntity(gravityWell);
        }

        ossa.setType(
                StarSystemGenerator.StarSystemType.NEBULA
        );

        sector.getMemoryWithoutUpdate().set(
                OSSA_SYSTEM_RETROGEN_KEY,
                true
        );

        Global.getLogger(getClass()).info(
                "Successfully applied Ossa system retrogen."
        );
    }
}