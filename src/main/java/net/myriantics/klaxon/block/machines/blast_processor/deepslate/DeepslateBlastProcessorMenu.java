package net.myriantics.klaxon.block.machines.blast_processor.deepslate;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.block.machines.blast_processor.AbstractBlastProcessorBlockEntity;
import net.myriantics.klaxon.block.machines.blast_processor.AbstractBlastProcessorMenu;
import net.myriantics.klaxon.networking.s2c.BlastProcessorMenuPowerSyncPacket;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeData;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeInput;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystData;
import net.myriantics.klaxon.registry.misc.KlaxonMenuTypes;
import net.myriantics.klaxon.util.PermissionsHelper;
import net.myriantics.klaxon.util.container.KlaxonBaseContainerMenu;
import net.myriantics.klaxon.util.container.KlaxonClientMenuInitializer;

import java.util.List;

public class DeepslateBlastProcessorMenu extends AbstractBlastProcessorMenu {
    public DeepslateBlastProcessorMenu(int containerId, Inventory playerInventory, BlastProcessorMenuPowerSyncPacket initializer) {
        super(KlaxonMenuTypes.DEEPSLATE_BLAST_PROCESSOR.value(), containerId, playerInventory, initializer);
    }

    public DeepslateBlastProcessorMenu(int containerId, Inventory playerInventory, Container container, ContainerData data, ContainerLevelAccess access) {
        super(KlaxonMenuTypes.DEEPSLATE_BLAST_PROCESSOR.value(), containerId, playerInventory, container, data, access);
    }
}
