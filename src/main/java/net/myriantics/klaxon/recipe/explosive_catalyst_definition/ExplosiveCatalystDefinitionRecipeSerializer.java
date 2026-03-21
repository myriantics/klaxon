package net.myriantics.klaxon.recipe.explosive_catalyst_definition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class ExplosiveCatalystDefinitionRecipeSerializer implements RecipeSerializer<ExplosiveCatalystDefinitionRecipe> {

    private final MapCodec<ExplosiveCatalystDefinitionRecipe> CODEC = RecordCodecBuilder.mapCodec((recipeInstance) -> recipeInstance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(ExplosiveCatalystDefinitionRecipe::getIngredient),
            ExplosiveCatalystData.CODEC.fieldOf("explosive_catalyst_data").forGetter(ExplosiveCatalystDefinitionRecipe::getData),
            PrimitiveCodec.BOOL.fieldOf("is_hidden_from_emi").forGetter(ExplosiveCatalystDefinitionRecipe::isHidden)
    ).apply(recipeInstance, ExplosiveCatalystDefinitionRecipe::new));

    private final StreamCodec<RegistryFriendlyByteBuf, ExplosiveCatalystDefinitionRecipe> PACKET_CODEC = StreamCodec.of(
            ExplosiveCatalystDefinitionRecipeSerializer::write, ExplosiveCatalystDefinitionRecipeSerializer::read
    );

    private static void write(RegistryFriendlyByteBuf buf, ExplosiveCatalystDefinitionRecipe recipe) {
        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.getIngredient());
        ExplosiveCatalystData.PACKET_CODEC.encode(buf, recipe.getData());
        ByteBufCodecs.BOOL.encode(buf, recipe.isHidden());
    }

    private static ExplosiveCatalystDefinitionRecipe read(RegistryFriendlyByteBuf buf) {
        Ingredient item = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
        ExplosiveCatalystData data = ExplosiveCatalystData.PACKET_CODEC.decode(buf);
        boolean isHidden = ByteBufCodecs.BOOL.decode(buf);

        return new ExplosiveCatalystDefinitionRecipe(item, data, isHidden);
    }

    @Override
    public MapCodec<ExplosiveCatalystDefinitionRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ExplosiveCatalystDefinitionRecipe> streamCodec() {
        return PACKET_CODEC;
    }
}