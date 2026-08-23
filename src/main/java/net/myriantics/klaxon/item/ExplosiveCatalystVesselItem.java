package net.myriantics.klaxon.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystBehavior;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystData;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystVessel;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public abstract class ExplosiveCatalystVesselItem extends Item {
    public ExplosiveCatalystVesselItem(Properties properties) {
        super(properties);
    }

    @SuppressWarnings("unchecked")
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos interactedPos = context.getClickedPos();
        BlockState interactedState = level.getBlockState(interactedPos);
        @Nullable BlockEntity interactedBlockEntity = level.getBlockEntity(interactedPos);
        ItemStack usedStack = context.getItemInHand();
        @Nullable Player player = context.getPlayer();
        if (player != null) {
            if (player.isCreative() && interactedBlockEntity instanceof ExplosiveCatalystVessel vessel && vessel.hasDataReady()) {
                if (!level.isClientSide() && vessel.hasDataReady()) {
                    Component blockName = level.getBlockEntity(interactedPos) instanceof Nameable nameable ? nameable.getDisplayName() : interactedState.getBlock().getName();
                    ExplosiveCatalystData vesselData = Objects.requireNonNullElse(vessel.getRawData(), ExplosiveCatalystData.ZERO);
                    usedStack.set(KlaxonDataComponentTypes.EXPLOSIVE_CATALYST_DATA.value(), vessel.getRawData());

                    // not clean... should've been one line... >:(
                    ExplosiveCatalystBehavior behavior = vesselData.behavior(level).value();
                    DataComponentMap blockEntityComponents = interactedBlockEntity.collectComponents();
                    for (DataComponentType<?> type : blockEntityComponents.keySet()) {
                        if (behavior.isComponentRelevant(type)) {
                            usedStack.set((DataComponentType<Object>) type, (Object) blockEntityComponents.get(type));
                        }
                    }
                    player.displayClientMessage(Component.translatable("klaxon.text.actionbar.explosive_catalyst_data.copy_from_to", blockName, usedStack.getDisplayName()), true);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.useOn(context);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        Fireworks fireworks = stack.get(DataComponents.FIREWORKS);
        if (fireworks != null) {
            fireworks.addToTooltip(context, tooltipComponents::add, tooltipFlag);
        }
        FireworkExplosion fireworkExplosion = stack.get(DataComponents.FIREWORK_EXPLOSION);
        if (fireworkExplosion != null) {
            fireworkExplosion.addToTooltip(context, tooltipComponents::add, tooltipFlag);
        }
    }
}
