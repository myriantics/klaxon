package net.myriantics.klaxon;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.datagen.KlaxonDatagenPhantomItems;
import net.myriantics.klaxon.item.equipment.armor.KlaxonArmorMaterials;
import net.myriantics.klaxon.registry.KlaxonRegistries;
import net.myriantics.klaxon.registry.KlaxonRegistryKeys;
import net.myriantics.klaxon.registry.custom.KlaxonBlastProcessorCatalystBehaviors;
import net.myriantics.klaxon.registry.custom.KlaxonEventListeners;
import net.myriantics.klaxon.registry.minecraft.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KlaxonCommon implements ModInitializer {
	public static final String MOD_ID = "klaxon";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Identifier locate(String name) {
		return Identifier.of(MOD_ID, name);
	}

	@Override
	public void onInitialize() {
		KlaxonBlocks.registerModBlocks();
		KlaxonBlockEntities.registerBlockEntities();
		KlaxonArmorMaterials.registerArmorMaterials();
		KlaxonItems.registerModItems();

		// if it's datagen, run my hacky hack of hacks
		if (isDatagenEnabled()) {
			KlaxonDatagenPhantomItems.registerPhantomItemsForDatagen();
		}

		// KlaxonEntities.registerModEntities();
		KlaxonRegistryKeys.registerRegistryKeys();
		KlaxonRegistries.registerRegistries();
		KlaxonRecipeTypes.registerRecipeTypes();
		KlaxonDamageTypes.registerModDamageTypes();
		KlaxonPackets.registerModPackets();
		KlaxonPackets.registerC2SPacketRecievers();
		KlaxonScreenHandlers.registerModScreenHandlers();
		KlaxonItemGroups.registerKlaxonItemGroups();
		KlaxonBlastProcessorCatalystBehaviors.registerBlastProcessorBehaviors();
		KlaxonDataComponentTypes.registerKlaxonComponents();
		KlaxonAdvancementCriteria.registerAdvancementCriteria();
		KlaxonStatusEffects.registerStatusEffects();
		KlaxonToolMaterials.registerToolMaterials();
		KlaxonGamerules.registerGamerules();
		KlaxonFuelRegistry.registerFuelItems();
		KlaxonOxidationRegistry.registerOxidationStages();
		KlaxonEventListeners.registerListeners();
		KlaxonDispenserBehaviors.registerDispenserBehaviors();

		LOGGER.info("KLAXON has loaded!");
	}

	public boolean isDatagenEnabled() {
		return FabricDataGenHelper.ENABLED;
	}
}