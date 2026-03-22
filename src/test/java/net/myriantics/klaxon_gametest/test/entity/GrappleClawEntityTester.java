package net.myriantics.klaxon_gametest.test.entity;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawEntity;
import net.myriantics.klaxon.registry.entity.KlaxonEntityTypes;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon_gametest.util.KlaxonGameTestHelper;
import net.myriantics.klaxon_gametest.util.KlaxonTestPlayer;

public class GrappleClawEntityTester {
    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    public static void testGrappleClawFastReload(KlaxonGameTestHelper context) {
        GrappleClawEntity grappleClaw = context.spawn(KlaxonEntityTypes.GRAPPLE_CLAW.value(), new Vec3(3, 0, 3));
        KlaxonTestPlayer player = context.createFakePlayer(GameType.SURVIVAL);
        player.lookAt(EntityAnchorArgument.Anchor.EYES, grappleClaw.getEyePosition());
        context.getLevel().addFreshEntity(player);

        context.onEachTick(() -> {
            if (grappleClaw.klaxon$isAnchored()) {
                player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(KlaxonItems.GRAPPLE_WINCH));
                player.attack(grappleClaw);

                context.assertEntityNotPresent(KlaxonEntityTypes.GRAPPLE_CLAW.value());
                context.expectBoolean(false, player.getItemInHand(InteractionHand.MAIN_HAND).getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY).isEmpty(), "Grapple Winch Charged Projectiles empty when it shouldn't be!");

                player.kill();
                context.succeed();
            }
        });
    }
}
