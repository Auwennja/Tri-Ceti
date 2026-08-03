package auwennja.triceti.campaign.backgrounds;

import auwennja.triceti.ModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionSpecAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import exerelin.campaign.backgrounds.BaseCharacterBackground;
import exerelin.utilities.NexFactionConfig;

public class triceti_RA extends BaseCharacterBackground {

    /**
     * Must match the hidden skill ID in skill_data.csv.
     */
    public static final String SKILL_ID = "triceti_RA_skill";

    public static final float RELATIONSHIP_BONUS = 0.35f;

    private static final String REWARD_MEMORY_KEY =
            "$triceti_ra_background_rewards_given";

    @Override
    public void onNewGameAfterTimePass(
            FactionSpecAPI factionSpec,
            NexFactionConfig factionConfig
    ) {
        if (Global.getSector()
                .getMemoryWithoutUpdate()
                .getBoolean(REWARD_MEMORY_KEY)) {
            return;
        }

        Global.getSector()
                .getPlayerPerson()
                .getStats()
                .setSkillLevel(SKILL_ID, 1f);

        Global.getSector()
                .getPlayerFleet()
                .getCargo()
                .addSpecial(
                        new SpecialItemData(
                                "triceti_cetudan_package",
                                null
                        ),
                        1f
                );

        Global.getSector()
                .getPlayerFaction()
                .adjustRelationship(
                        ModPlugin.cetudan,
                        RELATIONSHIP_BONUS
                );

        Global.getSector()
                .getMemoryWithoutUpdate()
                .set(REWARD_MEMORY_KEY, true);
    }
}
