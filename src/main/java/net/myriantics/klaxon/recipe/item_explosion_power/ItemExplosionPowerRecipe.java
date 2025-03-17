package net.myriantics.klaxon.recipe.item_explosion_power;

import net.minecraft.block.Blocks;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.myriantics.klaxon.registry.KlaxonRecipeTypes;

import static net.myriantics.klaxon.block.customblocks.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity.CATALYST_INDEX;

public class ItemExplosionPowerRecipe implements Recipe<Inventory> {
    private final Ingredient item;
    private final double explosionPower;
    private final boolean producesFire;
    private final boolean isHidden;

    private final Identifier identifier;

    public ItemExplosionPowerRecipe(Identifier id, Ingredient input, double explosionPower, boolean producesFire, boolean isHidden) {
        this.item = input;
        this.explosionPower = explosionPower;
        this.producesFire = producesFire;
        this.isHidden = isHidden;
        this.identifier = id;
    }

    // to whom it may concern: CHECK WHAT INDEX YOU'RE TRYING TO PULL FROM
    // GAH
    @Override
    public boolean matches(Inventory input, World world) {
        return item.test(input.getStack(CATALYST_INDEX));
    }

    @Override
    public ItemStack craft(Inventory input, DynamicRegistryManager registryManager) {
        return explosionPower > 0 ? ItemStack.EMPTY : input.getStack(0);
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return null;
    }

    public Ingredient getIngredient() {
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

    @Override
    public Identifier getId() {
        return identifier;
    }
}
