package net.myriantics.klaxon.compat.jade.providers.block;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.compat.jade.KlaxonJadePlugin;
import net.myriantics.klaxon.mechanics.muffling.MufflableBlock;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.StreamServerDataProvider;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IDisplayHelper;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public enum MufflableBlockProvider implements IBlockComponentProvider, StreamServerDataProvider<BlockAccessor, ItemStack> {
    INSTANCE;

    public static final ResourceLocation ID = KlaxonJadePlugin.locate("mufflable_block");
    public static final String NOT_MUFFLED = KlaxonJadePlugin.textTranslationKey(ID, "not_muffled");
    public static final String MUFFLED = KlaxonJadePlugin.textTranslationKey(ID, "muffled");
    public static final String CONFIG = KlaxonJadePlugin.configTranslationKey(ID);

    @Override
    public boolean shouldRequestData(BlockAccessor accessor) {
        return isMuffled(accessor);
    }

    private boolean isMuffled(BlockAccessor accessor) {
        return accessor.getBlockState().getBlock() instanceof MufflableBlock mufflableBlock && mufflableBlock.hasMuffler(accessor.getLevel(), accessor.getPosition());
    }

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        ItemStack mufflerStack = this.isMuffled(blockAccessor) ? this.decodeFromData(blockAccessor).orElse(ItemStack.EMPTY) : ItemStack.EMPTY;
        if (mufflerStack.isEmpty()) {
            iTooltip.add(Component.translatable(NOT_MUFFLED));
        } else {
            IElementHelper helper = IElementHelper.get();
            List<IElement> elements = new ArrayList<>();
            elements.add(helper.text(Component.translatable(MUFFLED)));
            elements.add(helper.smallItem(mufflerStack));
            elements.add(helper.text(Component.literal(" ").append(IDisplayHelper.get().stripColor(mufflerStack.getHoverName()))));
            iTooltip.add(elements);
        }
    }

    @Override
    public ItemStack streamData(BlockAccessor blockAccessor) {
        if (this.isMuffled(blockAccessor)) {
            MufflableBlock mufflableBlock = (MufflableBlock) blockAccessor.getBlock();
            return mufflableBlock.getMuffler(blockAccessor.getLevel(), blockAccessor.getPosition());
        } else {
            return ItemStack.EMPTY;
        }
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
