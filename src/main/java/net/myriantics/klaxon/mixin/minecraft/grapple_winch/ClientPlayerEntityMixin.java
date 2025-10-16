package net.myriantics.klaxon.mixin.minecraft.grapple_winch;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.util.ClientPlayerTickable;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.stat.StatHandler;
import net.minecraft.util.math.Vec3d;
import net.myriantics.klaxon.client.GrappleWinchMovementHandler;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.PlayerEntityGrappleAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Shadow
    @Final
    protected MinecraftClient client;

    @Shadow
    @Final
    private List<ClientPlayerTickable> tickables;

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void klaxon$attachTickables(MinecraftClient client, ClientWorld world, ClientPlayNetworkHandler networkHandler, StatHandler stats, ClientRecipeBook recipeBook, boolean lastSneaking, boolean lastSprinting, CallbackInfo ci) {
        this.tickables.add(new GrappleWinchMovementHandler((ClientPlayerEntity) (Object) this));
    }

    @Inject(
            method = "sendMovementPackets",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V", ordinal = 0)
    )
    public void klaxon$nullifyVelocityIfCrouchingInAirWithActiveNonRetractingGrappleWinchConnection(CallbackInfo ci) {
        PlayerEntityGrappleAccess access = (PlayerEntityGrappleAccess) this;
        if (!isOnGround() && access.klaxon$hasActiveConnection() && !access.klaxon$isRetracting()) {
            this.setVelocity(Vec3d.ZERO);
        }
    }
}
