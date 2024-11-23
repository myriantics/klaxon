package net.myriantics.klaxon.compat.emi.recipes;

import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.compat.emi.KlaxonEmiRecipeCategories;
import net.myriantics.klaxon.recipes.KlaxonRecipeTypes;
import net.myriantics.klaxon.recipes.blast_processing.BlastProcessingRecipe;
import net.myriantics.klaxon.recipes.item_explosion_power.ItemExplosionPowerRecipe;
import net.myriantics.klaxon.util.KlaxonTags;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlastProcessingEmiRecipe implements EmiRecipe {
    private static final Identifier BACKGROUND_TEXTURE = KlaxonCommon.locate("textures/gui/emi/deepslate_blast_processor_emi.png");

    private final Identifier id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;
    private final EmiRegistry registry;

    private final DefaultedList<ItemExplosionPowerRecipe> catalystData;
    private final EmiIngredient catalysts;

    private final double explosionPowerMin;
    private final double explosionPowerMax;

    public BlastProcessingEmiRecipe(BlastProcessingRecipe recipe, EmiRegistry registry) {
        this.id = recipe.getId();
        this.output = List.of(EmiStack.of(recipe.getOutput(null)));
        this.explosionPowerMin = recipe.getExplosionPowerMin();
        this.explosionPowerMax = recipe.getExplosionPowerMax();
        this.registry = registry;
        this.catalystData = getValidCatalysts();
        DefaultedList<EmiIngredient> catalystStacks = DefaultedList.ofSize(catalystData.size());

        for (ItemExplosionPowerRecipe catalystRecipe : catalystData) {
            catalystStacks.add(EmiIngredient.of(catalystRecipe.getItem()));
        }

        this.catalysts = EmiIngredient.of(catalystStacks);
        this.input = List.of(EmiIngredient.of(recipe.getProcessingItem()), catalysts);
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return KlaxonEmiRecipeCategories.BLAST_PROCESSING;
    }

    @Override
    public @Nullable Identifier getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return input;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return output;
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

        widgets.addSlot(input.get(0), 18, 3).drawBack(false);

        widgets.addSlot(catalysts, 18, 39).drawBack(false);
        widgets.addText(Text.literal("" + explosionPowerMin), 48, 44, 16777215, false);
        widgets.addText(Text.literal("" + explosionPowerMax), 48, 8, 16777215, false);
        widgets.addText(Text.literal("---" ), 48, 26, 16777215, false);

        widgets.addSlot(output.get(0), 90, 3).recipeContext(this).drawBack(false);
    }

    private DefaultedList<ItemExplosionPowerRecipe> getValidCatalysts() {
        DefaultedList<ItemExplosionPowerRecipe> catalysts = DefaultedList.of();
        for (ItemExplosionPowerRecipe recipe : registry.getRecipeManager().listAllOfType(KlaxonRecipeTypes.ITEM_EXPLOSION_POWER)) {
            if (recipe.matchesConditions(explosionPowerMin, explosionPowerMax)) {
                boolean fail = false;

                // dont show creative mode items in the scroller
                for (ItemStack stack : recipe.getItem().getMatchingStacks()) {
                    if (stack.isIn(KlaxonTags.Items.ITEM_EXPLOSION_POWER_EMI_OMITTED)) {
                        fail = true;
                    }
                }

                if (!fail) {
                    catalysts.add(recipe);
                }
            }
        }
        return catalysts;
    }
}
