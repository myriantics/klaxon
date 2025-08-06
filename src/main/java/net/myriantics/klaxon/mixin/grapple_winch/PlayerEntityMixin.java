package net.myriantics.klaxon.mixin.grapple_winch;

import net.minecraft.entity.player.PlayerEntity;
import net.myriantics.klaxon.entity.GrappleClawEntity;
import net.myriantics.klaxon.util.PlayerEntityGrappleAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements PlayerEntityGrappleAccess {

    @Unique
    private GrappleClawEntity klaxon$grappleClaw = null;

    @Override
    public GrappleClawEntity klaxon$getGrappleClaw() {
        return klaxon$grappleClaw;
    }

    @Override
    public void klaxon$setGrappleClaw(GrappleClawEntity grappleClaw) {
        this.klaxon$grappleClaw = grappleClaw;
    }
}
