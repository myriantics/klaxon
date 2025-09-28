package net.myriantics.klaxon.registry.misc;

import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonSoundEvents {

    public static final SoundEvent BLOCK_DEEPSLATE_BLAST_PROCESSOR_INSERT = register("block.deepslate_blast_processor.insert", SoundEvents.BLOCK_DEEPSLATE_PLACE);
    public static final SoundEvent ITEM_HAMMER_WALLJUMP_SUCCESS = register("item.hammer.walljump.success", SoundEvents.ENTITY_IRON_GOLEM_HURT);
    public static final SoundEvent ITEM_HAMMER_WALLJUMP_FAIL_HEAVY = register("item.hammer.walljump.fail.heavy", SoundEvents.ENTITY_ITEM_BREAK);
    public static final SoundEvent ITEM_HAMMER_USAGE = register("item.hammer.usage", SoundEvents.BLOCK_ANVIL_LAND);
    public static final SoundEvent ITEM_CABLE_SHEARS_USAGE = register("item.cable_shears.usage", SoundEvents.BLOCK_CHAIN_BREAK);
    public static final SoundEvent ITEM_SHEARS_USAGE = register("item.shears.usage", SoundEvents.ENTITY_SHEEP_SHEAR);
    // grapple winch
    public static final SoundEvent ITEM_GRAPPLE_WINCH_LAUNCH = register("item.grapple_winch.launch", SoundEvents.ITEM_TRIDENT_THROW.value());
    public static final SoundEvent ITEM_GRAPPLE_WINCH_EXTRUDING = register("item.grapple_winch.extrude", SoundEvents.ENTITY_MINECART_INSIDE_UNDERWATER);
    public static final SoundEvent ITEM_GRAPPLE_WINCH_RETRACTING = register("item.grapple_winch.retracting", SoundEvents.ENTITY_MINECART_RIDING);
    public static final SoundEvent ITEM_GRAPPLE_WINCH_LOAD = register("item.grapple_winch.load", SoundEvents.BLOCK_COPPER_BULB_PLACE);
    public static final SoundEvent ITEM_GRAPPLE_WINCH_UNLOAD = register("item.grapple_winch.load", SoundEvents.BLOCK_COPPER_BULB_BREAK);
    public static final SoundEvent ITEM_GRAPPLE_WINCH_FAST_LOAD = register("item.grapple_winch.fast_load", SoundEvents.BLOCK_COPPER_BREAK);
    // grapple claw
    public static final SoundEvent ENTITY_GRAPPLE_CLAW_ANCHOR = register("entity.grapple_claw.anchor", SoundEvents.ITEM_TRIDENT_THUNDER.value());
    public static final SoundEvent ENTITY_GRAPPLE_CLAW_DAMAGE = register("entity.grapple_claw.damage", SoundEvents.BLOCK_CHAIN_BREAK);
    public static final SoundEvent ENTITY_GRAPPLE_CLAW_DESTROY = register("entity.grapple_claw.damage", SoundEvents.ENTITY_ITEM_BREAK);

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's SoundEvents!");
    }

    // this doesnt actually register anything in order to prevent a crash when our custom sound files are missing
    private static SoundEvent register(String name, SoundEvent soundEvent) {
        return soundEvent;
        // return Registry.register(Registries.SOUND_EVENT, KlaxonCommon.locate(name), soundEvent);
    }
}
