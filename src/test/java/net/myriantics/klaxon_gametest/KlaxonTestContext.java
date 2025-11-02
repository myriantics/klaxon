package net.myriantics.klaxon_gametest;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.impl.event.interaction.FakePlayerNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTestState;
import net.minecraft.test.TestContext;
import net.minecraft.world.GameMode;

import java.util.UUID;

public class KlaxonTestContext extends TestContext {
    private final GameTestState test;

    public KlaxonTestContext(GameTestState test) {
        super(test);
        this.test = test;
    }

    @Override
    public PlayerEntity createMockPlayer(GameMode gameMode) {
        return createFakePlayer(gameMode);
    }

    public FakePlayer createFakePlayer(GameMode gameMode) {
        return new FakePlayer(
                KlaxonTestContext.this.test.getWorld(),
                new GameProfile(UUID.randomUUID(), "KlaxonTestPlayer" + KlaxonTestContext.this.test.getWorld().random.nextBetween(1, 999))
        ) {
            @Override
            public boolean isSpectator() {
                return gameMode == GameMode.SPECTATOR;
            }

            @Override
            public boolean isCreative() {
                return gameMode.isCreative();
            }

            @Override
            public boolean isMainPlayer() {
                return true;
            }
        };
    }
}
