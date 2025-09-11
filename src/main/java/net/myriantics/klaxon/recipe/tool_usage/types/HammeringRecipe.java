package net.myriantics.klaxon.recipe.tool_usage.types;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.myriantics.klaxon.recipe.tool_usage.AbstractToolUsageRecipe;
import net.myriantics.klaxon.registry.misc.KlaxonRecipeTypes;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import org.jetbrains.annotations.NotNull;

public class HammeringRecipe extends AbstractToolUsageRecipe {

    public HammeringRecipe(Ingredient inputIngredient, ItemStack output, SoundEvent soundOverride) {
        super(Ingredient.fromTag(KlaxonItemTags.RECIPE_PROCESSING_HAMMERS), inputIngredient, output, soundOverride);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return KlaxonRecipeTypes.HAMMERING_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return KlaxonRecipeTypes.HAMMERING;
    }

    @Override
    protected @NotNull SoundEvent getDefaultSoundEvent() {
        return SoundEvents.BLOCK_ANVIL_LAND;
    }
}
