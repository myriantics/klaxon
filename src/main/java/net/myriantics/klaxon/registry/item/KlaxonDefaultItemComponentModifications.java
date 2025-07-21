package net.myriantics.klaxon.registry.item;

import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.myriantics.klaxon.component.configuration.MeleeDamageTypeOverrideComponent;
import net.myriantics.klaxon.component.configuration.DefaultInnateItemEnchantmentsComponent;
import net.myriantics.klaxon.component.configuration.RepairIngredientOverrideComponent;
import net.myriantics.klaxon.component.configuration.ToolUseRecipeConfigComponent;
import net.myriantics.klaxon.registry.entity.KlaxonDamageTypes;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

import java.util.Map;

public abstract class KlaxonDefaultItemComponentModifications {
    public static void modify(DefaultItemComponentEvents.ModifyContext context) {

        // make flint and steel repairable with steel nuggets
        context.modify(Items.FLINT_AND_STEEL, builder -> {
            builder.add(
                            KlaxonDataComponentTypes.REPAIR_INGREDIENT_OVERRIDE,
                            new RepairIngredientOverrideComponent(Ingredient.fromTag(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_NUGGETS))
                    )
                    .add(
                            KlaxonDataComponentTypes.MELEE_DAMAGE_TYPE_OVERRIDE,
                            new MeleeDamageTypeOverrideComponent(KlaxonDamageTypes.FLINT_AND_STEEELING)
                    )
                    .add(
                            KlaxonDataComponentTypes.DEFAULT_INNATE_ENCHANTMENTS,
                            new DefaultInnateItemEnchantmentsComponent(Map.of(Enchantments.UNBREAKING, 4))
                    )
                    .add(
                            DataComponentTypes.MAX_DAMAGE,
                            KlaxonToolMaterials.STEEL_NUGGET.getDurability()
                    );
        });

        context.modify(Items.SHEARS, builder -> {
            builder.add(
                    KlaxonDataComponentTypes.TOOL_USE_RECIPE_CONFIG,
                    new ToolUseRecipeConfigComponent(KlaxonSoundEvents.ITEM_SHEARS_USAGE)
            );
        });
    }
}
