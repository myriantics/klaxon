package net.myriantics.klaxon.recipe.custom_crafting.fuse_extension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.component.configuration.ModularExplosiveBlockConfigComponent;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import net.myriantics.klaxon.registry.recipe.KlaxonRecipeSerializers;

public class FuseExtensionRecipe extends CustomRecipe {

    public static final MapCodec<FuseExtensionRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CraftingBookCategory.CODEC.fieldOf("category").forGetter(CustomRecipe::category),
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("item_to_be_extended").forGetter(i -> i.itemToBeExtended),
            Ingredient.CODEC_NONEMPTY.fieldOf("fuse_extender_ingredient").forGetter(i -> i.fuseExtenderIngredient),
            Codec.INT.fieldOf("fuse_time_ticks_per_extender").forGetter(i -> i.fuseTimeTicksPerExtender)
    ).apply(instance, FuseExtensionRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FuseExtensionRecipe> STREAM_CODEC = StreamCodec.composite(
            CraftingBookCategory.STREAM_CODEC, CustomRecipe::category,
            ByteBufCodecs.holderRegistry(Registries.ITEM).map(Holder::value, BuiltInRegistries.ITEM::wrapAsHolder), i -> i.itemToBeExtended,
            Ingredient.CONTENTS_STREAM_CODEC, i -> i.fuseExtenderIngredient,
            ByteBufCodecs.INT, i -> i.fuseTimeTicksPerExtender,
            FuseExtensionRecipe::new
    );

    public final int fuseTimeTicksPerExtender;
    public final Item itemToBeExtended;
    public final Ingredient fuseExtenderIngredient;

    public FuseExtensionRecipe(CraftingBookCategory category, Item itemToBeExtended, Ingredient fuseExtenderIngredient, int fuseTimeTicksPerExtender) {
        super(category);
        this.fuseTimeTicksPerExtender = fuseTimeTicksPerExtender;
        this.itemToBeExtended = itemToBeExtended;
        this.fuseExtenderIngredient = fuseExtenderIngredient;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        ItemStack extendableStack = null;
        int extenderCount = 0;
        for (ItemStack stack : input.items()) {
            if (stack.is(this.itemToBeExtended) && extendableStack == null && stack.has(KlaxonDataComponentTypes.MODULAR_EXPLOSIVE_BLOCK_CONFIG.value())) {
                extendableStack = stack;
            } else if (this.fuseExtenderIngredient.test(stack)) {
                extenderCount++;
            } else if (!stack.isEmpty()) {
                return false;
            }
        }

        return extenderCount > 0;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        int fuseExtenderCount = 0;
        ItemStack stackToBeExtended = null;
        for (ItemStack stack : input.items()) {
            if (this.fuseExtenderIngredient.test(stack)) {
              fuseExtenderCount++;
            } else if (stack.is(this.itemToBeExtended)) {
                stackToBeExtended = stack.copy();
            }
        }

        if (stackToBeExtended == null) {
            return ItemStack.EMPTY;
        }


        if (addFuseTicks(stackToBeExtended, fuseExtenderCount * this.fuseTimeTicksPerExtender)) {
            return stackToBeExtended;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return KlaxonRecipeSerializers.FUSE_EXTENSION_RECIPE_SERIALIZER.value();
    }

    public static boolean addFuseTicks(ItemStack stack, int addedFuseTicks) {
        if (stack.get(KlaxonDataComponentTypes.MODULAR_EXPLOSIVE_BLOCK_CONFIG.value()) instanceof ModularExplosiveBlockConfigComponent component) {
            int existingFuseTicks = component.maxFuseTime();
            stack.set(
                    KlaxonDataComponentTypes.MODULAR_EXPLOSIVE_BLOCK_CONFIG.value(),
                    new ModularExplosiveBlockConfigComponent(
                            existingFuseTicks + addedFuseTicks,
                            component.ignitionTicks(),
                            component.modifyWorld(),
                            component.exposeCatalystData()
                    )
            );
            return true;
        }
        return false;
    }

    public static class Serializer implements RecipeSerializer<FuseExtensionRecipe> {
        @Override
        public MapCodec<FuseExtensionRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FuseExtensionRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
