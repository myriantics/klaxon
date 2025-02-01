package net.myriantics.klaxon.recipe.makeshift_crafting.shaped;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerRecipe;
import net.myriantics.klaxon.util.KlaxonCodecUtils;

import java.util.List;

public class MakeshiftShapedCraftingRecipeSerializer implements RecipeSerializer<MakeshiftShapedCraftingRecipe> {

    private final MapCodec<MakeshiftShapedCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(
                    instance -> instance.group(
                            Codec.STRING.optionalFieldOf("group", "").forGetter(ShapedRecipe::getGroup),
                            CraftingRecipeCategory.CODEC.fieldOf("category").orElse(CraftingRecipeCategory.MISC).forGetter(ShapedRecipe::getCategory),
                            RawShapedRecipe.CODEC.forGetter(recipe -> recipe.raw),
                            KlaxonCodecUtils.INGREDIENT_LIST_CODEC.fieldOf("potential_makeshift_ingredients").forGetter(MakeshiftShapedCraftingRecipe::getPotentialMakeshiftIngredients),
                            ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter(MakeshiftShapedCraftingRecipe::getRawResult),
                            Codec.BOOL.optionalFieldOf("show_notification", Boolean.TRUE).forGetter(ShapedRecipe::showNotification)
            )
            .apply(instance, MakeshiftShapedCraftingRecipe::new)
    );

    private final PacketCodec<RegistryByteBuf, MakeshiftShapedCraftingRecipe> PACKET_CODEC = PacketCodec.ofStatic(
            MakeshiftShapedCraftingRecipeSerializer::write, MakeshiftShapedCraftingRecipeSerializer::read
    );

    private static void write(RegistryByteBuf buf, MakeshiftShapedCraftingRecipe recipe) {
        PacketCodecs.STRING.encode(buf, recipe.getGroup());
        CraftingRecipeCategory.PACKET_CODEC.encode(buf, recipe.getCategory());
        RawShapedRecipe.PACKET_CODEC.encode(buf, recipe.raw);
        KlaxonCodecUtils.INGREDIENT_LIST_PACKET_CODEC.encode(buf, recipe.getPotentialMakeshiftIngredients());
        ItemStack.PACKET_CODEC.encode(buf, recipe.getRawResult());
        PacketCodecs.BOOL.encode(buf, recipe.showNotification());
    }

    private static MakeshiftShapedCraftingRecipe read(RegistryByteBuf buf) {
        String group = PacketCodecs.STRING.decode(buf);
        CraftingRecipeCategory category = CraftingRecipeCategory.PACKET_CODEC.decode(buf);
        RawShapedRecipe raw = RawShapedRecipe.PACKET_CODEC.decode(buf);
        List<Ingredient> potentialMakeshiftIngredients = KlaxonCodecUtils.INGREDIENT_LIST_PACKET_CODEC.decode(buf);
        ItemStack result =  ItemStack.PACKET_CODEC.decode(buf);
        boolean showNotification = PacketCodecs.BOOL.decode(buf);

        return new MakeshiftShapedCraftingRecipe(group, category, raw, potentialMakeshiftIngredients, result, showNotification);
    }

    @Override
    public MapCodec<MakeshiftShapedCraftingRecipe> codec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, MakeshiftShapedCraftingRecipe> packetCodec() {
        return PACKET_CODEC;
    }
}
