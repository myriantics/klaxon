package net.myriantics.klaxon.compat.emi.recipes;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.myriantics.klaxon.compat.emi.KlaxonEmiRecipeCategories;
import net.myriantics.klaxon.recipe.nether_reaction.NetherReactionRecipe;
import net.myriantics.klaxon.registry.item.KlaxonBlockItems;
import net.myriantics.klaxon.registry.render.KlaxonTextures;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NetherReactionEmiRecipe implements EmiRecipe {

    private static final ResourceLocation BACKGROUND_TEXTURE = KlaxonTextures.decorate(KlaxonTextures.NETHER_REACTION_EMI_BACKGROUND);

    private static final List<Component> NETHER_REACTOR_HOVER_TEXT = List.of(Component.translatable("klaxon.emi.text.nether_reaction.hover"));

    private final ResourceLocation id;
    private final List<EmiIngredient> inputStacks;
    private final List<EmiStack> outputStacks;

    public NetherReactionEmiRecipe(RecipeHolder<NetherReactionRecipe> recipeEntry) {
        this.id = recipeEntry.id();
        this.inputStacks = Collections.singletonList(EmiIngredient.of(Arrays.stream(
                recipeEntry.value().getBlockIngredient().getDisplayStacks()
        ).filter(itemStack -> {
            // jank hack but whatevs
            if (itemStack.getItem() instanceof BlockItem blockItem) {
                return !blockItem.getBlock().defaultBlockState().is(KlaxonBlockTags.NETHER_REACTION_IMMUNE);
            }

            return true;
        }).map(EmiStack::of).toList()));
        this.outputStacks = List.of(EmiStack.of(
                KlaxonBlockItems.getBlockDisplayStack(recipeEntry.value().getOutputBlock())
        ));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return KlaxonEmiRecipeCategories.NETHER_REACTION;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return inputStacks;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return outputStacks;
    }

    @Override
    public int getDisplayWidth() {
        return 96;
    }

    @Override
    public int getDisplayHeight() {
        return 26;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(getInputs().get(0), 4, 4);
        widgets.addSlot(outputStacks.get(0), 74, 4).recipeContext(this);

        widgets.addTexture(new EmiTexture(
                BACKGROUND_TEXTURE,
                0, 0,
                96,
                26,
                96,
                26,
                128,
                128
        ), 0, 0);

        widgets.addTooltipText(
                NETHER_REACTOR_HOVER_TEXT,
                40, 4,
                18, 18
        );
    }
}
