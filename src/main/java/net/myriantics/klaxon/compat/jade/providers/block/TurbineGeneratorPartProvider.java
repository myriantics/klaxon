package net.myriantics.klaxon.compat.jade.providers.block;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.myriantics.klaxon.block.machines.energy.generators.turbine.TurbineGeneratorBlockEntity;
import net.myriantics.klaxon.compat.jade.KlaxonJadePlugin;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.StreamServerDataProvider;
import snownee.jade.api.config.IPluginConfig;

public enum TurbineGeneratorPartProvider implements IBlockComponentProvider, StreamServerDataProvider<BlockAccessor, ItemStack> {
    INSTANCE;

    public static final ResourceLocation ID = KlaxonJadePlugin.locate("turbine_generator_part");
    public static final String CONFIG = KlaxonJadePlugin.configTranslationKey(ID);
    public static final String TURBINE_DURABILITY = KlaxonJadePlugin.textTranslationKey(ID, "turbine_durability");
    public static final String TURBINE_MISSING = KlaxonJadePlugin.textTranslationKey(ID, "turbine_missing");

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor accessor, IPluginConfig iPluginConfig) {
        ItemStack turbineStack = this.decodeFromData(accessor).orElse(ItemStack.EMPTY);
        if (turbineStack.isEmpty()) {
            iTooltip.add(Component.translatable(TURBINE_MISSING));
        } else {
            iTooltip.add(Component.translatable(TURBINE_DURABILITY, turbineStack.getMaxDamage() - turbineStack.getDamageValue(), turbineStack.getMaxDamage()));
        }
    }

    @Override
    public @Nullable ItemStack streamData(BlockAccessor accessor) {
        return accessor.getBlockEntity() instanceof TurbineGeneratorBlockEntity blockEntity && blockEntity.hasTurbine() ? blockEntity.getTurbineStack() : null;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ItemStack> streamCodec() {
        return ItemStack.STREAM_CODEC;
    }

    @Override
    public ResourceLocation getUid() {
        return ID;
    }
}
