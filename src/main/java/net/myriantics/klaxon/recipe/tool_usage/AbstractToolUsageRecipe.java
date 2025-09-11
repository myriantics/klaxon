package net.myriantics.klaxon.recipe.tool_usage;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractToolUsageRecipe implements Recipe<RecipeInput> {
    private final Ingredient requiredTool;
    private final Ingredient inputIngredient;
    private final ItemStack output;
    private final @Nullable SoundEvent soundOverride;

    public AbstractToolUsageRecipe(Ingredient requiredTool, Ingredient inputIngredient, ItemStack output, @Nullable SoundEvent soundOverride) {
        this.requiredTool = requiredTool;
        this.inputIngredient = inputIngredient;
        this.output = output;
        this.soundOverride = soundOverride;
    }

    @Override
    public boolean matches(RecipeInput inventory, World world) {
        return requiredTool.test(inventory.getStackInSlot(0)) && inputIngredient.test(inventory.getStackInSlot(1));
    }

    @Override
    public ItemStack craft(RecipeInput input, RegistryWrapper.WrapperLookup lookup) {
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
        return requiredTool.getMatchingStacks()[0];
    }

    public Ingredient getRequiredTool() {
        return requiredTool;
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

    protected abstract @NotNull SoundEvent getDefaultSoundEvent();
}
