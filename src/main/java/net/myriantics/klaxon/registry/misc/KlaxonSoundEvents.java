package net.myriantics.klaxon.registry.misc;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonSoundEvents {

    public static final SoundEvent BLOCK_DEEPSLATE_BLAST_PROCESSOR_INSERT = register("block.deepslate_blast_processor.insert", SoundEvents.DEEPSLATE_PLACE);
    public static final SoundEvent ITEM_HAMMER_WALLJUMP_SUCCESS = register("item.hammer.walljump.success", SoundEvents.IRON_GOLEM_HURT);
    public static final SoundEvent ITEM_HAMMER_WALLJUMP_FAIL_HEAVY = register("item.hammer.walljump.fail.heavy", SoundEvents.ITEM_BREAK);
    public static final SoundEvent ITEM_HAMMER_USAGE = register("item.hammer.usage", SoundEvents.ANVIL_LAND);
    public static final SoundEvent ITEM_CABLE_SHEARS_USAGE = register("item.cable_shears.usage", SoundEvents.CHAIN_BREAK);
    public static final SoundEvent ITEM_SHEARS_USAGE = register("item.shears.usage", SoundEvents.SHEEP_SHEAR);
    // grapple winch
    public static final SoundEvent ITEM_GRAPPLE_WINCH_LAUNCH = register("item.grapple_winch.launch", SoundEvents.TRIDENT_THROW.value());
    public static final SoundEvent ITEM_GRAPPLE_WINCH_EXTRUDING = register("item.grapple_winch.extrude", SoundEvents.MINECART_INSIDE_UNDERWATER);
    public static final SoundEvent ITEM_GRAPPLE_WINCH_RETRACTING = register("item.grapple_winch.retracting", SoundEvents.MINECART_RIDING);
    public static final SoundEvent ITEM_GRAPPLE_WINCH_LOAD = register("item.grapple_winch.load", SoundEvents.PISTON_CONTRACT);
    public static final SoundEvent ITEM_GRAPPLE_WINCH_UNLOAD = register("item.grapple_winch.load", SoundEvents.PISTON_EXTEND);
    public static final SoundEvent ITEM_GRAPPLE_WINCH_FAST_LOAD = register("item.grapple_winch.fast_load", SoundEvents.PISTON_CONTRACT);
    // grapple claw
    public static final SoundEvent ENTITY_GRAPPLE_CLAW_ANCHOR = register("entity.grapple_claw.anchor", SoundEvents.TRIDENT_THUNDER.value());
    public static final SoundEvent ENTITY_GRAPPLE_CLAW_DAMAGE = register("entity.grapple_claw.damage", SoundEvents.METAL_HIT);
    public static final SoundEvent ENTITY_GRAPPLE_CLAW_DESTROY = register("entity.grapple_claw.destroy", SoundEvents.ITEM_BREAK);
    public static final SoundEvent ENTITY_GRAPPLE_CLAW_DETACH = register("entity.grapple_claw.detach", SoundEvents.ITEM_BREAK);
    public static final SoundEvent ENTITY_GRAPPLE_CLAW_HOOK = register("entity.grapple_claw.hook", SoundEvents.CHAIN_BREAK);
    public static final SoundEvent ENTITY_GRAPPLE_CLAW_REBOUND_AT_LIMIT = register("entity.grapple_claw.rebound_at_limit", SoundEvents.TRIDENT_RETURN);
    // nether reaction
    public static final Holder<SoundEvent> NETHER_REACTION_EXPLOSION = register("block.nether_reactor_core.explosion", SoundEvents.RESPAWN_ANCHOR_DEPLETE);
    // crested steel helmet
    public static final SoundEvent SNIFFER_DIG_METAL = register("entity.sniffer.dig_metal", SoundEvents.ARMOR_EQUIP_NETHERITE).value();
    // muffler
    public static final SoundEvent MUFFLER_APPLY_SUCCESS = register("mechanics.muffler.apply.success", SoundEvents.SHROOMLIGHT_PLACE);
    public static final SoundEvent MUFFLER_REMOVE_SUCCESS = register("mechainics.muffler.remove.success", SoundEvents.SHEEP_SHEAR);
    public static final SoundEvent MUFFLER_APPLY_FAIL = register("mechanics.muffler.apply.fail", SoundEvents.DECORATED_POT_INSERT_FAIL);
    public static final SoundEvent MUFFLER_REMOVE_FAIL = register("mechanics.muffler.remove.fail", SoundEvents.ITEM_BREAK);

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's SoundEvents!");
    }

    // this doesnt actually register anything in order to prevent a crash when our custom sound files are missing
    private static SoundEvent register(String name, SoundEvent soundEvent) {
        return soundEvent;
        // return Registry.register(Registries.SOUND_EVENT, KlaxonCommon.locate(name), soundEvent);
    }

    private static Holder<SoundEvent> register(String name, Holder<SoundEvent> soundEvent) {
        return soundEvent;
        // return Registry.register(Registries.SOUND_EVENT, KlaxonCommon.locate(name), soundEvent);
    }
}
