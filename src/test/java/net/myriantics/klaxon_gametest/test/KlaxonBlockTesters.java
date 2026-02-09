package net.myriantics.klaxon_gametest.test;

import net.myriantics.klaxon_gametest.KlaxonGameTester;
import net.myriantics.klaxon_gametest.test.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockTester;

public abstract class KlaxonBlockTesters {
    public static void init() {
        KlaxonGameTester.register(DeepslateBlastProcessorBlockTester.class);

        KlaxonGameTester.LOGGER.info("Registered KLAXON Game Tester's Block Testers!");
    }
}
