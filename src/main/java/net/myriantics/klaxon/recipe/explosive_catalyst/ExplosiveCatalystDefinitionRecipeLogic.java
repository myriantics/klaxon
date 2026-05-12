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
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class ExplosiveCatalystDefinitionRecipeLogic {

    public static @Nullable ExplosiveCatalystData computeExplosiveCatalystData(ExplosiveCatalystContext context, Holder<Item> itemHolder) {
        return computeExplosiveCatalystData(context, itemHolder.value());
    }

    public static @Nullable ExplosiveCatalystData computeExplosiveCatalystData(ExplosiveCatalystContext context, ItemLike itemLike) {
        return computeExplosiveCatalystData(context, itemLike.asItem());
    }

    public static @Nullable ExplosiveCatalystData computeExplosiveCatalystData(ExplosiveCatalystContext context, Item item) {
        return computeExplosiveCatalystData(context, item.getDefaultInstance());
    }

    public static @Nullable ExplosiveCatalystData computeExplosiveCatalystData(ExplosiveCatalystContext context, ItemStack catalyst) {
        DataComponentMap components = catalyst.getComponents();
        if (components.get(KlaxonDataComponentTypes.EXPLOSIVE_CATALYST_DATA.value()) instanceof ExplosiveCatalystData data) {
            return data.get(context.level()).value().transformExplosiveCatalystData(context, data);
        } else {
            @Nullable ExplosiveCatalystData raw = computeRawExplosiveCatalystData(context, catalyst);
            if (raw == null) {
                return null;
            } else {
                return raw.get(context.level()).value().transformExplosiveCatalystData(context, raw);
            }
        }
    }

    public static @Nullable ExplosiveCatalystData computeRawExplosiveCatalystData(ExplosiveCatalystContext context, ItemStack catalyst) {
        if (catalyst.get(KlaxonDataComponentTypes.EXPLOSIVE_CATALYST_DATA.value()) instanceof ExplosiveCatalystData data) {
            return data;
        } else {
            Optional<RecipeHolder<ExplosiveCatalystDefinitionRecipe>> match = context.level().getRecipeManager().getRecipeFor(KlaxonRecipeTypes.EXPLOSIVE_CATALYST_DEFINITION, new ExplosiveCatalystDefinitionRecipeInput(catalyst), context.level());
            return match.map(explosiveCatalystDefinitionRecipeRecipeHolder -> explosiveCatalystDefinitionRecipeRecipeHolder.value().getData()).orElse(null);
        }
    }
}
