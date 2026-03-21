package net.myriantics.klaxon.recipe.tool_usage;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.registry.misc.KlaxonRecipeTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ToolUsageRecipe implements Recipe<ToolUsageRecipeInput> {
    private final ResourceKey<ToolUsageRecipeType> type;
    private final Ingredient inputIngredient;
    private final ItemStack output;
    private final @Nullable SoundEvent soundOverride;

    public ToolUsageRecipe(ResourceKey<ToolUsageRecipeType> type, Ingredient inputIngredient, ItemStack output, @Nullable SoundEvent soundOverride) {
        this.type = type;
        this.inputIngredient = inputIngredient;
        this.output = output;
        this.soundOverride = soundOverride;
    }

    @Override
    public boolean matches(ToolUsageRecipeInput inventory, Level world) {
        return inventory.getTypeKey().equals(this.type) && inputIngredient.test(inventory.getItem(1));
    }

    @Override
    public ItemStack assemble(ToolUsageRecipeInput input, HolderLookup.Provider lookup) {
        return this.output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registriesLookup) {
        return this.output.copy();
    }

    @Override
    public ItemStack getToastSymbol() {
        return inputIngredient.getItems()[0];
    }

    public ResourceKey<ToolUsageRecipeType> getTypeKey() {
        return this.type;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return KlaxonRecipeTypes.TOOL_USAGE_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return KlaxonRecipeTypes.TOOL_USAGE;
    }

    public Ingredient getInputIngredient() {
        return inputIngredient;
    }

    public ItemStack getOutputStack() {
        return output;
    }

    public final @Nullable SoundEvent getSoundOverride() {
        return this.soundOverride;
    }

    public final SoundEvent getSound() {
        return this.soundOverride == null ? getDefaultSoundEvent() : this.soundOverride;
    }

    protected @NotNull SoundEvent getDefaultSoundEvent() {
        return SoundEvents.EMPTY;
    };
}
