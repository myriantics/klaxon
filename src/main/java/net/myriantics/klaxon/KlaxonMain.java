package net.myriantics.klaxon;

import net.fabricmc.api.ModInitializer;

import net.myriantics.klaxon.block.ModBlocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KlaxonMain implements ModInitializer {
	public static final String MOD_ID = "klaxon";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		ModBlocks.registerModBlocks();

		LOGGER.info("Klaxon is loading ... or has loaded ... i dont know when this is run exactly and i will prolly forget to change this");
	}
}