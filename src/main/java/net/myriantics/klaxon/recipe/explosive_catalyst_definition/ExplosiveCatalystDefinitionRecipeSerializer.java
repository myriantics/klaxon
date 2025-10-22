package net.myriantics.klaxon.recipe.explosive_catalyst_definition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;

public class ExplosiveCatalystDefinitionRecipeSerializer implements RecipeSerializer<ExplosiveCatalystDefinitionRecipe> {
    public ExplosiveCatalystDefinitionRecipeSerializer() {
    }

    private final MapCodec<ExplosiveCatalystDefinitionRecipe> CODEC = RecordCodecBuilder.mapCodec((recipeInstance) -> {
        return recipeInstance.group(
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter(ExplosiveCatalystDefinitionRecipe::getIngredient),
                PrimitiveCodec.DOUBLE.fieldOf("explosion_power").forGetter(ExplosiveCatalystDefinitionRecipe::getExplosionPower),
                PrimitiveCodec.BOOL.fieldOf("produces_fire").forGetter(ExplosiveCatalystDefinitionRecipe::producesFire),
                PrimitiveCodec.BOOL.fieldOf("is_hidden_from_emi").forGetter(ExplosiveCatalystDefinitionRecipe::isHidden)
        ).apply(recipeInstance, ExplosiveCatalystDefinitionRecipe::new);
    });

    private final PacketCodec<RegistryByteBuf, ExplosiveCatalystDefinitionRecipe> PACKET_CODEC = PacketCodec.ofStatic(
            ExplosiveCatalystDefinitionRecipeSerializer::write, ExplosiveCatalystDefinitionRecipeSerializer::read
    );

    private static void write(RegistryByteBuf buf, ExplosiveCatalystDefinitionRecipe recipe) {
        Ingredient.PACKET_CODEC.encode(buf, recipe.getIngredient());
        PacketCodecs.DOUBLE.encode(buf, recipe.getExplosionPower());
        PacketCodecs.BOOL.encode(buf, recipe.producesFire());
        PacketCodecs.BOOL.encode(buf, recipe.isHidden());
    }

    private static ExplosiveCatalystDefinitionRecipe read(RegistryByteBuf buf) {
        Ingredient item = Ingredient.PACKET_CODEC.decode(buf);
        double explosionPower = PacketCodecs.DOUBLE.decode(buf);
        boolean producesFire = PacketCodecs.BOOL.decode(buf);
        boolean isHidden = PacketCodecs.BOOL.decode(buf);

        return new ExplosiveCatalystDefinitionRecipe(item, explosionPower, producesFire, isHidden);
    }

    @Override
    public MapCodec<ExplosiveCatalystDefinitionRecipe> codec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, ExplosiveCatalystDefinitionRecipe> packetCodec() {
        return PACKET_CODEC;
    }
}