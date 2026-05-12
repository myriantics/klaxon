package net.myriantics.klaxon.compat.emi.recipes.special;

import com.google.common.collect.Lists;
import dev.emi.emi.api.recipe.EmiPatternCraftingRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.GeneratedSlotWidget;
import dev.emi.emi.api.widget.SlotWidget;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.myriantics.klaxon.recipe.custom_crafting.fuse_extension.FuseExtensionRecipe;

import java.util.List;
import java.util.Random;

public class FuseExtensionEmiRecipe extends EmiPatternCraftingRecipe {

    private final EmiIngredient ingredientToBeExtended;
    private final Item itemToBeExtended;
    private final List<EmiStack> possibleFuseExtenderStacks;
    private final int fuseTimeTicksPerExtender;

    public FuseExtensionEmiRecipe(FuseExtensionRecipe fuseExtensionRecipe, ResourceLocation id) {
        super(
                List.of(
                        EmiStack.of(fuseExtensionRecipe.itemToBeExtended),
                        EmiIngredient.of(fuseExtensionRecipe.fuseExtenderIngredient)
                ),
                EmiStack.of(fuseExtensionRecipe.itemToBeExtended),
                id
        );
        this.itemToBeExtended = fuseExtensionRecipe.itemToBeExtended;
        this.ingredientToBeExtended = EmiStack.of(this.itemToBeExtended);
        EmiIngredient fuseExtenderIngredient = EmiIngredient.of(fuseExtensionRecipe.fuseExtenderIngredient);
        this.possibleFuseExtenderStacks = fuseExtenderIngredient.getEmiStacks();
        this.fuseTimeTicksPerExtender = fuseExtensionRecipe.fuseTimeTicksPerExtender;
    }

    @Override
    public SlotWidget getInputWidget(int slot, int x, int y) {
        if (slot == 0) {
            return new SlotWidget(this.ingredientToBeExtended, x, y);
        } else {
            final int s = slot - 1;
            return new GeneratedSlotWidget(r -> {
                List<EmiIngredient> extenders = getFuseExtenders(r);
                if (s < extenders.size()) {
                    return extenders.get(s);
                }
                return EmiStack.EMPTY;
            }, unique, x, y);
        }
    }

    @Override
    public SlotWidget getOutputWidget(int x, int y) {
        return new GeneratedSlotWidget(
                random -> {
                    ItemStack stackToBeExtended = new ItemStack(this.itemToBeExtended);
                    FuseExtensionRecipe.addFuseTicks(stackToBeExtended, this.fuseTimeTicksPerExtender * this.getFuseExtenders(random).size());
                    return EmiStack.of(stackToBeExtended);
                },
                unique,
                x, y
        );
    }

    private List<EmiIngredient> getFuseExtenders(Random random) {
        List<EmiIngredient> extenders = Lists.newArrayList();
        int amount = 1 + random.nextInt(8);
        for (int i = 0; i < amount; i++) {
            extenders.add(this.possibleFuseExtenderStacks.get(random.nextInt(this.possibleFuseExtenderStacks.size())));
        }
        return extenders;
    }
}
