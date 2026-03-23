package net.myriantics.klaxon.compat.emi.recipes;

import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.compat.emi.registry.KlaxonEmiCategories;
import net.myriantics.klaxon.registry.misc.KlaxonRecipeTypes;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipe;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystDefinitionRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BlastProcessingEmiRecipe implements EmiRecipe {
    private static final ResourceLocation BACKGROUND_TEXTURE = KlaxonCommon.locate("textures/gui/emi/deepslate_blast_processor_emi.png");

    private final ResourceLocation id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> outputStacks;
    private final EmiRegistry registry;

    private final NonNullList<ExplosiveCatalystDefinitionRecipe> catalystData;
    private final EmiIngredient catalysts;

    private final double explosionPowerMin;
    private final double explosionPowerMax;

    public BlastProcessingEmiRecipe(RecipeHolder<BlastProcessingRecipe> recipe, EmiRegistry registry, ResourceLocation id) {
        this.id = id;
        this.outputStacks = new ArrayList<>();
        for (ItemStack stack : recipe.value().getRecipeOutputCompound().getDisplayStacks()) {
            outputStacks.add(EmiStack.of(stack));
        }
        this.explosionPowerMin = recipe.value().getExplosionPowerMin();
        this.explosionPowerMax = recipe.value().getExplosionPowerMax();
        this.registry = registry;
        this.catalystData = getValidCatalysts();
        NonNullList<EmiIngredient> catalystStacks = NonNullList.createWithCapacity(catalystData.size());

        for (ExplosiveCatalystDefinitionRecipe catalystRecipe : catalystData) {
            catalystStacks.add(EmiIngredient.of(catalystRecipe.getIngredient()));
        }

        this.catalysts = EmiIngredient.of(catalystStacks);
        this.input = List.of(EmiIngredient.of(recipe.value().getIngredientItem()), catalysts);
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return KlaxonEmiCategories.BLAST_PROCESSING;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return input;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return outputStacks;
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
        widgets.addText(Component.literal("" + explosionPowerMin), 48, 44, 16777215, false);
        widgets.addText(Component.literal("" + explosionPowerMax), 48, 8, 16777215, false);
        widgets.addText(Component.literal("---" ), 48, 26, 16777215, false);

        // add the 3x3 grid of output slots
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                int index = x + y * 3;

                widgets.addSlot(
                        index < outputStacks.size() ? outputStacks.get(index) : EmiStack.EMPTY,
                        90 + x * 18,
                        3 + y * 18
                ).recipeContext(this).drawBack(false);
            }
        }
    }

    private NonNullList<ExplosiveCatalystDefinitionRecipe> getValidCatalysts() {
        NonNullList<ExplosiveCatalystDefinitionRecipe> catalysts = NonNullList.create();
        for (RecipeHolder<ExplosiveCatalystDefinitionRecipe> recipe : registry.getRecipeManager().getAllRecipesFor(KlaxonRecipeTypes.EXPLOSIVE_CATALYST_DEFINITION)) {
            if (recipe.value().getData().matchesConditions(explosionPowerMin, explosionPowerMax)) {

                // dont show hidden recipes in the scroller
                if (!recipe.value().isHidden()) {
                    catalysts.add(recipe.value());
                }
            }
        }
        return catalysts;
    }
}
