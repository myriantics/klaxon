package net.myriantics.klaxon.registry.minecraft;

import net.fabricmc.fabric.api.object.builder.v1.block.type.BlockSetTypeBuilder;
import net.minecraft.block.BlockSetType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.myriantics.klaxon.KlaxonCommon;

public class KlaxonBlockSetTypes {
    public static final BlockSetType STEEL = register("steel",
            new BlockSetTypeBuilder()
                    .openableByHand(false)
                    .openableByWindCharge(false)
                    .buttonActivatedByArrows(false)
                    .pressurePlateActivationRule(BlockSetType.ActivationRule.MOBS)
                    .soundGroup(BlockSoundGroup.METAL)
                    .doorCloseSound(SoundEvents.BLOCK_IRON_DOOR_CLOSE)
                    .doorOpenSound(SoundEvents.BLOCK_IRON_DOOR_OPEN)
                    .trapdoorCloseSound(SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE)
                    .trapdoorOpenSound(SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN)
                    .pressurePlateClickOffSound(SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF)
                    .pressurePlateClickOnSound(SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON)
                    .buttonClickOffSound(SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF)
                    .buttonClickOnSound(SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON)
    );

    public static final BlockSetType CRUDE_STEEL = register("crude_steel",
            new BlockSetTypeBuilder()
                    .openableByHand(true)
                    .openableByWindCharge(true)
                    .buttonActivatedByArrows(false)
                    .pressurePlateActivationRule(BlockSetType.ActivationRule.EVERYTHING)
                    .soundGroup(BlockSoundGroup.METAL)
                    .doorCloseSound(SoundEvents.BLOCK_IRON_DOOR_CLOSE)
                    .doorOpenSound(SoundEvents.BLOCK_IRON_DOOR_OPEN)
                    .trapdoorCloseSound(SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE)
                    .trapdoorOpenSound(SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN)
                    .pressurePlateClickOffSound(SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF)
                    .pressurePlateClickOnSound(SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON)
                    .buttonClickOffSound(SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF)
                    .buttonClickOnSound(SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON)
    );

    public static final BlockSetType HALLNOX = register("hallnox",
            new BlockSetTypeBuilder()
                    .openableByHand(true)
                    .openableByWindCharge(true)
                    .buttonActivatedByArrows(true)
                    .pressurePlateActivationRule(BlockSetType.ActivationRule.EVERYTHING)
                    .soundGroup(BlockSoundGroup.NETHER_WOOD)
                    .doorOpenSound(SoundEvents.BLOCK_NETHER_WOOD_DOOR_OPEN)
                    .doorCloseSound(SoundEvents.BLOCK_NETHER_WOOD_DOOR_CLOSE)
                    .trapdoorOpenSound(SoundEvents.BLOCK_NETHER_WOOD_TRAPDOOR_OPEN)
                    .trapdoorCloseSound(SoundEvents.BLOCK_NETHER_WOOD_TRAPDOOR_CLOSE)
                    .pressurePlateClickOnSound(SoundEvents.BLOCK_NETHER_WOOD_PRESSURE_PLATE_CLICK_ON)
                    .pressurePlateClickOffSound(SoundEvents.BLOCK_NETHER_WOOD_PRESSURE_PLATE_CLICK_OFF)
                    .buttonClickOnSound(SoundEvents.BLOCK_NETHER_WOOD_BUTTON_CLICK_ON)
                    .buttonClickOffSound(SoundEvents.BLOCK_NETHER_WOOD_BUTTON_CLICK_OFF)
    );

    public static BlockSetType register(String name, BlockSetTypeBuilder builder) {
        return builder.register(KlaxonCommon.locate(name));
    }
}
