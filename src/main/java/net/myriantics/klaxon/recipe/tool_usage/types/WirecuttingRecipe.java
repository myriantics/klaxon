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
import org.jetbrains.annotations.Nullable;

public class WirecuttingRecipe extends AbstractToolUsageRecipe {
    public WirecuttingRecipe(Ingredient inputIngredient, ItemStack output, @Nullable SoundEvent soundEvent) {
        super(Ingredient.fromTag(KlaxonItemTags.RECIPE_PROCESSING_WIRECUTTERS), inputIngredient, output, soundEvent);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return KlaxonRecipeTypes.WIRECUTTING_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return KlaxonRecipeTypes.WIRECUTTING;
    }

    @Override
    protected @NotNull SoundEvent getDefaultSoundEvent() {
        return SoundEvents.BLOCK_CHAIN_BREAK;
    }
}
