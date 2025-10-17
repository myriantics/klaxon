package net.myriantics.klaxon.recipe.tool_usage;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import net.myriantics.klaxon.registry.misc.KlaxonRecipeTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ToolUsageRecipe implements Recipe<ToolUsageRecipeInput> {
    private final RegistryKey<ToolUsageRecipeType> type;
    private final Ingredient inputIngredient;
    private final ItemStack output;
    private final @Nullable SoundEvent soundOverride;

    public ToolUsageRecipe(RegistryKey<ToolUsageRecipeType> type, Ingredient inputIngredient, ItemStack output, @Nullable SoundEvent soundOverride) {
        this.type = type;
        this.inputIngredient = inputIngredient;
        this.output = output;
        this.soundOverride = soundOverride;
    }

    @Override
    public boolean matches(ToolUsageRecipeInput inventory, World world) {
        return inventory.getTypeKey().equals(this.type) && inputIngredient.test(inventory.getStackInSlot(1));
    }

    @Override
    public ItemStack craft(ToolUsageRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return this.output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return this.output.copy();
    }

    @Override
    public ItemStack createIcon() {
        return inputIngredient.getMatchingStacks()[0];
    }

    public RegistryKey<ToolUsageRecipeType> getTypeKey() {
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
        return SoundEvents.INTENTIONALLY_EMPTY;
    };
}
