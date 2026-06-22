package net.myriantics.klaxon.recipe.blast_processing.special;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.CommonColors;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeInput;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingTransmutationRecipe;
import net.myriantics.klaxon.registry.recipe.KlaxonRecipeSerializers;

import java.util.List;
import java.util.Map;

public class DecoratedPotCrackingBlastProcessingRecipe extends BlastProcessingTransmutationRecipe {

    public static final RecipeSerializer<DecoratedPotCrackingBlastProcessingRecipe> SERIALIZER = create(DecoratedPotCrackingBlastProcessingRecipe::new);
    private static final ItemLore DISPLAY_CRACKED_INDICATOR_LORE = new ItemLore(List.of(Component.translatable("klaxon.text.tooltip.pot_cracked_indicator").setStyle(Style.EMPTY.withColor(CommonColors.LIGHT_GRAY).withItalic(false))));

    public DecoratedPotCrackingBlastProcessingRecipe(Ingredient ingredient, float explosionPowerMin, float explosionPowerMax) {
        super(ingredient, explosionPowerMin, explosionPowerMax);
    }

    public DecoratedPotCrackingBlastProcessingRecipe(Ingredient ingredient, Bounds bounds) {
        super(ingredient, bounds);
    }

    @Override
    public void addDisplayComponents(ItemStack stack) {
        stack.set(DataComponents.LORE, DISPLAY_CRACKED_INDICATOR_LORE);
    }

    @Override
    public boolean matches(BlastProcessingRecipeInput inventory, Level world) {
        return super.matches(inventory, world) && inventory.getIngredientStack().getItem() instanceof BlockItem blockItem && blockItem.getBlock().getStateDefinition().getProperties().contains(BlockStateProperties.CRACKED);
    }

    @Override
    public ItemStack[] properlyAssemble(BlastProcessingRecipeInput input, HolderLookup.Provider registries) {
        ItemStack newStack = input.getIngredientStack().copy();
        newStack.set(DataComponents.BLOCK_STATE, new BlockItemStateProperties(Map.of("cracked", "true")));
        return new ItemStack[] {newStack};
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return KlaxonRecipeSerializers.BLAST_PROCESSING_DECORATED_POT_CRACKING.value();
    }
}
