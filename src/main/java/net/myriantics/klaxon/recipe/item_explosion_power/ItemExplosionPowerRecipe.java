package net.myriantics.klaxon.recipe.item_explosion_power;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import net.myriantics.klaxon.registry.minecraft.KlaxonRecipeTypes;

import static net.myriantics.klaxon.block.customblocks.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity.CATALYST_INDEX;

public class ItemExplosionPowerRecipe implements Recipe<RecipeInput> {
    private final Ingredient item;
    private final double explosionPower;
    private final boolean producesFire;
    private final boolean isHidden;

    public ItemExplosionPowerRecipe(Ingredient input, double explosionPower, boolean producesFire, boolean isHidden) {
        this.item = input;
        this.explosionPower = explosionPower;
        this.producesFire = producesFire;
        this.isHidden = isHidden;
    }

    // to whom it may concern: CHECK WHAT INDEX YOU'RE TRYING TO PULL FROM
    // GAH
    @Override
    public boolean matches(RecipeInput input, World world) {
        return item.test(input.getStackInSlot(CATALYST_INDEX));
    }

    @Override
    public ItemStack craft(RecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return explosionPower > 0 ? ItemStack.EMPTY : input.getStackInSlot(0);
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return null;
    }

    public Ingredient getItem() {
        return item;
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
