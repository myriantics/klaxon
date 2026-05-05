package net.myriantics.klaxon.compat.emi.recipes;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.myriantics.klaxon.compat.emi.infra.SpriteWidget;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractToolUsageEmiRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final ResourceLocation toolUsageAnimationRl;
    private final List<EmiIngredient> requiredTool;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;

    public AbstractToolUsageEmiRecipe(RecipeHolder<ToolUsageRecipe> recipe, ResourceLocation toolUsageAnimationRl, EmiIngredient requiredTool) {
        this.id = recipe.id();
        this.toolUsageAnimationRl = toolUsageAnimationRl;
        this.requiredTool = List.of(requiredTool);
        this.input = List.of(EmiIngredient.of(recipe.value().getInputIngredient()));
        this.output = List.of(EmiStack.of(recipe.value().getResultItem(null)));
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
        return output;
    }

    @Override
    public int getDisplayWidth() {
        return 76;
    }

    @Override
    public int getDisplayHeight() {
        return 32;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        final int slotSideLength = 18;
        final int displayHeight = this.getDisplayHeight();
        final int displayWidth = this.getDisplayWidth();
        final int elementDistance = 2;
        int animationWidth = 32;
        int animationHeight = 32;

        int slotY = (displayHeight - slotSideLength) / 2;
        int inputSlotX = elementDistance;
        int outputSlotX = (displayWidth - (slotSideLength + elementDistance));
        int animationX = findAnchorForCenter(displayWidth, animationWidth);
        int animationY = findAnchorForCenter(displayHeight, animationHeight);


        widgets.addSlot(input.getFirst(), inputSlotX, slotY).recipeContext(this);

        widgets.add(
                new SpriteWidget(
                        this.toolUsageAnimationRl,
                        animationX, animationY,
                        animationWidth, animationHeight,
                        0, 0,
                        32, 32,
                        32, 32
                )
        );

        // widgets.addSlot(getCatalysts().get(0), 29, 0).appendTooltip(Component.translatable("klaxon.emi.text.tool_usage.tool")).appendTooltip(Component.translatable("klaxon.emi.text.tool_usage.use"));

        widgets.addSlot(output.getFirst(), outputSlotX, slotY).recipeContext(this);

        // widgets.addText(Component.translatable("klaxon.emi.text.tool_usage.use_compact"), 0, 38, 4210752, false);
    }

    private static int findAnchorForCenter(int largeLength, int toAlignLength) {
        return (largeLength / 2) - (toAlignLength / 2);
    }

    @Override
    public List<EmiIngredient> getCatalysts() {
        return requiredTool;
    }
}
