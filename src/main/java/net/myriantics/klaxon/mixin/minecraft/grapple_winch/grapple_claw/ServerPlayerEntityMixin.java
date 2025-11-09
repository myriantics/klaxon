package net.myriantics.klaxon.mixin.minecraft.grapple_winch.grapple_claw;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawEntity;
import net.myriantics.klaxon.mechanics.grapple_winch.connection.ServerGrappleWinchConnection;
import net.myriantics.klaxon.mechanics.grapple_winch.manager.ServerGrappleWinchConnectionManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    @Shadow
    public abstract ServerWorld getServerWorld();

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(
            method = "damage",
            at = @At(value = "HEAD")
    )
    private void klaxon$conductElectricalDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        ServerGrappleWinchConnectionManager manager = ((ServerGrappleWinchConnectionManager.Access) this.getServerWorld()).klaxon$get();
        @Nullable ServerGrappleWinchConnection connection = manager.fromPlayer((ServerPlayerEntity)(Object)this);
        if (connection != null && connection.getHook() instanceof GrappleClawEntity grappleClaw) {
            grappleClaw.tryConductElectricalDamage(this, source, amount);
        }
    }
}
