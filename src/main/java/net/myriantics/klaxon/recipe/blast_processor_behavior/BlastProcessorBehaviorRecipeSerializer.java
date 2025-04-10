package net.myriantics.klaxon.recipe.blast_processor_behavior;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerRecipe;

public class BlastProcessorBehaviorRecipeSerializer implements RecipeSerializer<BlastProcessorBehaviorRecipe> {
    public BlastProcessorBehaviorRecipeSerializer() {
    }

    private final MapCodec<BlastProcessorBehaviorRecipe> CODEC = RecordCodecBuilder.mapCodec((recipeInstance) -> {
        return recipeInstance.group(Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter((recipe) -> {
            return recipe.getIngredient();
        }), Identifier.CODEC.fieldOf("behavior_id").forGetter((recipe) -> {
            return recipe.getBehaviorId();
        })).apply(recipeInstance, BlastProcessorBehaviorRecipe::new);
    });

    private final PacketCodec<RegistryByteBuf, BlastProcessorBehaviorRecipe> PACKET_CODEC = PacketCodec.ofStatic(
            BlastProcessorBehaviorRecipeSerializer::write, BlastProcessorBehaviorRecipeSerializer::read
    );

    private static void write(RegistryByteBuf buf, BlastProcessorBehaviorRecipe recipe) {
        Ingredient.PACKET_CODEC.encode(buf, recipe.getIngredient());
        Identifier.PACKET_CODEC.encode(buf, recipe.getBehaviorId());
    }

    private static BlastProcessorBehaviorRecipe read(RegistryByteBuf buf) {
        Ingredient ingredient = Ingredient.PACKET_CODEC.decode(buf);
        Identifier behaviorId = Identifier.PACKET_CODEC.decode(buf);

        return new BlastProcessorBehaviorRecipe(ingredient, behaviorId);
    }

    @Override
    public MapCodec<BlastProcessorBehaviorRecipe> codec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, BlastProcessorBehaviorRecipe> packetCodec() {
        return PACKET_CODEC;
    }
}
