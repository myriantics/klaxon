package net.myriantics.klaxon.mechanics.grapple_winch.hooking;

import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class HookingGrappleClawContainer {
    private GrappleClawEntity grappleClaw;

    public HookingGrappleClawContainer() {
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
