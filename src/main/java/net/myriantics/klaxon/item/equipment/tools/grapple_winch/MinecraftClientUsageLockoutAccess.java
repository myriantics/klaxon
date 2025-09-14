package net.myriantics.klaxon.item.equipment.tools.grapple_winch;

public interface MinecraftClientUsageLockoutAccess {

    boolean klaxon$isUsageLockoutActive();

    void klaxon$setUsageLockout(boolean lockout);
}
