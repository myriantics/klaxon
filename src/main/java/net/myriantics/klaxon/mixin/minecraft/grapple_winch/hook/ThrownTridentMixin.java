package net.myriantics.klaxon.mixin.minecraft.grapple_winch.hook;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.mechanics.grapple_winch.CableDetachmentReason;
import net.myriantics.klaxon.mechanics.grapple_winch.GrapplingHook;
import net.myriantics.klaxon.mechanics.grapple_winch.connection.GrappleWinchConnection;
import net.myriantics.klaxon.mechanics.grapple_winch.connection.ServerGrappleWinchConnection;
import net.myriantics.klaxon.mechanics.grapple_winch.manager.GrappleWinchConnectionManager;
import net.myriantics.klaxon.mechanics.grapple_winch.manager.ServerGrappleWinchConnectionManager;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementTriggers;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ThrownTrident.class)
public abstract class ThrownTridentMixin extends AbstractArrow implements GrapplingHook {
    @Shadow
    public abstract void tickDespawn();

    protected ThrownTridentMixin(EntityType<? extends AbstractArrow> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public void klaxon$onConnect(ServerPlayer serverPlayer) {
        this.setOwner(serverPlayer);
    }

    @Override
    public void klaxon$onDisconnect(CableDetachmentReason reason) {

    }

    @Override
    public ItemStack klaxon$getItemStack() {
        return this.getPickupItemStackOrigin();
    }

    @Override
    public boolean klaxon$isAnchored() {
        return this.inGround && !this.isNoPhysics();
    }

    @Override
    public void klaxon$deAnchor(Vec3 deAnchoringDirection) {
        boolean success = false;
        if (this.inGround) {
            BlockHitResult hitResult = this.level().clip(new ClipContext(
                    this.position(),
                    this.position().add(deAnchoringDirection),
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    this
            ));

            // make sure it's either a miss or we've got space to move a significant amount
            if (hitResult.getType().equals(HitResult.Type.MISS) || hitResult.getLocation().distanceTo(this.position()) > 0.1) {
                this.inGround = false;
                success = true;
            }
        }

        if (success) {
            KlaxonAdvancementTriggers.triggerGrappleWinchDeAnchorGrappleClaw((ServerPlayer) this.getOwner());
        }
    }

    @Override
    public ThrownTrident klaxon$asEntity() {
        return (ThrownTrident) (Object) this;
    }

    @Override
    public @Nullable Entity klaxon$getHookedEntity() {
        return null;
    }

    @WrapMethod(
            method = "onHitEntity"
    )
    private void klaxon$attemptFastReloading(EntityHitResult entityHitResult, Operation<Void> original) {
        if (entityHitResult.getEntity() instanceof ServerPlayer serverPlayer) {
            ServerGrappleWinchConnectionManager manager = ServerGrappleWinchConnectionManager.get(serverPlayer.serverLevel());
            @Nullable ServerGrappleWinchConnection connection = manager.fromHook(this);
            if (connection != null && serverPlayer.equals(connection.getPlayer())) {
                if (this.klaxon$tryFastReload(serverPlayer, serverPlayer.getMainHandItem()) || this.klaxon$tryFastReload(serverPlayer, serverPlayer.getOffhandItem())) {
                    return;
                }
            }
        }
        original.call(entityHitResult);
    }

    @WrapOperation(
            method = "tickDespawn",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;tickDespawn()V")
    )
    private void klaxon$preventAgingIfGrappleWinchConnectionPresent(ThrownTrident instance, Operation<Void> original) {
        // check if we have a connection - if we do, return early and don't age.
        if (instance.level() instanceof ServerLevel serverWorld) {
            ServerGrappleWinchConnectionManager manager = ServerGrappleWinchConnectionManager.get(serverWorld);
            if (manager.fromHook(this) != null) {
                return;
            }
        }
        original.call(instance);
    }

    @Definition(id = "dealtDamage", field = "Lnet/minecraft/world/entity/projectile/ThrownTrident;dealtDamage:Z")
    @Expression("this.dealtDamage")
    @ModifyExpressionValue(
            method = "tick",
            at = @At(value = "MIXINEXTRAS:EXPRESSION")
    )
    private boolean klaxon$dontReturnWithLoyaltyIfRetracting(boolean original) {
        GrappleWinchConnectionManager manager = GrappleWinchConnectionManager.get(this.level());
        @Nullable GrappleWinchConnection connection = manager.fromHook(this);
        // this makes it so that loyalty tridents are actually useful as a grappling hook
        // you just have to start retracting right before they land and then release when you want them to be recalled
        return original && (connection == null || !connection.isRetracting());
    }
}
