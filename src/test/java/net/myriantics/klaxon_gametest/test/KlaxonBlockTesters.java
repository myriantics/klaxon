package net.myriantics.klaxon_gametest.test;

import net.myriantics.klaxon_gametest.KlaxonGameTester;
import net.myriantics.klaxon_gametest.test.block.machines.blast_processor.DeepslateBlastProcessorBlockTester;
import net.myriantics.klaxon_gametest.test.block.machines.nether_reactor_core.NetherReactorCoreBlockTester;
import net.myriantics.klaxon_gametest.util.KlaxonGameTestHelper;

public abstract class KlaxonBlockTesters {
    public static void init() {
        register(DeepslateBlastProcessorBlockTester.class);
        register(NetherReactorCoreBlockTester.class);

        KlaxonGameTester.LOGGER.info("Registered KLAXON Game Tester's Block Testers!");
    }

    private static void register(Class<?> clazz) {
        KlaxonGameTester.register(clazz);
    }
}
