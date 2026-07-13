package net.myriantics.klaxon.block.machines.energy.contact_charger;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

public class ContactChargerBlock extends Block {
    public static final MapCodec<ContactChargerBlock> CODEC = simpleCodec(ContactChargerBlock::new);

    public ContactChargerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        @Nullable EnergyStorage storage = EnergyStorage.ITEM.find(stack, ContainerItemContext.forPlayerInteraction(player, hand));
        if (storage == null) {
            return ItemInteractionResult.FAIL;
        } else {
            if (!level.isClientSide()) {
                try (Transaction tx = Transaction.openOuter()) {
                    storage.insert(67, tx);
                    tx.commit();
                }
            }
            return ItemInteractionResult.SUCCESS;
        }
    }
}
