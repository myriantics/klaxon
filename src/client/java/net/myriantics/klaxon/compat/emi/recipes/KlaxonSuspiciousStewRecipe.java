package net.myriantics.klaxon.compat.emi.recipes;

import dev.emi.emi.api.recipe.EmiPatternCraftingRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.GeneratedSlotWidget;
import dev.emi.emi.api.widget.SlotWidget;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.SuspiciousEffectHolder;
import net.myriantics.klaxon.KlaxonCommon;

import java.util.List;
import java.util.Random;

public class KlaxonSuspiciousStewRecipe extends EmiPatternCraftingRecipe {

    private final List<Item> ingredients;

    public KlaxonSuspiciousStewRecipe(HolderSet.Named<Item> ingredients) {
        super(
                List.of(
                        EmiStack.of(Items.BOWL),
                        EmiStack.of(Items.RED_MUSHROOM),
                        EmiStack.of(Items.BROWN_MUSHROOM),
                        EmiIngredient.of(ingredients.stream().map(i -> EmiStack.of(i.value())).toList())
                ),
                EmiStack.of(Items.SUSPICIOUS_STEW),
                KlaxonCommon.locate("/suspicious_stew")
        );
        this.ingredients = ingredients.stream().map(Holder::value).toList();
    }

    @Override
    public SlotWidget getInputWidget(int slot, int x, int y) {
        if (slot == 0) {
            return new SlotWidget(EmiStack.of(Items.BOWL), x, y);
        } else if (slot == 1) {
            return new SlotWidget(EmiStack.of(Items.RED_MUSHROOM), x, y);
        } else if (slot == 2) {
            return new SlotWidget(EmiStack.of(Items.BROWN_MUSHROOM), x, y);
        } else if (slot == 3) {
            return new GeneratedSlotWidget(r -> EmiStack.of(getIngredient(r)), unique, x, y);
        }
        return new SlotWidget(EmiStack.EMPTY, x, y);
    }

    @Override
    public SlotWidget getOutputWidget(int x, int y) {
        return new GeneratedSlotWidget(r -> {
            SuspiciousEffectHolder holder = SuspiciousEffectHolder.tryGet(getIngredient(r));
            ItemStack stack = new ItemStack(Items.SUSPICIOUS_STEW);
            stack.set(DataComponents.SUSPICIOUS_STEW_EFFECTS, holder.getSuspiciousEffects());
            return EmiStack.of(stack);
        }, unique, x, y);
    }

    private Item getIngredient(Random random) {
        return this.ingredients.get(random.nextInt(this.ingredients.size()));
    }
}
