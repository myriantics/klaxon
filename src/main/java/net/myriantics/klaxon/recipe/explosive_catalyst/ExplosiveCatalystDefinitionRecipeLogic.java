package net.myriantics.klaxon.recipe.explosive_catalyst;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
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
        @Nullable ExplosiveCatalystData raw = computeRawExplosiveCatalystData(context.level(), catalyst);
        if (raw == null) {
            return null;
        } else {
            return raw.get(context.level()).value().transformExplosiveCatalystData(context, raw);
        }
    }

    public static @Nullable ExplosiveCatalystData computeRawExplosiveCatalystData(Level level, ItemStack catalyst) {
        if (catalyst.get(KlaxonDataComponentTypes.EXPLOSIVE_CATALYST_DATA.value()) instanceof ExplosiveCatalystData data) {
            return data;
        } else {
            return computeRawExplosiveCataystDefinitionData(level, catalyst);
        }
    }

    public static @Nullable ExplosiveCatalystData computeRawExplosiveCataystDefinitionData(Level level, ItemStack catalyst) {
        Optional<RecipeHolder<ExplosiveCatalystDefinitionRecipe>> match = level.getRecipeManager().getRecipeFor(KlaxonRecipeTypes.EXPLOSIVE_CATALYST_DEFINITION, new ExplosiveCatalystDefinitionRecipeInput(catalyst), level);
        return match.map(explosiveCatalystDefinitionRecipeRecipeHolder -> explosiveCatalystDefinitionRecipeRecipeHolder.value().getData()).orElse(null);
    }
}
