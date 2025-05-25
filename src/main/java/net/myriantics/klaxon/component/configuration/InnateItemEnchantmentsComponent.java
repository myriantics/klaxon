package net.myriantics.klaxon.component.configuration;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.myriantics.klaxon.registry.minecraft.KlaxonDataComponentTypes;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public record InnateItemEnchantmentsComponent(ItemEnchantmentsComponent enchantments) implements TooltipAppender {

    public static final Codec<InnateItemEnchantmentsComponent> CODEC = ItemEnchantmentsComponent.CODEC.xmap(
            InnateItemEnchantmentsComponent::new,
            InnateItemEnchantmentsComponent::enchantments
    );

    public static final PacketCodec<RegistryByteBuf, InnateItemEnchantmentsComponent> PACKET_CODEC = ItemEnchantmentsComponent.PACKET_CODEC.xmap(
            InnateItemEnchantmentsComponent::new,
            InnateItemEnchantmentsComponent::enchantments
    );

    public static @Nullable InnateItemEnchantmentsComponent get(ItemStack stack) {
        return stack.getComponents().get(KlaxonDataComponentTypes.INNATE_ENCHANTMENTS);
    }

    public void set(ItemStack stack) {
        stack.applyComponentsFrom(ComponentMap.builder().add(KlaxonDataComponentTypes.INNATE_ENCHANTMENTS, this).build());
    }

    public int getLevel(RegistryEntry<Enchantment> enchantment) {
        return enchantments().getLevel(enchantment);
    }

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type) {
        for (RegistryEntry<Enchantment> registryEntry : enchantments.getEnchantments()) {
            int i = enchantments.getLevel(registryEntry);
            if (i > 0) {
                MutableText text = Text.translatable("klaxon.text.innate_enchantment_prefix", Enchantment.getName(registryEntry, i));
                if (registryEntry.isIn(EnchantmentTags.CURSE)) {
                    Texts.setStyleIfAbsent(text, Style.EMPTY.withColor(Formatting.RED));
                } else {
                    Texts.setStyleIfAbsent(text, Style.EMPTY.withColor(Formatting.GRAY));
                }
                tooltip.accept(text);
            }
        }
    }
}
