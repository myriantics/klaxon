package net.myriantics.klaxon_gametest.test;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.gametest.framework.GameTest;
import net.myriantics.klaxon_gametest.util.KlaxonGameTestHelper;

public class AlwaysFailingTest {
    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    public static void fail(KlaxonGameTestHelper helper) {
        helper.fail("FAILED because TESTING");
    }
}
