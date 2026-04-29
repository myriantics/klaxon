package net.myriantics.klaxon.block.functional.pressure_plate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.myriantics.klaxon.mechanics.entity_weight.EntityWeightHelper;
import net.myriantics.klaxon.registry.block.KlaxonBlockStateProperties;

import java.util.List;

// slop ACTIVATE
public class FaultyHeavyGatedPressurePlateBlock extends GatedPressurePlateBlock {

    public static final EnumProperty<State> STATE = KlaxonBlockStateProperties.FAULTY_HEAVY_GATED_PRESSURE_PLATE_STATE;

    public static final MapCodec<FaultyHeavyGatedPressurePlateBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    propertiesCodec(),
                    BlockSetType.CODEC.fieldOf("block_set_type").forGetter(pressurePlateBlock -> pressurePlateBlock.type)
            ).apply(instance, FaultyHeavyGatedPressurePlateBlock::new)
    );

    public FaultyHeavyGatedPressurePlateBlock(Properties properties, BlockSetType type) {
        super(properties, type);
        registerDefaultState(this.stateDefinition.any()
                .setValue(STATE, State.DEPRESSED)
        );
    }

    @Override
    protected MapCodec<? extends BasePressurePlateBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STATE);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(STATE).equals(State.STRESSED)) {
            level.destroyBlock(pos, false);
        } else {
            super.tick(state, level, pos, random);
        }
    }

    @Override
    protected BlockState setSignalForState(BlockState state, int signal) {
        if (signal < 7) {
            return state.setValue(STATE, State.DEPRESSED);
        } else if (signal < 15) {
            return state.setValue(STATE, State.PRESSED);
        } else {
            return state.setValue(STATE, State.STRESSED);
        }
    }

    @Override
    protected int getSignalStrength(Level level, BlockPos pos) {
        Class<? extends Entity> clazz = switch (this.type.pressurePlateSensitivity()) {
            case EVERYTHING -> Entity.class;
            case MOBS -> LivingEntity.class;
        };
        List<? extends Entity> contacting = this.getContactingEntities(level, clazz, TOUCH_AABB.move(pos));
        if (contacting.isEmpty()) {
            return 0;
        }
        for (Entity entity : contacting) {
            if (EntityWeightHelper.isHeavy(entity)) {
                return 15;
            }
        }
        return 7;
    }

    @Override
    protected int getSignalForState(BlockState state) {
        return switch (state.getValue(STATE)) {
            case DEPRESSED -> 0;
            case PRESSED -> 7;
            case STRESSED -> 15;
        };
    }

    public enum State implements StringRepresentable {
        DEPRESSED,
        PRESSED,
        STRESSED;

        @Override
        public String getSerializedName() {
            return switch (this) {
                case DEPRESSED -> "depressed";
                case PRESSED -> "pressed";
                case STRESSED -> "stressed";
            };
        }
    }

    @Override
    protected boolean entityPredicate(Entity entity) {
        return true;
    }
}
