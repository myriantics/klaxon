package net.myriantics.klaxon_gametest;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.mixin.gametest.TestFunctionsMixin;
import net.minecraft.gametest.framework.GameTestRegistry;
import net.myriantics.klaxon_gametest.test.KlaxonBlockTesters;
import net.myriantics.klaxon_gametest.test.KlaxonEntityTesters;
import net.myriantics.klaxon_gametest.test.KlaxonItemTesters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class KlaxonGameTester implements ModInitializer {
    public static final String MOD_ID = "klaxon_gametest";
    public static final ArrayList<Class<?>> TEST_CLASSES = new ArrayList<>();
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        KlaxonBlockTesters.init();
        KlaxonEntityTesters.init();
        KlaxonItemTesters.init();

        for (Class<?> clazz : TEST_CLASSES) {
            GameTestRegistry.register(clazz);
        }
        LOGGER.info("Initialized KLAXON's Game Tests!");
    }

    public static void register(Class<?> clazz) {
        if (TEST_CLASSES.contains(clazz)) {
            throw new UnsupportedOperationException("Test class (%s) has already been registered with mod (%s)".formatted(clazz.getCanonicalName(), MOD_ID));
        }

        TEST_CLASSES.add(clazz);
    }
}
