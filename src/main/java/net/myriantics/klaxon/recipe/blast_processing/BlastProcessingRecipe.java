package net.myriantics.klaxon.recipe.blast_processing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.registry.recipe.KlaxonRecipeTypes;

public interface BlastProcessingRecipe extends Recipe<BlastProcessingRecipeInput> {

    ItemStack[] properlyAssemble(BlastProcessingRecipeInput input, HolderLookup.Provider registries);

    default float getExplosionPowerMin() {
        return this.getBounds().explosionPowerMin;
    }

    default float getExplosionPowerMax() {
        return this.getBounds().explosionPowerMax;
    }

    Bounds getBounds();

    Ingredient getIngredient();

    ItemStack[] getDisplayStacks();

    @Override
    default boolean matches(BlastProcessingRecipeInput inventory, Level world) {
        // check if explosion power exists and is within bounds
        if (this.getBounds().matches(inventory.getCatalystData().explosionPower())) {
            return this.getIngredient().test(inventory.getIngredientStack());
        } else {
            return false;
        }
    }

    @Override
    default boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    // not used because it only allows for the output of 1 itemstack (cringe)
    @Override
    default ItemStack assemble(BlastProcessingRecipeInput input, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    default ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    default RecipeType<?> getType() {
        return KlaxonRecipeTypes.BLAST_PROCESSING.value();
    }

    record Bounds(float explosionPowerMin, float explosionPowerMax) {
        private static final Codec<Bounds> RAW_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.floatRange(0, Float.MAX_VALUE).fieldOf("explosion_power_min").forGetter(Bounds::explosionPowerMin),
                Codec.floatRange(0, Float.MAX_VALUE).fieldOf("explosion_power_max").forGetter(Bounds::explosionPowerMax)
        ).apply(instance, Bounds::new));

        public static final Codec<Bounds> CODEC = RAW_CODEC.validate(bounds -> {
            if (bounds.explosionPowerMin < bounds.explosionPowerMax) {
                return DataResult.success(bounds);
            } else {
                return DataResult.error(() -> "Explosion Power minimum cannot be greater than or equal to maximum!");
            }
        });

        public static final StreamCodec<RegistryFriendlyByteBuf, Bounds> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT, Bounds::explosionPowerMin,
                ByteBufCodecs.FLOAT, Bounds::explosionPowerMax,
                Bounds::new
        );

        public boolean matches(double explosionPower) {
            return explosionPower >= this.explosionPowerMin && explosionPower <= this.explosionPowerMax;
        }
    }
}
