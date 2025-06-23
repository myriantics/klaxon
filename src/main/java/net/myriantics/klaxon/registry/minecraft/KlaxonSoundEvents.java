package net.myriantics.klaxon.registry.minecraft;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.myriantics.klaxon.KlaxonCommon;

public class KlaxonSoundEvents {

    public static final SoundEvent BLOCK_DEEPSLATE_BLAST_PROCESSOR_INSERT = register("block.deepslate_blast_processor.insert", SoundEvents.BLOCK_DEEPSLATE_PLACE);
    public static final SoundEvent ITEM_HAMMER_WALLJUMP_SUCCESS = register("item.hammer.walljump.success", SoundEvents.ENTITY_IRON_GOLEM_HURT);
    public static final SoundEvent ITEM_HAMMER_WALLJUMP_FAIL_HEAVY = register("item.hammer.walljump.fail.heavy", SoundEvents.ENTITY_ITEM_BREAK);
    public static final SoundEvent ITEM_HAMMER_USAGE = register("item.hammer.usage", SoundEvents.BLOCK_ANVIL_LAND);
    public static final SoundEvent ITEM_CABLE_SHEARS_USAGE = register("item.cable_shears.usage", SoundEvents.BLOCK_CHAIN_BREAK);
    public static final SoundEvent ITEM_SHEARS_USAGE = register("item.shears.usage", SoundEvents.ENTITY_SHEEP_SHEAR);

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's SoundEvents!");
    }

    // this doesnt actually register anything in order to prevent a crash when our custom sound files are missing
    private static SoundEvent register(String name, SoundEvent soundEvent) {
        return soundEvent;
        // return Registry.register(Registries.SOUND_EVENT, KlaxonCommon.locate(name), soundEvent);
    }
}
