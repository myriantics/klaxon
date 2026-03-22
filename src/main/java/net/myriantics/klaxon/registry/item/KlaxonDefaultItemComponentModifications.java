package net.myriantics.klaxon.registry.item;

import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.world.item.Items;
import net.myriantics.klaxon.component.configuration.MeleeDamageTypeOverrideComponent;
import net.myriantics.klaxon.component.configuration.ToolUseRecipeConfigComponent;
import net.myriantics.klaxon.registry.dynamic.KlaxonDamageTypes;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;

public abstract class KlaxonDefaultItemComponentModifications {
    public static void modify(DefaultItemComponentEvents.ModifyContext context) {

        // make flint and steel repairable with steel nuggets
        context.modify(Items.FLINT_AND_STEEL, builder -> {
            builder.set(
                            KlaxonDataComponentTypes.MELEE_DAMAGE_TYPE_OVERRIDE.value(),
                            new MeleeDamageTypeOverrideComponent(KlaxonDamageTypes.FLINT_AND_STEEELING)
                    );
        });

        context.modify(Items.SHEARS, builder -> {
            builder.set(
                    KlaxonDataComponentTypes.TOOL_USE_RECIPE_CONFIG.value(),
                    new ToolUseRecipeConfigComponent(KlaxonSoundEvents.ITEM_SHEARS_USAGE)
            );
        });
    }
}
