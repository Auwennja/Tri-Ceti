package auwennja.triceti.campaign.econ;

import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class triceti_ai_built_logistics_network
        extends BaseMarketConditionPlugin {

    /**
     * A flat +50 percentage-point accessibility bonus.
     */
    public static final float ACCESSIBILITY_BONUS = 0.5f;

    private static final String MODIFIER_DESCRIPTION =
            "AI-built logistics network";

    @Override
    public void apply(String id) {
        market.getAccessibilityMod().modifyFlat(
                id,
                ACCESSIBILITY_BONUS,
                MODIFIER_DESCRIPTION
        );
    }

    @Override
    public void unapply(String id) {
        market.getAccessibilityMod().unmodifyFlat(id);
    }

    @Override
    protected void createTooltipAfterDescription(
            TooltipMakerAPI tooltip,
            boolean expanded
    ) {
        super.createTooltipAfterDescription(tooltip, expanded);

        tooltip.addPara(
                "%s accessibility.",
                10f,
                Misc.getHighlightColor(),
                "+" + Math.round(ACCESSIBILITY_BONUS * 100f) + "%"
        );
    }
}