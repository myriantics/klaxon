package net.myriantics.klaxon.component.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryFixedCodec;
import net.minecraft.util.Pair;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.registry.minecraft.KlaxonDataComponentTypes;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public record InnateItemEnchantmentsComponent(ItemEnchantmentsComponent component) {

    public static final Codec<InnateItemEnchantmentsComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            ItemEnchantmentsComponent.CODEC.fieldOf("enchantments").forGetter(InnateItemEnchantmentsComponent::component)
                    )
                    .apply(instance, InnateItemEnchantmentsComponent::new)
    );

    public static final PacketCodec<RegistryByteBuf, InnateItemEnchantmentsComponent> PACKET_CODEC = PacketCodec.tuple(
            ItemEnchantmentsComponent.PACKET_CODEC,
            InnateItemEnchantmentsComponent::component,
            InnateItemEnchantmentsComponent::new
    );

    public static @Nullable InnateItemEnchantmentsComponent get(ItemStack stack) {
        return stack.getComponents().get(KlaxonDataComponentTypes.INNATE_ENCHANTMENTS);
    }

    public static void set(ItemStack stack, InnateItemEnchantmentsComponent component) {
        stack.applyComponentsFrom(ComponentMap.builder().add(KlaxonDataComponentTypes.INNATE_ENCHANTMENTS, component).build());
    }

    // i love getting errors i dont understand and then clicking the magic button to get them to hush
    @SafeVarargs
    public static InnateItemEnchantmentsComponent create(Pair<RegistryKey<Enchantment>, Integer>... pairs) {
        ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
        for (Pair<RegistryKey<Enchantment>, Integer> pair : pairs) {
            // tyvm to hisui on The Fabric Project Discord for providing a reference to help work this out. Least convoluted system haha.
            Optional<RegistryEntry.Reference<Enchantment>> entry = BuiltinRegistries.createWrapperLookup().createRegistryLookup().getOptionalEntry(RegistryKeys.ENCHANTMENT, pair.getLeft());
            // the ide wants me to replace this with functional style, but this is more readable than whatever that crap is, so it's staying
            if (entry.isPresent()) {
                builder.add(entry.get(), pair.getRight());
            }
        }

        return new InnateItemEnchantmentsComponent(builder.build().withShowInTooltip(false));
    }
}
