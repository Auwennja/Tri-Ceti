package auwennja.triceti.campaign.econ;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.util.Pair;

public class triceti_ichorene_plant extends BaseIndustry {

    @Override
    public void apply() {
        super.apply(true);

        int size = market.getSize();
        int production = Math.max(0, size + 1);
        int demandAmount = Math.max(0, size - 3);

        demand(Commodities.VOLATILES, demandAmount);

        supply(Commodities.ORGANICS, production);
        supply(Commodities.METALS, production);
        supply(Commodities.RARE_METALS, production);
        supply(Commodities.FUEL, production);

        Pair<String, Integer> deficit =
                getMaxDeficit(Commodities.VOLATILES);

        applyDeficitToProduction(
                0,
                deficit,
                Commodities.ORGANICS,
                Commodities.ORE,
                Commodities.RARE_METALS
        );

        if (!isFunctional()) {
            supply.clear();
        }
    }

    @Override
    public void unapply() {
        super.unapply();
    }
    protected boolean hasPostDemandSection(boolean hasDemand, IndustryTooltipMode mode) {
        Pair<String, Integer> deficit = getMaxDeficit(Commodities.ORGANS);
        if (deficit.two <= 0) return false;
        //return mode == IndustryTooltipMode.NORMAL && isFunctional();
        return mode != IndustryTooltipMode.NORMAL || isFunctional();
    }
    @Override
    public boolean isAvailableToBuild() {
        if (!Global.getSector().getPlayerFaction().knowsIndustry(getId())) {
            return false;
        }
        return market.getPlanetEntity() != null;
    }
    public boolean showWhenUnavailable() {
        return Global.getSector().getPlayerFaction().knowsIndustry(getId());
    }
}
