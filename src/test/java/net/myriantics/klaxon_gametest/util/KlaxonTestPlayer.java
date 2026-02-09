package net.myriantics.klaxon_gametest.util;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameMode;

import java.util.UUID;

public class KlaxonTestPlayer extends FakePlayer {
    protected KlaxonTestPlayer(ServerWorld world, KlaxonTestContext context) {
        super(
                world,
                new GameProfile(DEFAULT_UUID, "KlaxonTestPlayer" + world.getRandom().nextBetween(1, 999))
        );
    }
}
