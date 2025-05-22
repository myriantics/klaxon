package net.myriantics.klaxon.component.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.myriantics.klaxon.registry.minecraft.KlaxonDataComponentTypes;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record InnateItemEnchantmentsComponent(ItemEnchantmentsComponent component, Map<RegistryKey<Enchantment>, Integer> levelsFromKey) {

    public static final Codec<InnateItemEnchantmentsComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            ItemEnchantmentsComponent.CODEC.fieldOf("enchantments").forGetter(InnateItemEnchantmentsComponent::component)
                    )
                    .apply(instance, InnateItemEnchantmentsComponent::ofItemEnchantmentsComponent)
    );

    public static final PacketCodec<RegistryByteBuf, InnateItemEnchantmentsComponent> PACKET_CODEC = PacketCodec.tuple(
            ItemEnchantmentsComponent.PACKET_CODEC,
            InnateItemEnchantmentsComponent::component,
            InnateItemEnchantmentsComponent::ofItemEnchantmentsComponent
    );

    public static @Nullable InnateItemEnchantmentsComponent get(ItemStack stack) {
        return stack.getComponents().get(KlaxonDataComponentTypes.INNATE_ENCHANTMENTS);
    }

    public void set(ItemStack stack) {
        stack.applyComponentsFrom(ComponentMap.builder().add(KlaxonDataComponentTypes.INNATE_ENCHANTMENTS, this).build());
    }

    public static InnateItemEnchantmentsComponent ofItemEnchantmentsComponent(ItemEnchantmentsComponent component) {
        Map<RegistryKey<Enchantment>, Integer> levels2 = new HashMap<>();
        for (RegistryEntry<Enchantment> entry : component.getEnchantments()) {
            if (entry.getKey().isPresent()) levels2.put(entry.getKey().get(), component.getLevel(entry));
        }
        return new InnateItemEnchantmentsComponent(component, levels2);
    }

    public static InnateItemEnchantmentsComponent create(Map<RegistryKey<Enchantment>, Integer> pairs) {
        ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
        for (RegistryKey<Enchantment> key : pairs.keySet()) {
            // tyvm to hisui on The Fabric Project Discord for providing a reference to help work this out. Least convoluted system haha.
            Optional<RegistryEntry.Reference<Enchantment>> entry = BuiltinRegistries.createWrapperLookup().createRegistryLookup().getOptionalEntry(RegistryKeys.ENCHANTMENT, key);
            // the ide wants me to replace this with functional style, but this is more readable than whatever that crap is, so it's staying
            if (entry.isPresent()) {
                builder.add(entry.get(), pairs.get(key));
            }
        }

        return InnateItemEnchantmentsComponent.ofItemEnchantmentsComponent(builder.build().withShowInTooltip(false));
    }
}
