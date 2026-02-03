package net.myriantics.klaxon.compat.jade.providers;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawEntity;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;

public enum GrappleClawEntityProvider implements IEntityComponentProvider, StreamServerDataProvider<EntityAccessor, ItemStack> {
    INSTANCE;

    private static final Identifier ID = KlaxonCommon.locate("grapple_claw");

    @Override
    public @Nullable IElement getIcon(EntityAccessor accessor, IPluginConfig config, IElement currentIcon) {
        ItemStack grappleClawStack = this.decodeFromData(accessor).orElse(ItemStack.EMPTY);
        return grappleClawStack.isEmpty() ? currentIcon : IElementHelper.get().item(grappleClawStack);
    }

    @Override
    public void appendTooltip(ITooltip iTooltip, EntityAccessor entityAccessor, IPluginConfig iPluginConfig) {
    }

    @Override
    public Identifier getUid() {
        return ID;
    }

    @Override
    public @Nullable ItemStack streamData(EntityAccessor entityAccessor) {
        if (entityAccessor.getEntity() instanceof GrappleClawEntity grappleClaw) {
            return grappleClaw.getItemStack();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public PacketCodec<RegistryByteBuf, ItemStack> streamCodec() {
        return ItemStack.PACKET_CODEC;
    }
}
