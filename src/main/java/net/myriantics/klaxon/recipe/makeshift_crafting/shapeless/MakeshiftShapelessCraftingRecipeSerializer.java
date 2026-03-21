package net.myriantics.klaxon.recipe.makeshift_crafting.shapeless;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.myriantics.klaxon.util.KlaxonCodecUtils;

import java.util.List;

public class MakeshiftShapelessCraftingRecipeSerializer implements RecipeSerializer<MakeshiftShapelessCraftingRecipe> {

    private final MapCodec<MakeshiftShapelessCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.STRING.optionalFieldOf("group", "").forGetter(ShapelessRecipe::getGroup),
                    CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(ShapelessRecipe::category),
                    ItemStack.STRICT_CODEC.fieldOf("result").forGetter(MakeshiftShapelessCraftingRecipe::getRawResult),
                    Ingredient.CODEC_NONEMPTY
                            .listOf()
                            .fieldOf("ingredients")
                            .flatXmap(
                                    ingredients -> {
                                        Ingredient[] ingredients2 = (Ingredient[])ingredients.stream().filter(ingredient -> !ingredient.isEmpty()).toArray(Ingredient[]::new);
                                        if (ingredients2.length == 0) {
                                            return DataResult.error(() -> "No ingredients for shapeless recipe");
                                        } else {
                                            return ingredients2.length > 9
                                                    ? DataResult.error(() -> "Too many ingredients for shapeless recipe")
                                                    : DataResult.success(NonNullList.of(Ingredient.EMPTY, ingredients2));
                                        }
                                    },
                                    DataResult::success
                            )
                            .forGetter(ShapelessRecipe::getIngredients),
                    KlaxonCodecUtils.INGREDIENT_LIST_CODEC.fieldOf("constant_ingredients").forGetter(MakeshiftShapelessCraftingRecipe::getConstantIngredients)
            )
                    .apply(instance, MakeshiftShapelessCraftingRecipe::new)
    );

    private final StreamCodec<RegistryFriendlyByteBuf, MakeshiftShapelessCraftingRecipe> PACKET_CODEC = StreamCodec.of(
            MakeshiftShapelessCraftingRecipeSerializer::write, MakeshiftShapelessCraftingRecipeSerializer::read
    );

    private static void write(RegistryFriendlyByteBuf buf, MakeshiftShapelessCraftingRecipe recipe) {
        ByteBufCodecs.STRING_UTF8.encode(buf, recipe.getGroup());
        CraftingBookCategory.STREAM_CODEC.encode(buf, recipe.category());
        ByteBufCodecs.VAR_INT.encode(buf, recipe.getIngredients().size());

        for (Ingredient ingredient : recipe.getIngredients()) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient);
        }

        ItemStack.STREAM_CODEC.encode(buf, recipe.getRawResult());
        KlaxonCodecUtils.INGREDIENT_LIST_PACKET_CODEC.encode(buf, recipe.getConstantIngredients());
    }

    private static MakeshiftShapelessCraftingRecipe read(RegistryFriendlyByteBuf buf) {
        String group = ByteBufCodecs.STRING_UTF8.decode(buf);
        CraftingBookCategory category = CraftingBookCategory.STREAM_CODEC.decode(buf);
        int i = ByteBufCodecs.VAR_INT.decode(buf);

        NonNullList<Ingredient> defaultedList = NonNullList.withSize(i, Ingredient.EMPTY);
        defaultedList.replaceAll(empty -> Ingredient.CONTENTS_STREAM_CODEC.decode(buf));

        ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
        List<Ingredient> constantIngredients = KlaxonCodecUtils.INGREDIENT_LIST_PACKET_CODEC.decode(buf);
        return new MakeshiftShapelessCraftingRecipe(group, category, result, defaultedList, constantIngredients);
    }

    @Override
    public MapCodec<MakeshiftShapelessCraftingRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, MakeshiftShapelessCraftingRecipe> streamCodec() {
        return PACKET_CODEC;
    }
}
