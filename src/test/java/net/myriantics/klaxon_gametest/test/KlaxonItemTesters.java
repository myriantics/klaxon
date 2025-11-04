package net.myriantics.klaxon_gametest.test;

import net.myriantics.klaxon_gametest.KlaxonGameTester;
import net.myriantics.klaxon_gametest.test.item.equipment.tools.WrenchItemTester;

public abstract class KlaxonItemTesters {
    public static void init() {
        KlaxonGameTester.register(WrenchItemTester.class);

        KlaxonGameTester.LOGGER.info("Registered KLAXON Game Tester's Item Testers!");
    }
}
