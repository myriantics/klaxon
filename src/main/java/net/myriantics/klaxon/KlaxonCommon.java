package net.myriantics.klaxon;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.block.KlaxonBlockEntities;
import net.myriantics.klaxon.block.KlaxonBlocks;
import net.myriantics.klaxon.block.blockentities.blast_processor.BlastProcessorScreenHandler;
import net.myriantics.klaxon.entity.KlaxonEntities;
import net.myriantics.klaxon.item.KlaxonItems;
import net.myriantics.klaxon.recipes.KlaxonRecipeTypes;
import net.myriantics.klaxon.util.KlaxonDamageTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KlaxonCommon implements ModInitializer {
	public static final String MOD_ID = "klaxon";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Identifier locate(String name) {
		return new Identifier(MOD_ID, name);
	}

	public static final ExtendedScreenHandlerType<BlastProcessorScreenHandler> BLAST_PROCESSOR_SCREEN_HANDLER = new ExtendedScreenHandlerType<>(BlastProcessorScreenHandler::new);

	static {
		Registry.register(Registries.SCREEN_HANDLER, locate("blast_processor"), BLAST_PROCESSOR_SCREEN_HANDLER);
	}

	@Override
	public void onInitialize() {

		KlaxonBlocks.registerModBlocks();
		KlaxonBlockEntities.registerBlockEntities();
		KlaxonItems.registerModItems();
		KlaxonEntities.registerModEntities();
		KlaxonRecipeTypes.registerSerializer();
		KlaxonDamageTypes.registerModDamageTypes();

		LOGGER.info("KLAXON has loaded");
	}
}