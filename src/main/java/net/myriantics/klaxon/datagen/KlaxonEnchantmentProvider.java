package net.myriantics.klaxon.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.registry.dynamic.KlaxonEnchantments;
import net.myriantics.klaxon.registry.item.KlaxonEnchantmentEffectComponentTypes;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

import java.util.concurrent.CompletableFuture;

public class KlaxonEnchantmentProvider extends FabricDynamicRegistryProvider {
    public KlaxonEnchantmentProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        entries.addAll(registries.getWrapperOrThrow(RegistryKeys.ENCHANTMENT));
    }

    public static void bootstrap(Registerable<Enchantment> registry) {
        RegistryEntryLookup<Item> itemLookup = registry.getRegistryLookup(RegistryKeys.ITEM);

        registry.register(
                KlaxonEnchantments.STREAMLINE,
                Enchantment.builder(
                        Enchantment.definition(
                                itemLookup.getOrThrow(KlaxonItemTags.STREAMLINE_ENCHANTABLE),
                                2,
                                1,
                                Enchantment.leveledCost(25, 25),
                                Enchantment.leveledCost(75, 25),
                                4,
                                AttributeModifierSlot.ARMOR
                        )
                )
                        .addEffect(
                                KlaxonEnchantmentEffectComponentTypes.CANCEL_CERTAIN_VELOCITY_UPDATES
                        ).build(KlaxonEnchantments.STREAMLINE.getValue())
        );
    }

    @Override
    public String getName() {
        return KlaxonCommon.locateAlt("enchantment_provider");
    }
}
