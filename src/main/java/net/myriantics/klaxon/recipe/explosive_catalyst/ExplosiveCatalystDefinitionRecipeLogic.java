package net.myriantics.klaxon.recipe.explosive_catalyst;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;
import net.myriantics.klaxon.mechanics.explosive_catalyst.context.ExplosiveCatalystContext;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import net.myriantics.klaxon.registry.recipe.KlaxonRecipeTypes;

import java.util.Optional;

public abstract class ExplosiveCatalystDefinitionRecipeLogic {

    public static ExplosiveCatalystData computeExplosiveCatalystData(ExplosiveCatalystContext context, Holder<Item> itemHolder) {
        return computeExplosiveCatalystData(context, itemHolder.value());
    }

    public static ExplosiveCatalystData computeExplosiveCatalystData(ExplosiveCatalystContext context, ItemLike itemLike) {
        return computeExplosiveCatalystData(context, itemLike.asItem());
    }

    public static ExplosiveCatalystData computeExplosiveCatalystData(ExplosiveCatalystContext context, Item item) {
        return computeExplosiveCatalystData(context, item.getDefaultInstance());
    }

    public static ExplosiveCatalystData computeExplosiveCatalystData(ExplosiveCatalystContext context, ItemStack catalyst) {
        DataComponentMap components = catalyst.getComponents();
        if (components.get(KlaxonDataComponentTypes.EXPLOSIVE_CATALYST_DATA.value()) instanceof ExplosiveCatalystData data) {
            return data.get(context.level()).value().transformExplosiveCatalystData(context, data);
        } else {
            ExplosiveCatalystData raw = computeRawExplosiveCatalystData(context, catalyst);
            return raw.get(context.level()).value().transformExplosiveCatalystData(context, raw);
        }
    }

    public static ExplosiveCatalystData computeRawExplosiveCatalystData(ExplosiveCatalystContext context, ItemStack catalyst) {
        Optional<RecipeHolder<ExplosiveCatalystDefinitionRecipe>> match = context.level().getRecipeManager().getRecipeFor(KlaxonRecipeTypes.EXPLOSIVE_CATALYST_DEFINITION, new ExplosiveCatalystDefinitionRecipeInput(catalyst), context.level());
        if (match.isPresent()) {
            return match.get().value().getData();
        } else {
            return ExplosiveCatalystData.ZERO;
        }
    }
}
