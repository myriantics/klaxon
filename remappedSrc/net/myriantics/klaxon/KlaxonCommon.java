package net.myriantics.klaxon;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.registry.KlaxonAdvancementCriteria;
import net.myriantics.klaxon.api.behavior.BlastProcessorBehavior;
import net.myriantics.klaxon.registry.KlaxonBlockEntities;
import net.myriantics.klaxon.registry.KlaxonBlocks;
import net.myriantics.klaxon.datagen.KlaxonDatagenPhantomItems;
import net.myriantics.klaxon.registry.KlaxonEntities;
import net.myriantics.klaxon.registry.KlaxonStatusEffects;
import net.myriantics.klaxon.registry.KlaxonItemGroups;
import net.myriantics.klaxon.registry.KlaxonItems;
import net.myriantics.klaxon.registry.KlaxonToolMaterials;
import net.myriantics.klaxon.item.equipment.armor.KlaxonArmorMaterials;
import net.myriantics.klaxon.registry.KlaxonPackets;
import net.myriantics.klaxon.registry.KlaxonRecipeTypes;
import net.myriantics.klaxon.registry.KlaxonDamageTypes;
import net.myriantics.klaxon.registry.KlaxonScreenHandlers;
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

		KlaxonEntities.registerModEntities();
		KlaxonRecipeTypes.registerSerializer();
		KlaxonDamageTypes.registerModDamageTypes();
		KlaxonPackets.registerModPackets();
		KlaxonPackets.registerC2SPacketRecievers();
		KlaxonScreenHandlers.registerModScreenHandlers();
		KlaxonItemGroups.registerKlaxonItemGroups();
		BlastProcessorBehavior.registerBlastProcessorBehaviors();
		KlaxonAdvancementCriteria.registerAdvancementCriteria();
		KlaxonStatusEffects.registerStatusEffects();
		KlaxonToolMaterials.registerToolMaterials();

		LOGGER.info("KLAXON has loaded!");
	}

	public boolean isDatagenEnabled() {
		return FabricDataGenHelper.ENABLED;
	}
}