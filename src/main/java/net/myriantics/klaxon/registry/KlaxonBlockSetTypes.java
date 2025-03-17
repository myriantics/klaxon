package net.myriantics.klaxon.registry;

import net.minecraft.block.BlockSetType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.myriantics.klaxon.mixin.BlockSetTypeInvoker;

public class KlaxonBlockSetTypes {
    public static final BlockSetType STEEL = register(
            new BlockSetType(
                    "iron",
                    false,
                    BlockSoundGroup.METAL,
                    SoundEvents.BLOCK_IRON_DOOR_CLOSE,
                    SoundEvents.BLOCK_IRON_DOOR_OPEN,
                    SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE,
                    SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN,
                    SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF,
                    SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON,
                    SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF,
                    SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON
            )
    );

    public static final BlockSetType CRUDE_STEEL = register(
            new BlockSetType(
                    "crude_steel",
                    false,
                    BlockSoundGroup.METAL,
                    SoundEvents.BLOCK_IRON_DOOR_CLOSE,
                    SoundEvents.BLOCK_IRON_DOOR_OPEN,
                    SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE,
                    SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN,
                    SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF,
                    SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON,
                    SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF,
                    SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON
            )
    );

    public static BlockSetType register(BlockSetType type) {
        return BlockSetTypeInvoker.klaxon$register(type);
    }
}
