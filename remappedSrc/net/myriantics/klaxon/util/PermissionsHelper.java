package net.myriantics.klaxon.util;

import net.minecraft.entity.player.PlayerEntity;

public abstract class PermissionsHelper {

    public static boolean canModifyWorld(PlayerEntity player) {
        return player.getAbilities().allowModifyWorld;
    }
}
