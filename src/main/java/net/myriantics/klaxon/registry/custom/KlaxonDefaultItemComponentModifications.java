package net.myriantics.klaxon.registry.custom;

import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.myriantics.klaxon.component.configuration.RepairIngredientOverrideComponent;
import net.myriantics.klaxon.registry.minecraft.KlaxonDataComponentTypes;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

public class KlaxonDefaultItemComponentModifications {
    public static void modify(DefaultItemComponentEvents.ModifyContext context) {
        context.modify(Items.FLINT_AND_STEEL, builder -> {
            builder.add(
                    KlaxonDataComponentTypes.REPAIR_INGREDIENT_OVERRIDE,
                    new RepairIngredientOverrideComponent(Ingredient.fromTag(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_NUGGETS)));
        });
    }
}
