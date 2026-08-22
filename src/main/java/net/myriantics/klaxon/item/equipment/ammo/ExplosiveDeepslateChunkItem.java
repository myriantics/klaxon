package net.myriantics.klaxon.item.equipment.ammo;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.entity.entities.projectile.explosive_deepslate_chunk.ExplosiveDeepslateChunkEntity;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystData;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystVessel;
import net.myriantics.klaxon.registry.entity.KlaxonEntityTypes;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ExplosiveDeepslateChunkItem extends Item implements ProjectileItem {
    public ExplosiveDeepslateChunkItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        ExplosiveDeepslateChunkEntity chunk = new ExplosiveDeepslateChunkEntity(level, stack, player.getX(), player.getEyeY(), player.getZ());
        chunk.setOwner(player);
        chunk.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
        level.addFreshEntity(chunk);
        stack.consume(1, player);
        return InteractionResultHolder.success(stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos interactedPos = context.getClickedPos();
        BlockState interactedState = level.getBlockState(interactedPos);
        @Nullable BlockEntity interactedBlockEntity = level.getBlockEntity(interactedPos);
        ItemStack usedStack = context.getItemInHand();
        @Nullable Player player = context.getPlayer();
        if (player != null && interactedBlockEntity instanceof ExplosiveCatalystVessel vessel && vessel.hasDataReady()) {
            if (!level.isClientSide() && vessel.hasDataReady()) {
                Component blockName = level.getBlockEntity(interactedPos) instanceof Nameable nameable ? nameable.getDisplayName() : interactedState.getBlock().getName();
                ExplosiveCatalystData vesselData = Objects.requireNonNullElse(vessel.getRawData(), ExplosiveCatalystData.ZERO);
                usedStack.set(KlaxonDataComponentTypes.EXPLOSIVE_CATALYST_DATA.value(), vessel.getRawData());
                usedStack.applyComponents(interactedBlockEntity.collectComponents().filter(vesselData.behavior(level).value()::isComponentIrrelevant));
                player.displayClientMessage(Component.translatable("klaxon.text.actionbar.explosive_catalyst_data.copy_from_to", blockName, usedStack.getDisplayName()), true);
            }
            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }

    @Override
    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        return new ExplosiveDeepslateChunkEntity(level, stack, pos.x(), pos.y(), pos.z());
    }
}
