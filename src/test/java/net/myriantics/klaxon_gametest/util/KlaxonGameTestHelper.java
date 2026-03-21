package net.myriantics.klaxon_gametest.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;

public class KlaxonGameTestHelper extends GameTestHelper {
    private final GameTestInfo test;
    private final ArrayList<KlaxonTestPlayer> players = new ArrayList<>();

    public KlaxonGameTestHelper(GameTestInfo test) {
        super(test);
        this.test = test;
    }

    @Override
    public Player makeMockPlayer(GameType gameType) {
        return createFakePlayer(gameType);
    }

    public KlaxonTestPlayer createFakePlayer(GameType gameMode) {
        KlaxonTestPlayer testPlayer = new KlaxonTestPlayer(test.getLevel(), this);
        testPlayer.setGameMode(gameMode);
        testPlayer.setPos(this.absoluteVec(BlockPos.ZERO.getCenter()));
        this.players.add(testPlayer);
        return testPlayer;
    }

    public BlockHitResult hitResult(BlockPos pos) {
        return this.hitResult(pos, Direction.NORTH);
    }

    public BlockHitResult hitResult(BlockPos pos, Direction side) {
        BlockPos blockPos = this.absolutePos(pos);
        return new BlockHitResult(blockPos.getCenter(), side, blockPos, true);
    }

    public void expectBoolean(boolean expected, boolean result, String message) {
        if (expected != result) {
            throw new GameTestAssertException(message + " Expected: " + expected + " Got: " + result);
        }
    }

    public void expectInt(int expected, int result, String message) {
        if (expected != result) {
            throw new GameTestAssertException(message + " Expected: " + expected + " Got: " + result);
        }
    }

    @Override
    public void succeed() {
        for (KlaxonTestPlayer player : this.players) {
            // player.kill();
        }
        super.succeed();
    }
}
