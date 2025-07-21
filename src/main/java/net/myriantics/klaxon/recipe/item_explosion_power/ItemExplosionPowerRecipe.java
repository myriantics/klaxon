package net.myriantics.klaxon.recipe.item_explosion_power;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import net.myriantics.klaxon.registry.misc.KlaxonRecipeTypes;

public class ItemExplosionPowerRecipe implements Recipe<ExplosiveCatalystRecipeInput> {
    private final Ingredient ingredient;
    private final double explosionPower;
    private final boolean producesFire;
    private final boolean isHidden;

    public ItemExplosionPowerRecipe(Ingredient input, double explosionPower, boolean producesFire, boolean isHidden) {
        this.ingredient = input;
        this.explosionPower = explosionPower;
        this.producesFire = producesFire;
        this.isHidden = isHidden;
    }

    // to whom it may concern: CHECK WHAT INDEX YOU'RE TRYING TO PULL FROM
    // GAH
    @Override
    public boolean matches(ExplosiveCatalystRecipeInput input, World world) {
        return ingredient.test(input.catalystStack());
    }

    @Override
    public ItemStack craft(ExplosiveCatalystRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return input.catalystStack();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return ItemStack.EMPTY;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public double getExplosionPower() {
        return explosionPower;
    }

    public boolean producesFire() {
        return producesFire;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public boolean matchesConditions(double explosionPowerMin, double explosionPowerMax) {
        if (explosionPowerMin <= explosionPower && explosionPower <= explosionPowerMax) {
            //return requiresFire == producesFire || producesFire;
            return true;
        }
        return false;
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(Blocks.TNT);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return KlaxonRecipeTypes.ITEM_EXPLOSION_POWER_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return KlaxonRecipeTypes.ITEM_EXPLOSION_POWER;
    }

}
