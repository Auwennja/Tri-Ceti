package auwennja.triceti.world;

import auwennja.triceti.world.systems.Ossa;
import auwennja.triceti.world.systems.Poros;
import auwennja.triceti.world.systems.Soteria;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;

public class tricetigen {
    public static void initFactionRelationships(SectorAPI sector) {
        FactionAPI hegemony = sector.getFaction(Factions.HEGEMONY);
        FactionAPI tritachyon = sector.getFaction(Factions.TRITACHYON);
        FactionAPI pirates = sector.getFaction(Factions.PIRATES);
        FactionAPI kol = sector.getFaction(Factions.KOL);
        FactionAPI church = sector.getFaction(Factions.LUDDIC_CHURCH);
        FactionAPI path = sector.getFaction(Factions.LUDDIC_PATH);
        FactionAPI league = sector.getFaction(Factions.PERSEAN);
        FactionAPI independent = sector.getFaction(Factions.INDEPENDENT);
        FactionAPI player = sector.getFaction(Factions.PLAYER);

        FactionAPI cetora = sector.getFaction("cetora");
        FactionAPI cetevska = sector.getFaction("cetevska");
        FactionAPI cetudan = sector.getFaction("cetudan");

        cetora.setRelationship(path.getId(), RepLevel.HOSTILE);
        cetora.setRelationship(hegemony.getId(), RepLevel.HOSTILE);
        cetora.setRelationship(pirates.getId(), RepLevel.HOSTILE);
        cetora.setRelationship(tritachyon.getId(), RepLevel.HOSTILE);
        cetora.setRelationship(church.getId(), RepLevel.HOSTILE);
        cetora.setRelationship(kol.getId(), RepLevel.HOSTILE);
        cetora.setRelationship(league.getId(), RepLevel.HOSTILE);
        cetora.setRelationship(independent.getId(), RepLevel.SUSPICIOUS);
        cetora.setRelationship(player.getId(), RepLevel.SUSPICIOUS);

        cetevska.setRelationship(path.getId(), RepLevel.HOSTILE);
        cetevska.setRelationship(hegemony.getId(), RepLevel.HOSTILE);
        cetevska.setRelationship(pirates.getId(), RepLevel.HOSTILE);
        cetevska.setRelationship(tritachyon.getId(), RepLevel.HOSTILE);
        cetevska.setRelationship(church.getId(), RepLevel.HOSTILE);
        cetevska.setRelationship(kol.getId(), RepLevel.HOSTILE);
        cetevska.setRelationship(league.getId(), RepLevel.HOSTILE);
        cetevska.setRelationship(independent.getId(), RepLevel.HOSTILE);
        cetevska.setRelationship(player.getId(), RepLevel.HOSTILE);

        cetudan.setRelationship(path.getId(), RepLevel.HOSTILE);
        cetudan.setRelationship(hegemony.getId(), RepLevel.HOSTILE);
        cetudan.setRelationship(pirates.getId(), RepLevel.HOSTILE);
        cetudan.setRelationship(tritachyon.getId(), RepLevel.HOSTILE);
        cetudan.setRelationship(church.getId(), RepLevel.HOSTILE);
        cetudan.setRelationship(kol.getId(), RepLevel.HOSTILE);
        cetudan.setRelationship(league.getId(), RepLevel.HOSTILE);
        cetudan.setRelationship(independent.getId(), RepLevel.HOSTILE);
        cetudan.setRelationship(player.getId(), RepLevel.HOSTILE);
    }


    public void generate(SectorAPI sector) {
        new Soteria().generate(sector);
        new Poros().generate(sector);
        new Ossa().generate(sector);

        initFactionRelationships(sector);
    }

}
