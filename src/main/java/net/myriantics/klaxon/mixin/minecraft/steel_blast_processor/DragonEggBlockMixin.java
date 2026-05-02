package net.myriantics.klaxon.mixin.minecraft.steel_blast_processor;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DragonEggBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.block.machines.blast_processor.steel.SteelBlastProcessorExhaustHandler;
import net.myriantics.klaxon.networking.KlaxonServerPlayNetworkHandler;
import net.myriantics.klaxon.registry.misc.KlaxonWorldEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(DragonEggBlock.class)
public abstract class DragonEggBlockMixin implements SteelBlastProcessorExhaustHandler {

    @Shadow
    protected abstract void teleport(BlockState state, Level level, BlockPos pos);

    @Unique
    @Override
    public boolean klaxon$allowCustomExhaustHandling(ServerLevel level, BlockPos pos, BlockState state) {
        return true;
    }

    @Unique
    @Override
    public boolean klaxon$handleExhaust(ServerLevel level, BlockPos pos, BlockState state) {
        this.teleport(state, level, pos);
        KlaxonServerPlayNetworkHandler.syncWorldEvent(level, pos, KlaxonWorldEvents.DRAGON_EGG_PARTICLES);
        return true;
    }
}
