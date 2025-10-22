package net.myriantics.klaxon.recipe.explosive_catalyst_definition;

import net.myriantics.klaxon.util.KlaxonMathHelper;

public record ExplosiveCatalystData(double explosionPower, boolean producesFire) {
    public ExplosiveCatalystData(double explosionPower, boolean producesFire) {
        this.explosionPower = KlaxonMathHelper.roundToTenth(explosionPower);
        this.producesFire = producesFire;
    }
}
