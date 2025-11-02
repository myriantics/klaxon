package net.myriantics.klaxon.mechanics.grapple_winch;

import net.minecraft.entity.Entity;
import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class AttachedGrappleClawContainer {
    private GrappleClawEntity grappleClaw;

    public AttachedGrappleClawContainer() {
    }

    public void clear() {
        this.grappleClaw = null;
    }

    public boolean isPresent() {
        return this.grappleClaw != null;
    }

    public void setGrappleClaw(@NotNull GrappleClawEntity grappleClaw) {
        this.grappleClaw = grappleClaw;
    }

    public Optional<GrappleClawEntity> getOptionalGrappleClaw() {
        return Optional.ofNullable(this.grappleClaw);
    }
}
