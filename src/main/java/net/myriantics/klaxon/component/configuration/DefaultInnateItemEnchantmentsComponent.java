package net.myriantics.klaxon.component.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.server.MinecraftServer;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mixin.ItemAccessor;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;

import java.util.Map;
import java.util.Optional;

public record DefaultInnateItemEnchantmentsComponent(Map<RegistryKey<Enchantment>, Integer> prebaked) {
    public static Codec<DefaultInnateItemEnchantmentsComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.unboundedMap(RegistryKey.createCodec(RegistryKeys.ENCHANTMENT), Codec.intRange(0, 255)).fieldOf("levels").forGetter(DefaultInnateItemEnchantmentsComponent::prebaked)
            ).apply(instance, DefaultInnateItemEnchantmentsComponent::new));

    public static PacketCodec<RegistryByteBuf, DefaultInnateItemEnchantmentsComponent> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.map(Object2IntOpenHashMap::new, RegistryKey.createPacketCodec(RegistryKeys.ENCHANTMENT), PacketCodecs.INTEGER), DefaultInnateItemEnchantmentsComponent::prebaked,
            DefaultInnateItemEnchantmentsComponent::new
    );

    public Optional<ComponentMap> bake(DynamicRegistryManager manager) {
        ComponentMap.Builder mapBuilder = ComponentMap.builder();
        if (manager != null) {
            ItemEnchantmentsComponent.Builder enchantmentBuilder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
            for (RegistryKey<Enchantment> key : prebaked.keySet()) {
                Optional<RegistryEntry.Reference<Enchantment>> entry = manager.get(RegistryKeys.ENCHANTMENT).getEntry(key);

                // only add enchantment entry to the builder if there actually is one
                if (entry.isPresent()) {
                    enchantmentBuilder.add(entry.get(), prebaked.get(key));
                } else {
                    KlaxonCommon.LOGGER.error("Invalid key {} passed into Default Innate Enchantment. Skipping.", key.toString());
                }
            }

            InnateItemEnchantmentsComponent component = new InnateItemEnchantmentsComponent(enchantmentBuilder.build().withShowInTooltip(false));

            if (!component.enchantments().isEmpty()) {
                mapBuilder.add(KlaxonDataComponentTypes.INNATE_ENCHANTMENTS, component);
                return Optional.ofNullable(mapBuilder.build());
            } else {
                // if we don't have any enchantments, fail.
                return Optional.empty();
            }
        }

        // manager is null? fail - empty
        return Optional.empty();
    }

    // returns how many items had components baked
    private static int bakeAll(DynamicRegistryManager manager) {
        int operationsPerformed = 0;

        for (Item item : Registries.ITEM) {
            ComponentMap original = item.getComponents();

            // only make a change if item has prebaked innate enchantments as a default component
            if (original.get(KlaxonDataComponentTypes.DEFAULT_INNATE_ENCHANTMENTS) instanceof DefaultInnateItemEnchantmentsComponent component) {
                Optional<ComponentMap> baked = component.bake(manager);

                // only perform operations if component baking was successful
                if (baked.isPresent()) {
                    // create a new component map with all the old components + the new one, then commit it to the item.
                    ((ItemAccessor) item).klaxon$setComponentMap(ComponentMap.builder().addAll(original).addAll(baked.get()).build());
                    operationsPerformed++;
                }
            }
        }

        return operationsPerformed;
    }

    // we don't mirror this on the client because it's not necessary - also it caused an issue with the stored registryentries of default innate enchantments resetting everytime the creative inventory screen was opened... super fucked...
    public static void onServerStarted(MinecraftServer minecraftServer) {
        KlaxonCommon.LOGGER.info("Baked Innate Enchantment Components on {} Items!", bakeAll(minecraftServer.getReloadableRegistries().getRegistryManager()));
    }

    // enchantments are datapackable, so do this
    public static void onDataPackReload(MinecraftServer minecraftServer, LifecycledResourceManager lifecycledResourceManager, boolean success) {
        if (success) KlaxonCommon.LOGGER.info("Reloaded Innate Enchantment Components on {} Items!", bakeAll(minecraftServer.getReloadableRegistries().getRegistryManager()));
    }
}
