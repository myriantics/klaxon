package net.myriantics.klaxon.component.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.myriantics.klaxon.registry.minecraft.KlaxonDataComponentTypes;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record InnateItemEnchantmentsComponent(ItemEnchantmentsComponent bakedEnchantments, Map<RegistryKey<Enchantment>, Integer> prebakedLevels) {
    public InnateItemEnchantmentsComponent(Map<RegistryKey<Enchantment>, Integer> prebakedLevels)  {
        this(ItemEnchantmentsComponent.DEFAULT, prebakedLevels);
    }

    private static RegistryEntryLookup.RegistryLookup lookup = null;

    public static final Codec<InnateItemEnchantmentsComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codec.unboundedMap(RegistryKey.createCodec(RegistryKeys.ENCHANTMENT), Codec.INT).fieldOf("enchantments").forGetter(InnateItemEnchantmentsComponent::prebakedLevels)
                    )
                    .apply(instance, InnateItemEnchantmentsComponent::new)
    );

    public static final PacketCodec<RegistryByteBuf, InnateItemEnchantmentsComponent> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.map(Object2ObjectOpenHashMap::new, RegistryKey.createPacketCodec(RegistryKeys.ENCHANTMENT), PacketCodecs.INTEGER), InnateItemEnchantmentsComponent::prebakedLevels,
            InnateItemEnchantmentsComponent::new
    );

    public static @Nullable InnateItemEnchantmentsComponent get(ItemStack stack) {
        InnateItemEnchantmentsComponent component = stack.getComponents().get(KlaxonDataComponentTypes.INNATE_ENCHANTMENTS);
        if (component != null && component.bakedEnchantments == ItemEnchantmentsComponent.DEFAULT) {
            component.bakeEnchantments(stack);
        }
        return component;
    }

    public void set(ItemStack stack) {
        stack.applyComponentsFrom(ComponentMap.builder().add(KlaxonDataComponentTypes.INNATE_ENCHANTMENTS, this).build());
    }

    public int getLevel(RegistryEntry<Enchantment> enchantment) {
        return bakedEnchantments().getLevel(enchantment);
    }

    private void bakeEnchantments(ItemStack stack) {
        if (lookup != null) {
            ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
            for (RegistryKey<Enchantment> key : prebakedLevels.keySet()) {
                Optional<RegistryEntry.Reference<Enchantment>> entry = lookup.getOptionalEntry(RegistryKeys.ENCHANTMENT, key);
                // the ide wants me to replace this with functional style, but this is more readable than whatever that crap is, so it's staying
                if (entry.isPresent()) {
                    builder.add(entry.get(), prebakedLevels.get(key));
                }
            }

            stack.applyComponentsFrom(ComponentMap.builder().add(KlaxonDataComponentTypes.INNATE_ENCHANTMENTS, new InnateItemEnchantmentsComponent(builder.build().withShowInTooltip(false), prebakedLevels)).build());
        }
    }

    public static void updateRegistryLookup(RegistryEntryLookup.RegistryLookup lookup) {
        InnateItemEnchantmentsComponent.lookup = lookup;
    }

    public static void onClientFinishJoinWorld(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client) {
        if (client.world != null) InnateItemEnchantmentsComponent.updateRegistryLookup(client.world.getRegistryManager().createRegistryLookup());
    }

    public static void onServerStarted(MinecraftServer minecraftServer) {
        InnateItemEnchantmentsComponent.updateRegistryLookup(minecraftServer.getRegistryManager().createRegistryLookup());
    }
}
