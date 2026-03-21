package net.myriantics.klaxon.mixin.minecraft.gerald_sniffer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;

@Mixin(Sniffer.class)
public interface SnifferInvoker {
    @Invoker(value = "calculateDigPosition")
    Optional<BlockPos> klaxon$invokeFindSniffingTargetPos();
}
