package net.myriantics.klaxon.mixin.minecraft.precision_dispenser;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.myriantics.klaxon.block.machines.precision_dispenser.PrecisionDispenserBlock;
import net.myriantics.klaxon.mechanics.muffling.MufflableBlock;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntityTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.Optional;

@Mixin(DispenserBlock.class)
public abstract class DispenserBlockMixin extends BaseEntityBlock {
    protected DispenserBlockMixin(Properties properties) {
        super(properties);
    }

    @WrapOperation(
            method = "dispenseFrom",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getBlockEntity(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntityType;)Ljava/util/Optional;")
    )
    private Optional<DispenserBlockEntity> klaxon$allowPrecisionDispenserBlockEntity(ServerLevel instance, BlockPos pos, BlockEntityType<DispenserBlockEntity> blockEntityType, Operation<Optional<DispenserBlockEntity>> original) {
        Optional<DispenserBlockEntity> originalValue = original.call(instance, pos, blockEntityType);
        if (originalValue.isPresent()) {
            return originalValue;
        } else {
            return original.call(instance, pos, KlaxonBlockEntityTypes.PRECISION_DISPENSER.value());
        }
    }

    @WrapOperation(
            method = "dispenseFrom",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;levelEvent(ILnet/minecraft/core/BlockPos;I)V")
    )
    private void klaxon$skipLevelEventIfMuffled(ServerLevel instance, int type, BlockPos pos, int data, Operation<Void> original) {
        if (this instanceof MufflableBlock mufflableBlock && mufflableBlock.hasMuffler(instance, pos)) {
            // bonked
        } else {
            original.call(instance, type, pos, data);
        }
    }

    @WrapOperation(
            method = "dispenseFrom",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;gameEvent(Lnet/minecraft/core/Holder;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/gameevent/GameEvent$Context;)V")
    )
    private void klaxon$skipGameEventIfMuffled(ServerLevel instance, Holder<GameEvent> holder, BlockPos pos, GameEvent.Context context, Operation<Void> original) {
        if (this instanceof MufflableBlock mufflableBlock && mufflableBlock.hasMuffler(instance, pos)) {
            // bonked
        } else {
            original.call(instance, holder, pos, context);
        }
    }

    @WrapOperation(
            method = "neighborChanged",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;above()Lnet/minecraft/core/BlockPos;")
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;hasNeighborSignal(Lnet/minecraft/core/BlockPos;)Z")
    )
    private boolean klaxon$disableQuasiconnectivityOnPrecisionDispenser(Level instance, BlockPos pos, Operation<Boolean> original) {
        return !((Object) this instanceof PrecisionDispenserBlock) && original.call(instance, pos);
    }
}
