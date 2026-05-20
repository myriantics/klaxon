package net.myriantics.klaxon_gametest.util;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.server.level.ServerLevel;

public class KlaxonTestPlayer extends FakePlayer {
    protected KlaxonTestPlayer(ServerLevel level, KlaxonGameTestHelper context) {
        super(
                level,
                new GameProfile(DEFAULT_UUID, "KlaxonTestPlayer" + level.getRandom().nextInt(1, 999))
        );
    }
}
