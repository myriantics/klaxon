package net.myriantics.klaxon.api;

import net.minecraft.entity.player.PlayerEntity;

public abstract class PermissionsHelper {

    public static boolean canModifyWorld(PlayerEntity player) {
        return player.getAbilities().allowModifyWorld;
    }
}
