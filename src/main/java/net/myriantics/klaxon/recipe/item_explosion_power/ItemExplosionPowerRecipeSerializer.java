package net.myriantics.klaxon.recipe.item_explosion_power;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;

public class ItemExplosionPowerRecipeSerializer implements RecipeSerializer<ItemExplosionPowerRecipe> {
    public ItemExplosionPowerRecipeSerializer() {
    }

    private final MapCodec<ItemExplosionPowerRecipe> CODEC = RecordCodecBuilder.mapCodec((recipeInstance) -> {
        return recipeInstance.group(Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter((recipe) ->  {
            return recipe.getItem();
        }), PrimitiveCodec.DOUBLE.fieldOf("explosionPower").forGetter((recipe) -> {
            return recipe.getExplosionPower();
        }), PrimitiveCodec.BOOL.fieldOf("producesFire").forGetter((recipe) -> {
            return recipe.producesFire();
        }), PrimitiveCodec.BOOL.fieldOf("isHidden").forGetter((recipe) -> {
            return recipe.isHidden();
        })).apply(recipeInstance, ItemExplosionPowerRecipe::new);
    });

    private final PacketCodec<RegistryByteBuf, ItemExplosionPowerRecipe> PACKET_CODEC = PacketCodec.ofStatic(
            ItemExplosionPowerRecipeSerializer::write, ItemExplosionPowerRecipeSerializer::read
    );

    private static void write(RegistryByteBuf buf, ItemExplosionPowerRecipe recipe) {
        Ingredient.PACKET_CODEC.encode(buf, recipe.getItem());
        PacketCodecs.DOUBLE.encode(buf, recipe.getExplosionPower());
        PacketCodecs.BOOL.encode(buf, recipe.producesFire());
        PacketCodecs.BOOL.encode(buf, recipe.isHidden());
    }

    private static ItemExplosionPowerRecipe read(RegistryByteBuf buf) {
        Ingredient item = Ingredient.PACKET_CODEC.decode(buf);
        double explosionPower = PacketCodecs.DOUBLE.decode(buf);
        boolean producesFire = PacketCodecs.BOOL.decode(buf);
        boolean isHidden = PacketCodecs.BOOL.decode(buf);

        return new ItemExplosionPowerRecipe(item, explosionPower, producesFire, isHidden);
    }

    @Override
    public MapCodec<ItemExplosionPowerRecipe> codec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, ItemExplosionPowerRecipe> packetCodec() {
        return PACKET_CODEC;
    }
}