package net.myriantics.klaxon.component.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentMapImpl;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.registry.minecraft.KlaxonDataComponentTypes;

import java.util.HashMap;
import java.util.Map;

public record PrebakedInnateItemEnchantmentsComponent(Map<RegistryKey<Enchantment>, Integer> prebaked) {
    public static Codec<PrebakedInnateItemEnchantmentsComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.unboundedMap(RegistryKey.createCodec(RegistryKeys.ENCHANTMENT), Codec.intRange(0, 255)).fieldOf("levels").forGetter(PrebakedInnateItemEnchantmentsComponent::prebaked)
            ).apply(instance, PrebakedInnateItemEnchantmentsComponent::new));

    public static PacketCodec<RegistryByteBuf, PrebakedInnateItemEnchantmentsComponent> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.map(Object2IntOpenHashMap::new, RegistryKey.createPacketCodec(RegistryKeys.ENCHANTMENT), PacketCodecs.INTEGER), PrebakedInnateItemEnchantmentsComponent::prebaked,
            PrebakedInnateItemEnchantmentsComponent::new
    );

    private static DynamicRegistryManager manager = null;

    public ComponentMap bake() {
        ComponentMap.Builder mapBuilder = ComponentMap.builder();
        if (manager != null) {
            ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
            for (RegistryKey<Enchantment> key : prebaked.keySet()) {
                RegistryEntry<Enchantment> entry = manager.get(RegistryKeys.ENCHANTMENT).entryOf(key);
                builder.add(entry, prebaked.get(key));
            }

            mapBuilder.add(KlaxonDataComponentTypes.INNATE_ENCHANTMENTS, new InnateItemEnchantmentsComponent(builder.build().withShowInTooltip(false)));
        } else  {
            KlaxonCommon.LOGGER.warn("Failed to bake components due to missing manager!");
        }
        return mapBuilder.build();
    }

    public static void updateRegistryLookup(DynamicRegistryManager manager) {
        PrebakedInnateItemEnchantmentsComponent.manager = manager;
    }

    public static void onClientFinishJoinWorld(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client) {
        if (client.world != null) updateRegistryLookup(client.world.getRegistryManager());
    }

    public static void onServerStarted(MinecraftServer minecraftServer) {
        updateRegistryLookup(minecraftServer.getReloadableRegistries().getRegistryManager());
    }
}
