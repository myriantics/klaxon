package net.myriantics.klaxon.recipes.item_explosion_power;

import net.minecraft.block.Blocks;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import net.myriantics.klaxon.block.blockentities.blast_processor.BlastProcessorBlockEntity;

import static net.myriantics.klaxon.block.blockentities.blast_processor.BlastProcessorBlockEntity.CATALYST_INDEX;

public class ItemExplosionPowerRecipe implements Recipe<SimpleInventory> {
    private final Ingredient item;
    private final double explosionPower;
    private final boolean producesFire;
    private final Identifier id;

    public ItemExplosionPowerRecipe(Ingredient input, double explosionPower, boolean producesFire, Identifier id) {
        this.item = input;
        this.explosionPower = explosionPower;
        this.producesFire = producesFire;
        this.id = id;
    }

    // to whom it may concern: CHECK WHAT INDEX YOU'RE TRYING TO PULL FROM
    // GAH
    @Override
    public boolean matches(SimpleInventory inventory, World world) {
        return item.test(inventory.getStack(CATALYST_INDEX));
    }

    @Override
    public ItemStack craft(SimpleInventory inventory, DynamicRegistryManager registryManager) {
        return explosionPower > 0 ? ItemStack.EMPTY : inventory.getStack(0);
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    // TODO: actually implement dimension support with this for beds and other stuff
    public DimensionType getValidDimensions() {
        return null;
    }

    public boolean validInDimension(DimensionType dimensionType) {
        return false;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return ItemStack.EMPTY;
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

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(Blocks.TNT);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ItemExplosionPowerRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<ItemExplosionPowerRecipe> {
        private Type() {}
        public static final Type INSTANCE = new Type();
        public static final String ID = "item_explosion_power";
    }
}
