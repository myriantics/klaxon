package net.myriantics.klaxon.block.machines.blast_processor.steel;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.block.machines.blast_processor.AbstractBlastProcessorBlock;
import net.myriantics.klaxon.mechanics.muffling.MufflableBlock;
import net.myriantics.klaxon.networking.KlaxonServerPlayNetworkHandler;
import net.myriantics.klaxon.networking.s2c.SteelBlastProcessorExhaustLaunchPacket;
import net.myriantics.klaxon.recipe.explosive_catalyst.ExplosiveCatalystData;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntityTypes;
import net.myriantics.klaxon.registry.block.KlaxonBlockStateProperties;
import net.myriantics.klaxon.registry.dynamic.KlaxonDamageTypes;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class SteelBlastProcessorBlock extends AbstractBlastProcessorBlock implements MufflableBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty MUFFLED = KlaxonBlockStateProperties.MUFFLED;

    public SteelBlastProcessorBlock(Properties properties) {
        super(properties);

        registerDefaultState(defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(MUFFLED, false)
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(SteelBlastProcessorBlock::new);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SteelBlastProcessorBlockEntity(KlaxonBlockEntityTypes.STEEL_BLAST_PROCESSOR.value(), pos, state);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock()) && !level.isClientSide()) {
            if (level.getBlockEntity(pos) instanceof SteelBlastProcessorBlockEntity blastProcessor) {
                Containers.dropItemStack(
                        level,
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        blastProcessor.getMuffler()
                );
            }
        }
        super.onRemove(state, level, pos, newState, moved);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, MUFFLED);
    }

    @Override
    protected boolean isRecievingPower(Level level, BlockPos pos) {
        return level.hasNeighborSignal(pos);
    }

    @Override
    protected int getTriggerDuration() {
        return 4;
    }

    public boolean isFieryExhaust(BlockState state) {
        if (state.is(BlockTags.FIRE)) {
            return true;
        }

        if (state.is(KlaxonBlockTags.STEEL_BLAST_PROCESSOR_FIRE_HOLDERS) && state.hasProperty(CampfireBlock.LIT) && state.getValue(CampfireBlock.LIT)) {
            return true;
        }

        return false;
    }

    public boolean handleOverload(Level level, BlockPos pos, SteelBlastProcessorBlockEntity blastProcessor, ExplosiveCatalystData catalystData) {
        BlockPos abovePos = pos.above();
        BlockState aboveState = level.getBlockState(abovePos);

        if (this.isFieryExhaust(aboveState) || !this.canExhaustReplaceState(level, abovePos, aboveState)) {
            return false;
        } else {
            if (blastProcessor.getMuffler().isEmpty()) {
                RandomSource random = level.getRandom();
                level.playSound(
                        null,
                        pos,
                        KlaxonSoundEvents.BLOCK_STEEL_BLAST_PROCESSOR_IGNITE,
                        SoundSource.BLOCKS,
                        0.3f + (0.5f * random.nextFloat()),
                        0.3f + (0.4f * random.nextFloat())
                );
            }

            if (aboveState.is(KlaxonBlockTags.STEEL_BLAST_PROCESSOR_FIRE_HOLDERS) && aboveState.hasProperty(BlockStateProperties.LIT)) {
                level.setBlock(abovePos, aboveState.setValue(BlockStateProperties.LIT, true), 11);
            } else {
                level.setBlockAndUpdate(abovePos, Blocks.FIRE.defaultBlockState());
            }

            if (!aboveState.isCollisionShapeFullBlock(level, abovePos)) {
                List<Entity> caughtInExhaustBlast = level.getEntities(EntityTypeTest.forClass(Entity.class), new AABB(abovePos), entity -> !entity.isInvulnerable());

                float damage = (float) (catalystData.explosionPower() * 2);
                if (catalystData.producesFire()) {
                    damage++;
                }

                // launched up one block for each tick of damage
                Vec3 launchVelocity = new Vec3(0, damage/20, 0);

                for (Entity entity : caughtInExhaustBlast) {
                    if (!entity.fireImmune() && !(entity instanceof LivingEntity livingEntity && livingEntity.hasEffect(MobEffects.FIRE_RESISTANCE))) {
                        entity.hurt(this.createDamageSource(level), damage);
                    }
                    if (entity instanceof ServerPlayer serverPlayer) {
                        KlaxonServerPlayNetworkHandler.send(serverPlayer, new SteelBlastProcessorExhaustLaunchPacket(launchVelocity.toVector3f()));
                    } else {
                        entity.addDeltaMovement(launchVelocity);
                    }
                }
            }

            return true;
        }
    }



    public DamageSource createDamageSource(Level level) {
        return level.damageSources().source(
                KlaxonDamageTypes.FORCEFUL_EXHAUST,
                null,
                null
        );
    }

    public void updateMuffler(Level level, BlockPos pos, SteelBlastProcessorBlockEntity blastProcessor) {
        BlockState original = level.getBlockState(pos);
        BlockState newState = original.setValue(MUFFLED, !blastProcessor.getMuffler().isEmpty());

        if (!original.equals(newState)) {
            level.setBlockAndUpdate(pos, newState);
        }
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        @Nullable BlockState original = super.getStateForPlacement(context);

        return Objects.requireNonNullElseGet(original, this::defaultBlockState).setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public boolean hasMuffler(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.getValue(MUFFLED);
    }

    @Override
    public ItemStack getMuffler(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof SteelBlastProcessorBlockEntity blastProcessor) {
            return blastProcessor.getMuffler();
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setMuffler(Level level, BlockPos pos, ItemStack stack) {
        if (level.getBlockEntity(pos) instanceof SteelBlastProcessorBlockEntity blastProcessor) {
            blastProcessor.setMuffler(stack);
        }
    }

    protected boolean canExhaustReplaceState(Level level, BlockPos pos, BlockState state) {
        if (state.is(KlaxonBlockTags.STEEL_BLAST_PROCESSOR_EXHAUST_OVERWRITABLE_DENYLIST)) {
            return false;
        } else if (state.is(KlaxonBlockTags.STEEL_BLAST_PROCESSOR_EXHAUST_OVERWRITABLE_ALLOWLIST)) {
            return true;
        }

        if (state.is(KlaxonBlockTags.STEEL_BLAST_PROCESSOR_FIRE_HOLDERS) && state.hasProperty(BlockStateProperties.LIT)) {
            return true;
        }

        if (state.getBlock() instanceof SteelBlastProcessorExhaustHandler handler && handler.klaxon$handleExhaust(level, pos, state)) {
            return true;
        }

        if (state.canBeReplaced() || state.getDestroySpeed(level, pos) == 0f) {
            return true;
        }

        return false;
    }
}
