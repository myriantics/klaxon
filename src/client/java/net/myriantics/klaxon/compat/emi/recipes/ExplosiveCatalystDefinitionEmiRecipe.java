package net.myriantics.klaxon.compat.emi.recipes;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.myriantics.klaxon.compat.emi.registry.KlaxonEmiCategories;
import net.myriantics.klaxon.recipe.explosive_catalyst.ExplosiveCatalystData;
import net.myriantics.klaxon.recipe.explosive_catalyst.ExplosiveCatalystDefinitionRecipe;
import net.myriantics.klaxon.registry.misc.KlaxonColors;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ExplosiveCatalystDefinitionEmiRecipe implements EmiRecipe {

    private static final Style ORANGE = Style.EMPTY.withColor(KlaxonColors.ORANGE.getRGB());

    private static final int SPACING = 2;
    private static final int SLOT_SIDE_LENGTH = 18;
    private static final int CATALYST_DATA_ELEMENT_WIDTH = (SLOT_SIDE_LENGTH * 2) + (SPACING * 2);

    private final ResourceLocation id;
    private final List<EmiIngredient> ingredient;
    private final ExplosiveCatalystData baseCatalystData;

    public ExplosiveCatalystDefinitionEmiRecipe(RecipeHolder<ExplosiveCatalystDefinitionRecipe> recipeEntry) {
        this.id = recipeEntry.id();
        this.ingredient = List.of(EmiIngredient.of(recipeEntry.value().getIngredient()));
        this.baseCatalystData = recipeEntry.value().getData();
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return KlaxonEmiCategories.EXPLOSIVE_CATALYST_DEFINITION;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return this.id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return this.ingredient;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return this.ingredient.getFirst().getEmiStacks();
    }

    @Override
    public int getDisplayWidth() {
        return SPACING + SLOT_SIDE_LENGTH + (SPACING * 2) + SLOT_SIDE_LENGTH + (SPACING * 2) + CATALYST_DATA_ELEMENT_WIDTH + SPACING;
    }

    @Override
    public int getDisplayHeight() {
        return SPACING + SLOT_SIDE_LENGTH + SPACING;
    }

    @Override
    public void addWidgets(WidgetHolder widgetHolder) {
        final int ingredientSlotX = SPACING;
        final int ingredientSlotY = SPACING;

        final int behaviorIconX = ingredientSlotX + SLOT_SIDE_LENGTH + (SPACING * 2);
        final int behaviorIconY = SPACING;

        final int catalystDataX = behaviorIconX + SLOT_SIDE_LENGTH + (SPACING * 2);
        final int catalystDataY = SPACING;
        final int catalystDataWidth = CATALYST_DATA_ELEMENT_WIDTH;
        final int catalystDataHeight = SLOT_SIDE_LENGTH;

        // ingredient slot
        widgetHolder.addSlot(this.ingredient.getFirst(), ingredientSlotX, SPACING).recipeContext(this);

        // catalyst behavior box (24, 2) -> (42, 20)
        // widgetHolder.addTooltipText();
        // should have a swirling texture with the catalyst behavior's color around it unless hovered

        // catalyst data box
        widgetHolder.addTooltipText(
                this.getExplosiveCatalystTooltipComponents(),
                catalystDataX, catalystDataY,
                catalystDataWidth, catalystDataHeight
        );
    }

    private List<Component> getExplosiveCatalystTooltipComponents() {
        if (this.baseCatalystData.producesFire()) {
            return List.of(
                    Component.translatable("klaxon.emi.text.explosive_catalyst_definition.tooltip.data.base_explosion_power", Component.literal(String.valueOf(this.baseCatalystData.explosionPower())).setStyle(ORANGE)),
                    Component.translatable("klaxon.emi.text.explosive_catalyst_definition.tooltip.data.base_fiery").setStyle(ORANGE)
            );
        } else {
            return List.of(
                    Component.translatable("klaxon.emi.text.explosive_catalyst_definition.tooltip.data.base_explosion_power", this.baseCatalystData.explosionPower())
            );
        }
    }

}
