package auwennja.triceti.combat.titles;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.combat.CombatEngine;
import lunalib.lunaTitle.BaseLunaTitleScreenPlugin;

import java.util.ArrayList;
import java.util.List;

public class ossatitlescreen extends BaseLunaTitleScreenPlugin {

    @Override
    public boolean pickBasedOnSystemCondition(
            String lastSystemID,
            ArrayList<String> lastSystemTags
    ) {
        return true;
    }

    @Override
    public void init(CombatEngineAPI engine) {
        CombatEngine.replaceBackground(
                "graphics/triceti/backgrounds/background_ossa.jpg",
                true
        );
        Global.getSoundPlayer().playCustomMusic(1, 1, "music_zea_lunasea_theme", true);
    }

    @Override
    public void advance(
            float amount,
            List<InputEventAPI> events
    ) {
    }

    @Override
    public void renderInUICoords(ViewportAPI viewport) {
    }

    @Override
    public void renderInWorldCoords(ViewportAPI viewport) {
    }
}