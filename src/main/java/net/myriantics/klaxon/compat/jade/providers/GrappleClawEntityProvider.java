package net.myriantics.klaxon.compat.jade.providers;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawEntity;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.StreamServerDataProvider;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;

public enum GrappleClawEntityProvider implements IEntityComponentProvider, StreamServerDataProvider<EntityAccessor, ItemStack> {
    INSTANCE;

    private static final ResourceLocation ID = KlaxonCommon.locate("grapple_claw");

    @Override
    public @Nullable IElement getIcon(EntityAccessor accessor, IPluginConfig config, IElement currentIcon) {
        ItemStack grappleClawStack = this.decodeFromData(accessor).orElse(ItemStack.EMPTY);
        return grappleClawStack.isEmpty() ? currentIcon : IElementHelper.get().item(grappleClawStack);
    }

    @Override
    public void appendTooltip(ITooltip iTooltip, EntityAccessor entityAccessor, IPluginConfig iPluginConfig) {
    }

    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Override
    public @Nullable ItemStack streamData(EntityAccessor entityAccessor) {
        if (entityAccessor.getEntity() instanceof GrappleClawEntity grappleClaw) {
            return grappleClaw.getPickupItemStackOrigin();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ItemStack> streamCodec() {
        return ItemStack.STREAM_CODEC;
    }
}
