package net.myriantics.klaxon.screen.container.blast_processor;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.myriantics.klaxon.block.machines.blast_processor.AbstractBlastProcessorMenu;
import net.myriantics.klaxon.screen.BaseKlaxonContainerScreen;
import net.myriantics.klaxon.util.KlaxonMathHelper;

public abstract class AbstractBlastProcessorScreen<T extends AbstractBlastProcessorMenu> extends BaseKlaxonContainerScreen<T> {

    protected String explosionPower;
    protected String explosionPowerMin;
    protected String explosionPowerMax;
    protected String truncatedExplosionPower;
    protected String truncatedExplosionPowerMin;
    protected String truncatedExplosionPowerMax;

    private float numberLinePosition = 0.0f;
    private State state;

    public AbstractBlastProcessorScreen(T menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        this.updateState();
        if (this.state == State.VALID_RECIPE) {
            this.updateNumberLinePosition();
        }
        if (this.state.hasRecipe()) {
            this.explosionPower = String.valueOf(this.menu.getExplosionPower());
            this.explosionPowerMin = String.valueOf(this.menu.getExplosionPowerMin());
            this.explosionPowerMax = String.valueOf(this.menu.getExplosionPowerMax());
            this.truncatedExplosionPower = String.valueOf(KlaxonMathHelper.roundToDecimalPlace(this.menu.getExplosionPower(), 2));
            this.truncatedExplosionPowerMin = String.valueOf(KlaxonMathHelper.roundToDecimalPlace(this.menu.getExplosionPowerMin(), 2));
            this.truncatedExplosionPowerMax = String.valueOf(KlaxonMathHelper.roundToDecimalPlace(this.menu.getExplosionPowerMax(), 2));
        } else {
            this.explosionPower = "---";
            this.explosionPowerMin = "---";
            this.explosionPowerMax = "---";
            this.truncatedExplosionPower = "---";
            this.truncatedExplosionPowerMin = "---";
            this.truncatedExplosionPowerMax = "---";
        }
    }

    protected float getNumberLinePosition() {
        return this.numberLinePosition;
    }

    protected void updateNumberLinePosition() {
        double range = this.menu.getExplosionPowerMax() - this.menu.getExplosionPowerMin();
        double adjusted = this.menu.getExplosionPower() - this.menu.getExplosionPowerMin();
        this.numberLinePosition = (float) (adjusted / range);
    }

    protected State getState() {
        return this.state;
    }

    protected void updateState() {
        boolean hasCatalyst = this.menu.hasCatalyst();
        boolean hasIngredient = this.menu.hasIngredient();
        if (hasCatalyst && hasIngredient) {
            double explosionPowerMin = this.menu.getExplosionPowerMin();
            double explosionPowerMax = this.menu.getExplosionPowerMax();
            double explosionPower = this.menu.getExplosionPower();

            if (explosionPower < explosionPowerMin) {
                this.state = State.UNDERPOWERED_CATALYST;
            } else if (explosionPower > explosionPowerMax) {
                this.state = State.OVERPOWERED_CATALYST;
            } else {
                this.state = State.VALID_RECIPE;
            }
        } else if (hasCatalyst) {
            this.state = State.MISSING_INGREDIENT;
        } else if (hasIngredient) {
            this.state = State.MISSING_CATALYST;
        } else {
            this.state = State.EMPTY;
        }
    }

    protected enum State {
        EMPTY,
        MISSING_CATALYST,
        MISSING_INGREDIENT,
        UNDERPOWERED_CATALYST,
        VALID_RECIPE,
        OVERPOWERED_CATALYST;

        public boolean hasRecipe() {
            return this == UNDERPOWERED_CATALYST || this == VALID_RECIPE || this == OVERPOWERED_CATALYST;
        }
    }
}
