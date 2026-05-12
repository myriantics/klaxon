package net.myriantics.klaxon.recipe.makeshift_crafting.shaped;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.myriantics.klaxon.util.KlaxonCodecUtils;

import java.util.List;

public class MakeshiftShapedCraftingRecipeSerializer implements RecipeSerializer<MakeshiftShapedCraftingRecipe> {

    private final MapCodec<MakeshiftShapedCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(
                    instance -> instance.group(
                            Codec.STRING.optionalFieldOf("group", "").forGetter(ShapedRecipe::getGroup),
                            CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(ShapedRecipe::category),
                            ShapedRecipePattern.MAP_CODEC.forGetter(recipe -> recipe.raw),
                            KlaxonCodecUtils.INGREDIENT_LIST_CODEC.fieldOf("constant_ingredients").forGetter(MakeshiftShapedCraftingRecipe::getConstantIngredients),
                            ItemStack.STRICT_CODEC.fieldOf("result").forGetter(MakeshiftShapedCraftingRecipe::getRawResult),
                            Codec.BOOL.optionalFieldOf("show_notification", Boolean.TRUE).forGetter(ShapedRecipe::showNotification)
            )
            .apply(instance, MakeshiftShapedCraftingRecipe::new)
    );

    private final StreamCodec<RegistryFriendlyByteBuf, MakeshiftShapedCraftingRecipe> PACKET_CODEC = StreamCodec.of(
            MakeshiftShapedCraftingRecipeSerializer::write, MakeshiftShapedCraftingRecipeSerializer::read
    );

    private static void write(RegistryFriendlyByteBuf buf, MakeshiftShapedCraftingRecipe recipe) {
        ByteBufCodecs.STRING_UTF8.encode(buf, recipe.getGroup());
        CraftingBookCategory.STREAM_CODEC.encode(buf, recipe.category());
        ShapedRecipePattern.STREAM_CODEC.encode(buf, recipe.raw);
        KlaxonCodecUtils.INGREDIENT_LIST_PACKET_CODEC.encode(buf, recipe.getConstantIngredients());
        ItemStack.STREAM_CODEC.encode(buf, recipe.getRawResult());
        ByteBufCodecs.BOOL.encode(buf, recipe.showNotification());
    }

    private static MakeshiftShapedCraftingRecipe read(RegistryFriendlyByteBuf buf) {
        String group = ByteBufCodecs.STRING_UTF8.decode(buf);
        CraftingBookCategory category = CraftingBookCategory.STREAM_CODEC.decode(buf);
        ShapedRecipePattern raw = ShapedRecipePattern.STREAM_CODEC.decode(buf);
        List<Ingredient> constantIngredients = KlaxonCodecUtils.INGREDIENT_LIST_PACKET_CODEC.decode(buf);
        ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
        boolean showNotification = ByteBufCodecs.BOOL.decode(buf);

        return new MakeshiftShapedCraftingRecipe(group, category, raw, constantIngredients, result, showNotification);
    }

    @Override
    public MapCodec<MakeshiftShapedCraftingRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, MakeshiftShapedCraftingRecipe> streamCodec() {
        return PACKET_CODEC;
    }
}
