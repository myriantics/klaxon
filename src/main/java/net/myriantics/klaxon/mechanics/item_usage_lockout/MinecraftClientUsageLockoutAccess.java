package net.myriantics.klaxon.mechanics.item_usage_lockout;

public interface MinecraftClientUsageLockoutAccess {

    boolean klaxon$isUsageLockoutActive();

    void klaxon$setUsageLockout(boolean lockout);
}
