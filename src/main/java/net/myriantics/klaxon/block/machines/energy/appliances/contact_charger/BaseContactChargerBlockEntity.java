package net.myriantics.klaxon.block.machines.energy.appliances.contact_charger;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntityTypes;
import net.myriantics.klaxon.registry.misc.KlaxonNBTIds;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.UUID;

public class BaseContactChargerBlockEntity extends BlockEntity {

    private static final int MAX_KEEP_ALIVE_TICKS = 6;

    private EnergyStorage stackEnergyStorage = null;
    private ItemStack chargingStack = null;
    private UUID userUUID = null;
    private Player user = null;
    private int preferredReplacementSlot = -1;
    private int keepAliveTicks = 0;

    protected BaseContactChargerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public BaseContactChargerBlockEntity(BlockPos pos, BlockState state) {
        this(KlaxonBlockEntityTypes.CREATIVE_CONTACT_CHARGER.value(), pos, state);
    }

    public boolean startCharging(ItemStack stack, ServerPlayer player, int preferredReplacementSlot) {
        this.chargingStack = stack;
        this.stackEnergyStorage = this.initEnergyStorage();
        this.userUUID = player.getUUID();
        this.user = player;
        this.preferredReplacementSlot = preferredReplacementSlot;
        this.refreshKeepAliveTicks();
        this.updateClients();
        return true;
    }

    public void serverTick(Level level, BlockPos blockPos, BlockState blockState) {
        if (!this.hasItem()) {
            return;
        }

        if (!this.isPlayerValid()) {
            this.ejectHeldStack();
            this.clear();
            return;
        }

        if (this.keepAliveTicks-- <= 0) {
            this.grantHeldStackBackToPlayer();
            this.clear();
            return;
        }

        if (this.stackEnergyStorage != null) {
            try (Transaction tx = Transaction.openOuter()) {
                this.stackEnergyStorage.insert(67, tx);
                tx.commit();
            }
            this.setChanged();
        }
    }

    public int getAnalogSignalForChargeFullness() {
        if (this.stackEnergyStorage == null) {
            return 0;
        } else {
            return Mth.lerpDiscrete((float) this.stackEnergyStorage.getAmount() / this.stackEnergyStorage.getCapacity(), 0, 15);
        }
    }

    public void refreshKeepAliveTicks() {
        this.keepAliveTicks = MAX_KEEP_ALIVE_TICKS;
        this.setChanged();
    }

    public ItemStack getChargingStack() {
        return this.chargingStack;
    }

    /**
     * Assumes player has already been validated and that we have a charging stack
     * Tries to insert charged stack into player's selected slot, then the slot the stack was inserted from, then into whatever slot accepts it.
     */
    public void grantHeldStackBackToPlayer() {
        Inventory inventory = this.user.getInventory();
        PlayerInventoryStorage storage = PlayerInventoryStorage.of(inventory);
        ItemVariant chargedVariant = ItemVariant.of(this.chargingStack);

        int remainingStackCount = this.chargingStack.getCount();

        // try saved slot first
        if (Inventory.isHotbarSlot(this.preferredReplacementSlot) || this.preferredReplacementSlot == Inventory.SLOT_OFFHAND) {
            SingleSlotStorage<ItemVariant> slot = storage.getSlot(this.preferredReplacementSlot);
            try (Transaction tx = Transaction.openOuter()) {
                int amountInserted = Math.toIntExact(slot.insert(chargedVariant, remainingStackCount, tx));
                if (amountInserted > 0) {
                    remainingStackCount -= amountInserted;
                    tx.commit();
                } else {
                    tx.abort();
                }
            }
        }

        if (remainingStackCount == 0) {
            return;
        }

        // try selected hotbar slot next
        if (Inventory.isHotbarSlot(inventory.selected)) {
            SingleSlotStorage<ItemVariant> slot = storage.getSlot(inventory.selected);
            try (Transaction tx = Transaction.openOuter()) {
                int amountInserted = Math.toIntExact(slot.insert(chargedVariant, remainingStackCount, tx));
                if (amountInserted > 0) {
                    remainingStackCount -= amountInserted;
                    tx.commit();
                } else {
                    tx.abort();
                }
            }
        }

        if (remainingStackCount == 0) {
            return;
        }

        try (Transaction tx = Transaction.openOuter()) {
            storage.offerOrDrop(chargedVariant, remainingStackCount, tx);
            tx.commit();
        }
    }

