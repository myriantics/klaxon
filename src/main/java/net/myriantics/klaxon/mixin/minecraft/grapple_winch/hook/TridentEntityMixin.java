package net.myriantics.klaxon.mixin.minecraft.grapple_winch.hook;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawBlockDestructionHelper;
import net.myriantics.klaxon.mechanics.grapple_winch.CableDetachmentReason;
import net.myriantics.klaxon.mechanics.grapple_winch.GrapplingHook;
import net.myriantics.klaxon.mechanics.grapple_winch.connection.ServerGrappleWinchConnection;
import net.myriantics.klaxon.mechanics.grapple_winch.manager.ServerGrappleWinchConnectionManager;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementTriggers;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(TridentEntity.class)
public abstract class TridentEntityMixin extends PersistentProjectileEntity implements GrapplingHook {
    @Shadow
    public abstract void age();

    protected TridentEntityMixin(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void klaxon$onConnect(ServerPlayerEntity serverPlayer) {
        this.setOwner(serverPlayer);
    }

    @Override
    public void klaxon$onDisconnect(CableDetachmentReason reason) {

    }

    @Override
    public ItemStack klaxon$getItemStack() {
        return this.getItemStack();
    }

    @Override
    public boolean klaxon$isAnchored() {
        return this.inGround;
    }

    @Override
    public void klaxon$deAnchor(Vec3d deAnchoringDirection) {
        boolean success = false;
        if (this.inGround) {
            BlockHitResult hitResult = this.getWorld().raycast(new RaycastContext(
                    this.getPos(),
                    this.getPos().add(deAnchoringDirection),
                    RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE,
                    this
            ));

            // make sure it's either a miss or we've got space to move a significant amount
            if (hitResult.getType().equals(HitResult.Type.MISS) || hitResult.getPos().distanceTo(this.getPos()) > 0.1) {
                this.inGround = false;
                success = true;
            }
        }

        if (success) {
            KlaxonAdvancementTriggers.triggerGrappleWinchDeAnchorGrappleClaw((ServerPlayerEntity) this.getOwner());
        }
    }

    @Override
    public TridentEntity klaxon$asEntity() {
        return (TridentEntity) (Object) this;
    }

    @Override
    public @Nullable Entity klaxon$getHookedEntity() {
        return null;
    }

    @WrapMethod(
            method = "onEntityHit"
    )
    private void klaxon$attemptFastReloading(EntityHitResult entityHitResult, Operation<Void> original) {
        if (entityHitResult.getEntity() instanceof ServerPlayerEntity serverPlayer) {
            ServerGrappleWinchConnectionManager manager = ((ServerGrappleWinchConnectionManager.Access) serverPlayer.getServerWorld()).klaxon$get();
            @Nullable ServerGrappleWinchConnection connection = manager.fromHook(this);
            if (connection != null && serverPlayer.equals(connection.getPlayer())) {
                if (this.klaxon$tryFastReload(serverPlayer, serverPlayer.getMainHandStack()) || this.klaxon$tryFastReload(serverPlayer, serverPlayer.getOffHandStack())) {
                    return;
                }
            }
        }
        original.call(entityHitResult);
    }

    @WrapOperation(
            method = "age",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;age()V")
    )
    private void klaxon$preventAgingIfGrappleWinchConnectionPresent(TridentEntity instance, Operation<Void> original) {
        // check if we have a connection - if we do, return early and don't age.
        if (instance.getWorld() instanceof ServerGrappleWinchConnectionManager.Access access) {
            ServerGrappleWinchConnectionManager manager = access.klaxon$get();
            if (manager.fromHook(this) != null) {
                return;
            }
        }
        original.call(instance);
    }
}
