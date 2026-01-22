package net.myriantics.klaxon_gametest.test.entity;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.test.GameTest;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawEntity;
import net.myriantics.klaxon.registry.entity.KlaxonEntityTypes;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon_gametest.util.KlaxonTestContext;
import net.myriantics.klaxon_gametest.util.KlaxonTestPlayer;

public class GrappleClawEntityTester {
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void testGrappleClawFastReload(KlaxonTestContext context) {
        GrappleClawEntity grappleClaw = context.spawnEntity(KlaxonEntityTypes.GRAPPLE_CLAW, new Vec3d(3, 0, 3));
        KlaxonTestPlayer player = context.createFakePlayer(GameMode.SURVIVAL);
        player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, grappleClaw.getEyePos());
        context.getWorld().spawnEntity(player);

        context.runAtEveryTick(() -> {
            if (grappleClaw.klaxon$isAnchored()) {
                player.setStackInHand(Hand.MAIN_HAND, new ItemStack(KlaxonItems.GRAPPLE_WINCH));
                player.attack(grappleClaw);

                context.dontExpectEntity(KlaxonEntityTypes.GRAPPLE_CLAW);
                context.expectBoolean(false, player.getStackInHand(Hand.MAIN_HAND).getOrDefault(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.DEFAULT).isEmpty(), "Grapple Winch Charged Projectiles empty when it shouldn't be!");

                player.kill();
                context.complete();
            }
        });
    }
}
