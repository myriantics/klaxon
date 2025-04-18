package net.myriantics.klaxon.component.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Pair;
import net.myriantics.klaxon.registry.minecraft.KlaxonDataComponentTypes;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

//todo: This doesn't work with enchantment effects such as sharpness because it uses a different system. FIX SOON!!!
public record InnateItemEnchantmentsComponent(Object2IntOpenHashMap<RegistryKey<Enchantment>> innateEnchantments) {
    private static final Codec<RegistryKey<Enchantment>> ENCHANTMENT_KEY_CODEC = RegistryKey.createCodec(RegistryKeys.ENCHANTMENT);
    private static final Codec<Integer> ENCHANTMENT_LEVEL_CODEC = Codec.intRange(0, 255);
    private static final Codec<Object2IntOpenHashMap<RegistryKey<Enchantment>>> INLINE_CODEC = Codec.unboundedMap(
                    ENCHANTMENT_KEY_CODEC, ENCHANTMENT_LEVEL_CODEC
            )
            .xmap(Object2IntOpenHashMap::new, Function.identity());
    private static final Codec<InnateItemEnchantmentsComponent> BASE_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            INLINE_CODEC.fieldOf("levels").forGetter(InnateItemEnchantmentsComponent::innateEnchantments)
                            // might add tooltip functionality eventually. maybe when i break this off into an api mod
                            //Codec.BOOL.optionalFieldOf("show_in_tooltip", true).forGetter(component -> component.showInTooltip)
                    )
                    .apply(instance, InnateItemEnchantmentsComponent::new)
    );

    private static final PacketCodec<ByteBuf, RegistryKey<Enchantment>> ENCHANTMENT_KEY_PACKET_CODEC = RegistryKey.createPacketCodec(RegistryKeys.ENCHANTMENT);

    public static final Codec<InnateItemEnchantmentsComponent> CODEC = Codec.withAlternative(BASE_CODEC, INLINE_CODEC, InnateItemEnchantmentsComponent::new);
    public static final PacketCodec<ByteBuf, InnateItemEnchantmentsComponent> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.map(Object2IntOpenHashMap::new, ENCHANTMENT_KEY_PACKET_CODEC, PacketCodecs.VAR_INT),
            InnateItemEnchantmentsComponent::innateEnchantments,
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
        Object2IntOpenHashMap<RegistryKey<Enchantment>> enchantments = new Object2IntOpenHashMap<>();
        for (Pair<RegistryKey<Enchantment>, Integer> pair : pairs) {
            enchantments.put(pair.getLeft(), pair.getRight().intValue());
        }

        return new InnateItemEnchantmentsComponent(enchantments);
    }
}
