package net.myriantics.klaxon_gametest.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.BlockState;
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

    public void pushButton(BlockPos pos) {
        this.assertBlock(pos, block -> block instanceof ButtonBlock, "Targeted block is not a Button!");
        BlockPos blockPos = this.absolutePos(pos);
        BlockState blockState = this.getLevel().getBlockState(blockPos);
        ButtonBlock buttonBlock = (ButtonBlock)blockState.getBlock();
        buttonBlock.press(blockState, this.getLevel(), blockPos, null);
    }

    public void setBlock(BlockPos pos, Holder<Block> holder) {
        this.setBlock(pos, holder.value());
    }

    public void assertBlockPresent(Holder<Block> block, BlockPos pos) {
        this.assertBlockPresent(block.value(), pos);
    }

    public void assertBlockNotPresent(Holder<Block> holder, BlockPos pos) {
        this.assertBlockNotPresent(holder.value(), pos);
    }

    public void moveTo(Mob mob, BlockPos pos) {
        this.moveTo(mob, pos.getX(), pos.getY(), pos.getZ());
    }

    public BlockHitResult hitResult(BlockPos pos) {
        return this.hitResult(pos, Direction.NORTH);
    }

    public BlockHitResult hitResult(BlockPos pos, Direction side) {
        BlockPos blockPos = this.absolutePos(pos);
        return new BlockHitResult(blockPos.getCenter(), side, blockPos, true);
    }

    public int getAnalogSignal(BlockPos pos) {
        BlockState state = this.getBlockState(pos);
        return state.getAnalogOutputSignal(this.getLevel(), this.absolutePos(pos));
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
