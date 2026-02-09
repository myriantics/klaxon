package net.myriantics.klaxon.mixin.minecraft.gerald_sniffer;

import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;

@Mixin(SnifferEntity.class)
public interface SnifferEntityInvoker {
    @Invoker(value = "findSniffingTargetPos")
    Optional<BlockPos> klaxon$invokeFindSniffingTargetPos();
}
