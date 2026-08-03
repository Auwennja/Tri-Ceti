package auwennja.triceti;

import auwennja.triceti.world.tricetigen;
import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import exerelin.campaign.SectorManager;

import static exerelin.world.VanillaSystemsGenerator.generate;

public class ModPlugin extends BaseModPlugin {
    public static boolean nexerelinEnabled = false;
    public static final String cetora="cetora";
    public static final String cetevska="cetevska";
    public static final String cetudan="cetudan";
    @Override
    public void onApplicationLoad() throws Exception {
        super.onApplicationLoad();

        nexerelinEnabled = Global.getSettings().getModManager().isModEnabled("nexerelin");
    }

    public void onNewGame() {

        super.onNewGame();
        if (!nexerelinEnabled || SectorManager.getManager().isCorvusMode()) {
            new tricetigen().generate(Global.getSector());
        }
        else {
            new tricetigen().generate(Global.getSector());
        }

    }

    public void onGameLoad(boolean newGame) {
        Global.getSector().getPlayerFaction().getPortraits(FullName.Gender.MALE).remove("graphics/triceti/portraits/belenos.png");
        Global.getSector().getPlayerFaction().getPortraits(FullName.Gender.MALE).remove("graphics/triceti/portraits/kremy.png");
        Global.getSector().getPlayerFaction().getPortraits(FullName.Gender.FEMALE).remove("graphics/triceti/portraits/auwennja.png");
        Global.getSector().getPlayerFaction().getPortraits(FullName.Gender.FEMALE).remove("graphics/triceti/portraits/pisenica.png");
        Global.getSector().getPlayerFaction().getPortraits(FullName.Gender.FEMALE).remove("graphics/triceti/portraits/tavorin.png");

    }

    public static void initFactionRelationships(SectorAPI sector) {
        FactionAPI cetora = sector.getFaction(ModPlugin.cetora);
        FactionAPI cetevska = sector.getFaction(ModPlugin.cetevska);
        FactionAPI cetudan = sector.getFaction(ModPlugin.cetudan);
    }
}
