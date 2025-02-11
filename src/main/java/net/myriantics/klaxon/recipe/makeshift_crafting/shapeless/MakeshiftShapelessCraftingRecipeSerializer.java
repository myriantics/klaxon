package net.myriantics.klaxon.recipe.makeshift_crafting.shapeless;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.util.collection.DefaultedList;
import net.myriantics.klaxon.util.KlaxonCodecUtils;

import java.util.List;

public class MakeshiftShapelessCraftingRecipeSerializer implements RecipeSerializer<MakeshiftShapelessCraftingRecipe> {

    private final MapCodec<MakeshiftShapelessCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.STRING.optionalFieldOf("group", "").forGetter(ShapelessRecipe::getGroup),
                    CraftingRecipeCategory.CODEC.fieldOf("category").orElse(CraftingRecipeCategory.MISC).forGetter(ShapelessRecipe::getCategory),
                    ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter(MakeshiftShapelessCraftingRecipe::getRawResult),
                    Ingredient.DISALLOW_EMPTY_CODEC
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
                                                    : DataResult.success(DefaultedList.copyOf(Ingredient.EMPTY, ingredients2));
                                        }
                                    },
                                    DataResult::success
                            )
                            .forGetter(ShapelessRecipe::getIngredients),
                    KlaxonCodecUtils.INGREDIENT_LIST_CODEC.fieldOf("constant_ingredients").forGetter(MakeshiftShapelessCraftingRecipe::getConstantIngredients)
            )
                    .apply(instance, MakeshiftShapelessCraftingRecipe::new)
    );

    private final PacketCodec<RegistryByteBuf, MakeshiftShapelessCraftingRecipe> PACKET_CODEC = PacketCodec.ofStatic(
            MakeshiftShapelessCraftingRecipeSerializer::write, MakeshiftShapelessCraftingRecipeSerializer::read
    );

    private static void write(RegistryByteBuf buf, MakeshiftShapelessCraftingRecipe recipe) {
        PacketCodecs.STRING.encode(buf, recipe.getGroup());
        CraftingRecipeCategory.PACKET_CODEC.encode(buf, recipe.getCategory());
        PacketCodecs.VAR_INT.encode(buf, recipe.getIngredients().size());

        for (Ingredient ingredient : recipe.getIngredients()) {
            Ingredient.PACKET_CODEC.encode(buf, ingredient);
        }

        ItemStack.PACKET_CODEC.encode(buf, recipe.getRawResult());
        KlaxonCodecUtils.INGREDIENT_LIST_PACKET_CODEC.encode(buf, recipe.getConstantIngredients());
    }

    private static MakeshiftShapelessCraftingRecipe read(RegistryByteBuf buf) {
        String group = PacketCodecs.STRING.decode(buf);
        CraftingRecipeCategory category = CraftingRecipeCategory.PACKET_CODEC.decode(buf);
        int i = PacketCodecs.VAR_INT.decode(buf);

        DefaultedList<Ingredient> defaultedList = DefaultedList.ofSize(i, Ingredient.EMPTY);
        defaultedList.replaceAll(empty -> Ingredient.PACKET_CODEC.decode(buf));

        ItemStack result = ItemStack.PACKET_CODEC.decode(buf);
        List<Ingredient> constantIngredients = KlaxonCodecUtils.INGREDIENT_LIST_PACKET_CODEC.decode(buf);
        return new MakeshiftShapelessCraftingRecipe(group, category, result, defaultedList, constantIngredients);
    }

    @Override
    public MapCodec<MakeshiftShapelessCraftingRecipe> codec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, MakeshiftShapelessCraftingRecipe> packetCodec() {
        return PACKET_CODEC;
    }
}
