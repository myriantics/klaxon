package net.myriantics.klaxon_gametest.test;

import net.myriantics.klaxon_gametest.KlaxonGameTester;
import net.myriantics.klaxon_gametest.test.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockTester;
import net.myriantics.klaxon_gametest.test.entity.GrappleClawEntityTester;

public abstract class KlaxonEntityTesters {
    public static void init() {
        KlaxonGameTester.register(GrappleClawEntityTester.class);

        KlaxonGameTester.LOGGER.info("Registered KLAXON Game Tester's Entity Testers!");
    }
}
