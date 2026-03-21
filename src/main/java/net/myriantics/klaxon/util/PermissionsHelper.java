package net.myriantics.klaxon.util;

import net.minecraft.world.entity.player.Player;

public abstract class PermissionsHelper {

    public static boolean canModifyWorld(Player player) {
        return player.getAbilities().mayBuild;
    }
}
