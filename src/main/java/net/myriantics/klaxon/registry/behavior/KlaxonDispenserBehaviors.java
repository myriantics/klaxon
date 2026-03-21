package net.myriantics.klaxon.registry.behavior;

import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.core.dispenser.ShearsDispenseItemBehavior;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.DispenserBlock;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mechanics.wrench.WrenchDispenserBehavior;
import net.myriantics.klaxon.recipe.world_item_application.WorldItemApplicationDispenserBehavior;
import net.myriantics.klaxon.registry.item.KlaxonItems;

public abstract class KlaxonDispenserBehaviors {

    // used in DispenserBlockMixin
    public static WorldItemApplicationDispenserBehavior MANUAL_ITEM_APPLICATION_BEHAVIOR = new WorldItemApplicationDispenserBehavior();

    public static DispenseItemBehavior CABLE_SHEARS_BEHAVIOR = register(KlaxonItems.STEEL_CABLE_SHEARS, new ShearsDispenseItemBehavior());
    public static DispenseItemBehavior WRENCH_BEHAVIOR = register(KlaxonItems.STEEL_WRENCH, new WrenchDispenserBehavior());
    public static DispenseItemBehavior STEEL_GRAPPLE_CLAW_BEHAVIOR = register(KlaxonItems.STEEL_GRAPPLE_CLAW, new ProjectileDispenseBehavior(KlaxonItems.STEEL_GRAPPLE_CLAW));
    public static DispenseItemBehavior REINFORCED_FLINT_AND_STEEL_BEHAVIOR = register(KlaxonItems.REINFORCED_FLINT_AND_STEEL, DispenserBlock.DISPENSER_REGISTRY.get(Items.FLINT_AND_STEEL));

    private static DispenseItemBehavior register(ItemLike item, DispenseItemBehavior behavior) {
        DispenserBlock.registerBehavior(item, behavior);
        return behavior;
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Dispenser Behaviors!");
    }
}
