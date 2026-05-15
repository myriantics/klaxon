package net.myriantics.klaxon.block.machines.modular_explosive;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.myriantics.klaxon.component.configuration.ModularExplosiveBlockConfigComponent;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystBehavior;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystData;
import net.myriantics.klaxon.mechanics.explosive_catalyst.context.ExplosiveCatalystContext;
import net.myriantics.klaxon.registry.explosive_catalyst.KlaxonExplosiveCatalystContextParams;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;

public class ModularExplosiveBlockDispenserBehavior extends DefaultDispenseItemBehavior {

    @Override
    protected ItemStack execute(BlockSource blockSource, ItemStack stack) {
        ServerLevel level = blockSource.level();
        BlockPos pos = blockSource.pos();
        Direction facing = blockSource.state().getValue(DispenserBlock.FACING);
        Position targetPos = pos.relative(facing).getCenter();

        ExplosiveCatalystData data = stack.getOrDefault(KlaxonDataComponentTypes.EXPLOSIVE_CATALYST_DATA.value(), ExplosiveCatalystData.ZERO);
        ModularExplosiveBlockConfigComponent config = stack.getOrDefault(KlaxonDataComponentTypes.MODULAR_EXPLOSIVE_BLOCK_CONFIG.value(), ModularExplosiveBlockConfigComponent.DEFAULT);
        Holder<ExplosiveCatalystBehavior> behavior = data.behavior(level);

        if (config.isDetonationDisabled()) {
            return super.execute(blockSource, stack);
        }

        ExplosiveCatalystContext context = new ExplosiveCatalystContext(level, stack.getComponents());
        context.add(KlaxonExplosiveCatalystContextParams.BLOCK_POS, pos);
        context.add(KlaxonExplosiveCatalystContextParams.SUPPORT_STATE, level.getBlockState(pos.below()));

        data = behavior.value().transformExplosiveCatalystData(context, data);
        data.behavior(level).value().createExplosion(context, targetPos, data, config.modifyWorld());

        stack.shrink(1);
        return stack;
    }

    @Override
    protected void playSound(BlockSource blockSource) {
        super.playSound(blockSource);
    }
}
