package net.myriantics.klaxon.block.functional.pressure_plate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.myriantics.klaxon.mechanics.entity_weight.EntityWeightHelper;

public class HeavyGatedPressurePlateBlock extends GatedPressurePlateBlock {
    public static final MapCodec<HeavyGatedPressurePlateBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            propertiesCodec(),
            BlockSetType.CODEC.fieldOf("block_set_type").forGetter(pressurePlateBlock -> pressurePlateBlock.type)
            ).apply(instance, HeavyGatedPressurePlateBlock::new)
    );

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public HeavyGatedPressurePlateBlock(BlockBehaviour.Properties properties, BlockSetType type) {
        super(properties, type);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(POWERED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    protected MapCodec<? extends BasePressurePlateBlock> codec() {
        return CODEC;
    }

    @Override
    protected int getSignalForState(BlockState state) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    @Override
    protected BlockState setSignalForState(BlockState state, int signal) {
        return state.setValue(POWERED, signal > 0);
    }

    @Override
    protected boolean entityPredicate(Entity entity) {
        return EntityWeightHelper.isHeavy(entity);
    }
}
