package net.myriantics.klaxon.compat.emi.recipes;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.compat.emi.registry.KlaxonEmiCategories;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipe;
import net.myriantics.klaxon.util.KlaxonMathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public abstract class AbstractBlastProcessingEmiRecipe<T extends BlastProcessingRecipe> implements EmiRecipe {
    private static final ResourceLocation BACKGROUND_TEXTURE = KlaxonCommon.locate("textures/gui/sprites/emi/deepslate_blast_processor_emi.png");

    private static final Random RANDOM = new Random();

    protected final T recipe;
    protected final ResourceLocation id;
    protected final int unique;

    protected final double explosionPowerMin;
    protected final double explosionPowerMax;

    public AbstractBlastProcessingEmiRecipe(T recipe, ResourceLocation id) {
        this.recipe = recipe;
        this.id = id;
        this.explosionPowerMin = KlaxonMathHelper.roundToDecimalPlace(this.recipe.getExplosionPowerMin(), 2);
        this.explosionPowerMax = KlaxonMathHelper.roundToDecimalPlace(this.recipe.getExplosionPowerMax(), 2);
        this.unique = RANDOM.nextInt();
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return KlaxonEmiCategories.BLAST_PROCESSING;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return this.id;
    }

    @Override
    public int getDisplayWidth() {
        return 147;
    }

    @Override
    public int getDisplayHeight() {
        return 60;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(BACKGROUND_TEXTURE, 0, 0, 147, 60, 0, 0);

        widgets.add(
                this.addInputSlot(widgets, 18, 3)
        );

        widgets.addText(Component.literal(String.valueOf(this.explosionPowerMin)), 48, 44, 16777215, false);
        widgets.addText(Component.literal(String.valueOf(this.explosionPowerMax)), 48, 8, 16777215, false);

        // add the 3x3 grid of output slots
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                int index = x + y * 3;

                widgets.add(this.addResultSlot(
                        widgets,
                        index,
                        90 + x * 18,
                        3 + y * 18
                ).recipeContext(this).drawBack(false));
            }
        }
    }

    protected abstract SlotWidget addInputSlot(WidgetHolder widgets, int x, int y);

    protected abstract SlotWidget addResultSlot(WidgetHolder widgets, int index, int x, int y);
}