    /**
     * Assumes the level is present
     */
    public void ejectHeldStack() {
        Vec3 centerPos = this.worldPosition.getCenter();
        ItemEntity droppedItem = new ItemEntity(this.level, centerPos.x, centerPos.y, centerPos.z, this.chargingStack.copy());
        droppedItem.setPos(centerPos.x, centerPos.y - (droppedItem.getBbHeight() / 2), centerPos.z);
        this.level.addFreshEntity(droppedItem);
    }

    public void clear() {
        this.stackEnergyStorage = null;
        this.chargingStack = null;
        this.user = null;
        this.userUUID = null;
        this.preferredReplacementSlot = -1;
        this.setChanged();
        this.updateClients();
    }

    public boolean acceptsStack(ItemStack stack) {
        return !stack.isEmpty();//EnergyStorageUtil.isEnergyStorage(stack);
    }

    private boolean isPlayerValid() {
        Player user = this.getUser();

        if (user == null || user.isRemoved()) {
            return false;
        }
        if (user.level() != this.level) {
            return false;
        }

        Vec3 playerPos = user.position();
        BlockPos blockPos = this.worldPosition;
        double x = blockPos.getX() - playerPos.x;
        double y = blockPos.getY() - playerPos.y;
        double z = blockPos.getZ() - playerPos.z;
        double distance = Math.sqrt(x * x + y * y + z * z);

        if (distance > user.blockInteractionRange() * 1.5) {
            return false;
        }
        return true;
    }

    public @Nullable Player getUser() {
        if (!this.hasItem()) {
            return null;
        }
        if (this.user == null) {
            if (this.userUUID == null || this.level == null) {
                return null;
            } else {
                Player player = this.level.getPlayerByUUID(this.userUUID);
                this.user = player;
                return player;
            }
        } else {
            return this.user;
        }
    }

    public boolean hasItem() {
        return this.chargingStack != null && !this.chargingStack.isEmpty();
    }

    private EnergyStorage initEnergyStorage() {
        return EnergyStorage.ITEM.find(this.chargingStack, ContainerItemContext.ofSingleSlot(new SingleStackStorage() {
            @Override
            protected ItemStack getStack() {
                return BaseContactChargerBlockEntity.this.chargingStack;
            }

            @Override
            protected void setStack(ItemStack stack) {
                BaseContactChargerBlockEntity.this.chargingStack = stack;
                BaseContactChargerBlockEntity.this.setChanged();
                BaseContactChargerBlockEntity.this.updateClients();
            }
        }));
    }

    protected void updateClients() {
        this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), (Block.UPDATE_ALL_IMMEDIATE));
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = this.saveCustomOnly(registries);
        tag.remove(KlaxonNBTIds.PREFERRED_REPLACEMENT_SLOT);
        return tag;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (this.chargingStack != null && !this.chargingStack.isEmpty()) {
            tag.put(KlaxonNBTIds.CHARGING_STACK, this.chargingStack.save(registries));
        }
        if (this.userUUID != null) {
            tag.putUUID(KlaxonNBTIds.USER_UUID, this.userUUID);
        }
        tag.putInt(KlaxonNBTIds.PREFERRED_REPLACEMENT_SLOT, this.preferredReplacementSlot);
        tag.putInt(KlaxonNBTIds.KEEP_ALIVE_TICKS, this.keepAliveTicks);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(KlaxonNBTIds.CHARGING_STACK)) {
            this.chargingStack = ItemStack.parse(registries, tag.get(KlaxonNBTIds.CHARGING_STACK)).orElse(null);
            this.stackEnergyStorage = this.chargingStack == null ? null : this.initEnergyStorage();
        } else {
            this.chargingStack = null;
            this.stackEnergyStorage = null;
        }
        if (tag.contains(KlaxonNBTIds.USER_UUID)) {
            this.userUUID = tag.getUUID(KlaxonNBTIds.USER_UUID);
        } else {
            this.userUUID = null;
        }
        this.preferredReplacementSlot = this.validatePreferredReplacementSlot(tag.getInt(KlaxonNBTIds.PREFERRED_REPLACEMENT_SLOT));
        this.keepAliveTicks = Math.clamp(tag.getInt(KlaxonNBTIds.KEEP_ALIVE_TICKS), -1, MAX_KEEP_ALIVE_TICKS);
    }

    private int validatePreferredReplacementSlot(int unvalidatedSlot) {
        return Inventory.isHotbarSlot(unvalidatedSlot) || unvalidatedSlot == Inventory.SLOT_OFFHAND ? unvalidatedSlot : -1;
    }
}
