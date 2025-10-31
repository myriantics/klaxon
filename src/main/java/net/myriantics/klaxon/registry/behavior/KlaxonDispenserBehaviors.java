package net.myriantics.klaxon.registry.behavior;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.block.dispenser.ShearsDispenserBehavior;
import net.minecraft.item.ItemConvertible;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.recipe.world_item_application.WorldItemApplicationDispenserBehavior;
import net.myriantics.klaxon.mechanics.wrench.WrenchDispenserBehavior;
import net.myriantics.klaxon.registry.item.KlaxonItems;

public abstract class KlaxonDispenserBehaviors {

    // used in DispenserBlockMixin
    public static WorldItemApplicationDispenserBehavior MANUAL_ITEM_APPLICATION_BEHAVIOR = new WorldItemApplicationDispenserBehavior();

    public static DispenserBehavior CABLE_SHEARS_BEHAVIOR = register(KlaxonItems.STEEL_CABLE_SHEARS, new ShearsDispenserBehavior());
    public static DispenserBehavior WRENCH_BEHAVIOR = register(KlaxonItems.STEEL_WRENCH, new WrenchDispenserBehavior());
    public static DispenserBehavior STEEL_GRAPPLE_CLAW_BEHAVIOR = register(KlaxonItems.STEEL_GRAPPLE_CLAW, new ProjectileDispenserBehavior(KlaxonItems.STEEL_GRAPPLE_CLAW));

    private static DispenserBehavior register(ItemConvertible item, DispenserBehavior behavior) {
        DispenserBlock.registerBehavior(item, behavior);
        return behavior;
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Dispenser Behaviors!");
    }
}
