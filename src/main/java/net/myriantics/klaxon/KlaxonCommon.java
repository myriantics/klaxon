package net.myriantics.klaxon;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;
import net.minecraft.resources.ResourceLocation;
import net.myriantics.klaxon.registry.*;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementCriteria;
import net.myriantics.klaxon.registry.behavior.KlaxonBlockStateWrenchBehaviors;
import net.myriantics.klaxon.registry.behavior.KlaxonCauldronBehaviors;
import net.myriantics.klaxon.registry.explosive_catalyst.KlaxonExplosiveCatalystBehaviors;
import net.myriantics.klaxon.registry.behavior.KlaxonDispenserBehaviors;
import net.myriantics.klaxon.registry.block.*;
import net.myriantics.klaxon.registry.dynamic.KlaxonDamageTypes;
import net.myriantics.klaxon.registry.entity.*;
import net.myriantics.klaxon.registry.explosive_catalyst.KlaxonExplosiveCatalystHandlers;
import net.myriantics.klaxon.registry.explosive_catalyst.KlaxonExplosiveCatalystTransformerTypes;
import net.myriantics.klaxon.registry.item.*;
import net.myriantics.klaxon.registry.misc.*;
import net.myriantics.klaxon.registry.worldgen.KlaxonSaplingGenerators;
import net.myriantics.klaxon.registry.worldgen.KlaxonWorldgenFeatures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KlaxonCommon implements ModInitializer {
	public static final String MOD_ID = "klaxon";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static ResourceLocation locate(String name) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
	}

    public static String locateAlt(String name) {
        return MOD_ID + ResourceLocation.NAMESPACE_SEPARATOR + name;
    }

    @Override
	public void onInitialize() {
        KlaxonEnchantmentEffectComponentTypes.init();
		KlaxonWorldgenFeatures.init();
		KlaxonSaplingGenerators.init();
		KlaxonBlocks.init();
		KlaxonBlockItems.init();
		KlaxonBlockEntityTypes.init();
		KlaxonItemStorages.init();
		KlaxonArmorMaterials.init();
		KlaxonItems.init();
		KlaxonRegistryKeys.init();
		KlaxonRegistries.init();
		KlaxonRecipeTypes.init();
		KlaxonDamageTypes.init();
		KlaxonPackets.init();
		KlaxonPackets.initC2SRecievers();
		KlaxonMenuTypes.init();
		KlaxonCreativeModeTabs.init();
		KlaxonExplosiveCatalystBehaviors.init();
		KlaxonDataComponentTypes.init();
		KlaxonAdvancementCriteria.init();
		KlaxonStatusEffects.init();
		KlaxonToolMaterials.init();
		KlaxonGameRules.init();
		KlaxonFuelRegistry.init();
		KlaxonOxidationRegistry.init();
		KlaxonEventListeners.init();
		KlaxonDispenserBehaviors.init();
		KlaxonStrippedBlocksRegistry.init();
		KlaxonCompostableRegistry.init();
		KlaxonParticleTypes.init();
		KlaxonEntityTypes.init();
        KlaxonEntityAttributes.init();
        KlaxonDynamicRegistries.init();
        KlaxonBlockStateWrenchBehaviors.init();
		KlaxonCauldronBehaviors.init();
		KlaxonItemUsageTweaks.init();
		KlaxonAttachmentTypes.init();
		KlaxonExplosiveCatalystHandlers.init();
		KlaxonExplosiveCatalystTransformerTypes.init();

		LOGGER.info("KLAXON has loaded!");
	}

	public boolean isDatagenEnabled() {
		return FabricDataGenHelper.ENABLED;
	}
}