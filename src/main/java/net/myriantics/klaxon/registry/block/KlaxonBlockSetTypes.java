package net.myriantics.klaxon.registry.block;

import net.fabricmc.fabric.api.object.builder.v1.block.type.BlockSetTypeBuilder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonBlockSetTypes {
    public static final BlockSetType STEEL = register("steel",
            new BlockSetTypeBuilder()
                    .openableByHand(false)
                    .openableByWindCharge(false)
                    .buttonActivatedByArrows(false)
                    .pressurePlateActivationRule(BlockSetType.PressurePlateSensitivity.MOBS)
                    .soundGroup(SoundType.METAL)
                    .doorCloseSound(SoundEvents.IRON_DOOR_CLOSE)
                    .doorOpenSound(SoundEvents.IRON_DOOR_OPEN)
                    .trapdoorCloseSound(SoundEvents.IRON_TRAPDOOR_CLOSE)
                    .trapdoorOpenSound(SoundEvents.IRON_TRAPDOOR_OPEN)
                    .pressurePlateClickOffSound(SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF)
                    .pressurePlateClickOnSound(SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON)
                    .buttonClickOffSound(SoundEvents.STONE_BUTTON_CLICK_OFF)
                    .buttonClickOnSound(SoundEvents.STONE_BUTTON_CLICK_ON)
    );

    public static final BlockSetType CRUDE_STEEL = register("crude_steel",
            new BlockSetTypeBuilder()
                    .openableByHand(true)
                    .openableByWindCharge(true)
                    .buttonActivatedByArrows(false)
                    .pressurePlateActivationRule(BlockSetType.PressurePlateSensitivity.EVERYTHING)
                    .soundGroup(SoundType.METAL)
                    .doorCloseSound(SoundEvents.IRON_DOOR_CLOSE)
                    .doorOpenSound(SoundEvents.IRON_DOOR_OPEN)
                    .trapdoorCloseSound(SoundEvents.IRON_TRAPDOOR_CLOSE)
                    .trapdoorOpenSound(SoundEvents.IRON_TRAPDOOR_OPEN)
                    .pressurePlateClickOffSound(SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF)
                    .pressurePlateClickOnSound(SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON)
                    .buttonClickOffSound(SoundEvents.STONE_BUTTON_CLICK_OFF)
                    .buttonClickOnSound(SoundEvents.STONE_BUTTON_CLICK_ON)
    );

    public static final BlockSetType HALLNOX = register("hallnox",
            new BlockSetTypeBuilder()
                    .openableByHand(true)
                    .openableByWindCharge(true)
                    .buttonActivatedByArrows(true)
                    .pressurePlateActivationRule(BlockSetType.PressurePlateSensitivity.EVERYTHING)
                    .soundGroup(SoundType.NETHER_WOOD)
                    .doorOpenSound(SoundEvents.NETHER_WOOD_DOOR_OPEN)
                    .doorCloseSound(SoundEvents.NETHER_WOOD_DOOR_CLOSE)
                    .trapdoorOpenSound(SoundEvents.NETHER_WOOD_TRAPDOOR_OPEN)
                    .trapdoorCloseSound(SoundEvents.NETHER_WOOD_TRAPDOOR_CLOSE)
                    .pressurePlateClickOnSound(SoundEvents.NETHER_WOOD_PRESSURE_PLATE_CLICK_ON)
                    .pressurePlateClickOffSound(SoundEvents.NETHER_WOOD_PRESSURE_PLATE_CLICK_OFF)
                    .buttonClickOnSound(SoundEvents.NETHER_WOOD_BUTTON_CLICK_ON)
                    .buttonClickOffSound(SoundEvents.NETHER_WOOD_BUTTON_CLICK_OFF)
    );

    public static BlockSetType register(String name, BlockSetTypeBuilder builder) {
        return builder.register(KlaxonCommon.locate(name));
    }
}
