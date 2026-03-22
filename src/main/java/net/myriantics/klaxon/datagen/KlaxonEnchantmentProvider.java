package net.myriantics.klaxon.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.registry.dynamic.KlaxonEnchantments;
import net.myriantics.klaxon.registry.item.KlaxonEnchantmentEffectComponentTypes;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

import java.util.concurrent.CompletableFuture;

public class KlaxonEnchantmentProvider extends FabricDynamicRegistryProvider {
    public KlaxonEnchantmentProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        entries.addAll(registries.lookupOrThrow(Registries.ENCHANTMENT));
    }

    public static void bootstrap(BootstrapContext<Enchantment> registry) {
        HolderGetter<Item> itemLookup = registry.lookup(Registries.ITEM);

        registry.register(
                KlaxonEnchantments.STREAMLINE,
                Enchantment.enchantment(
                        Enchantment.definition(
                                itemLookup.getOrThrow(KlaxonItemTags.STREAMLINE_ENCHANTABLE),
                                2,
                                1,
                                Enchantment.dynamicCost(25, 25),
                                Enchantment.dynamicCost(75, 25),
                                4,
                                EquipmentSlotGroup.ARMOR
                        )
                )
                        .withEffect(
                                KlaxonEnchantmentEffectComponentTypes.CANCEL_CERTAIN_VELOCITY_UPDATES.value()
                        ).build(KlaxonEnchantments.STREAMLINE.location())
        );
    }

    @Override
    public String getName() {
        return KlaxonCommon.locateAlt("enchantment_provider");
    }
}
