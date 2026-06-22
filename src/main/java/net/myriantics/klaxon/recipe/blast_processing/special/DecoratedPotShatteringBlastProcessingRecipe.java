package net.myriantics.klaxon.recipe.blast_processing.special;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.entity.PotDecorations;
import net.myriantics.klaxon.recipe.RecipeOutputCompound;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipe;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeInput;
import net.myriantics.klaxon.registry.recipe.KlaxonRecipeSerializers;
import net.myriantics.klaxon.util.KlaxonItemStackHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class DecoratedPotShatteringBlastProcessingRecipe implements BlastProcessingRecipe {

    public static final MapCodec<DecoratedPotShatteringBlastProcessingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(DecoratedPotShatteringBlastProcessingRecipe::getIngredient),
            Bounds.CODEC.fieldOf("bounds").forGetter(DecoratedPotShatteringBlastProcessingRecipe::getBounds),
            Codec.doubleRange(0, 1).fieldOf("success_chance").forGetter(i -> i.successChance)
    ).apply(instance, DecoratedPotShatteringBlastProcessingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DecoratedPotShatteringBlastProcessingRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, DecoratedPotShatteringBlastProcessingRecipe::getIngredient,
            Bounds.STREAM_CODEC, DecoratedPotShatteringBlastProcessingRecipe::getBounds,
            ByteBufCodecs.DOUBLE, i -> i.successChance,
            DecoratedPotShatteringBlastProcessingRecipe::new
    );

    private final Ingredient potIngredient;
    private final Bounds bounds;
    public final double successChance;

    public DecoratedPotShatteringBlastProcessingRecipe(Ingredient potIngredient, float explosionPowerMin, float explosionPowerMax, double successChance) {
        this(potIngredient, new Bounds(explosionPowerMin, explosionPowerMax), successChance);
    }

    public DecoratedPotShatteringBlastProcessingRecipe(Ingredient potIngredient, Bounds bounds, double successChance) {
        this.potIngredient = potIngredient;
        this.bounds = bounds;
        this.successChance = successChance;
    }

    @Override
    public ItemStack[] properlyAssemble(BlastProcessingRecipeInput input, HolderLookup.Provider registries) {
        if (input.getIngredientStack().get(DataComponents.POT_DECORATIONS) instanceof PotDecorations decorations) {
            ItemStack[] stacks = this.gatherPotDecorationStacks(decorations);
            for (ItemStack stack : stacks) {
                stack.setCount(RecipeOutputCompound.getCountForChance(stack, input.getRandom(), this.successChance));
            }
            return stacks;
        } else {
            return new ItemStack[0];
        }
    }

    @Override
    public Bounds getBounds() {
        return this.bounds;
    }

    @Override
    public Ingredient getIngredient() {
        return this.potIngredient;
    }

    @Override
    public ItemStack[] getDisplayStacks(BlastProcessingRecipeInput input, HolderLookup.Provider registries) {
        ItemStack ingredientStack = input.getIngredientStack();
        if (ingredientStack.get(DataComponents.POT_DECORATIONS) instanceof PotDecorations decorations) {
            ItemStack[] displayDecorationStacks = this.gatherPotDecorationStacks(decorations);
            for (ItemStack stack : displayDecorationStacks) {
                if (!stack.isEmpty()) {
                    RecipeOutputCompound.setRecipeOutputChanceLore(stack, this.successChance);
                }
            }
            return displayDecorationStacks;
        } else {
            return new ItemStack[0];
        }
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return KlaxonRecipeSerializers.BLAST_PROCESSING_DECORATED_POT_SHATTERING.value();
    }

    public ItemStack[] gatherPotDecorationStacks(PotDecorations decorations) {
        ItemStack[] stacks = new ItemStack[] {
                new ItemStack(decorations.front().orElse(Items.BRICK)),
                new ItemStack(decorations.right().orElse(Items.BRICK)),
                new ItemStack(decorations.back().orElse(Items.BRICK)),
                new ItemStack(decorations.left().orElse(Items.BRICK))
        };

        for (int i = 0; i < stacks.length; i++) {
            if (stacks[i].isEmpty()) {
                continue;
            }

            for (int j = i + 1; j < stacks.length; j++) {
                KlaxonItemStackHelper.combineStacksIfPossible(stacks[i], stacks[j]);
            }
        }

        Arrays.sort(stacks, (stack1, stack2) -> (stack1.isEmpty() ? 1 : 0) + (stack2.isEmpty() ? -1 : 0));

        return stacks;
    }

    public static final class Serializer implements RecipeSerializer<DecoratedPotShatteringBlastProcessingRecipe> {

        @Override
        public MapCodec<DecoratedPotShatteringBlastProcessingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, DecoratedPotShatteringBlastProcessingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
