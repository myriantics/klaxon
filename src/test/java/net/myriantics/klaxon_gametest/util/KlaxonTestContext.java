package net.myriantics.klaxon_gametest.util;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.test.GameTestException;
import net.minecraft.test.GameTestState;
import net.minecraft.test.TestContext;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
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

    public KlaxonTestPlayer createFakePlayer(GameMode gameMode) {
        KlaxonTestPlayer testPlayer = new KlaxonTestPlayer(test.getWorld(), this);
        testPlayer.changeGameMode(gameMode);
        testPlayer.setPosition(this.getAbsolute(BlockPos.ORIGIN.toCenterPos()));
        return testPlayer;
    }

    public BlockHitResult hitResult(BlockPos pos) {
        return this.hitResult(pos, Direction.NORTH);
    }

    public BlockHitResult hitResult(BlockPos pos, Direction side) {
        BlockPos blockPos = this.getAbsolutePos(pos);
        return new BlockHitResult(blockPos.toCenterPos(), side, blockPos, true);
    }

    public void expectBoolean(boolean expected, boolean result, String message) {
        if (expected != result) {
            throw new GameTestException(message + " Expected: " + expected + " Got: " + result);
        }
    }

    public void expectInt(int expected, int result, String message) {
        if (expected != result) {
            throw new GameTestException(message + " Expected: " + expected + " Got: " + result);
        }
    }
}
