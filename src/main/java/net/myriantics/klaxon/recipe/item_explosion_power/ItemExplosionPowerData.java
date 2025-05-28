package net.myriantics.klaxon.recipe.item_explosion_power;

import net.myriantics.klaxon.util.KlaxonMathHelper;

public record ItemExplosionPowerData(double explosionPower, boolean producesFire) {
    public ItemExplosionPowerData(double explosionPower, boolean producesFire) {
        this.explosionPower = KlaxonMathHelper.roundToTenth(explosionPower);
        this.producesFire = producesFire;
    }
}
