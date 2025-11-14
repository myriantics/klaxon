package net.myriantics.klaxon.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.random.Random;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorScreenHandler;
import net.myriantics.klaxon.mechanics.dual_wielding.LivingEntityMixinAccess;
import net.myriantics.klaxon.mechanics.grapple_winch.ClientGrappleWinchConnection;
import net.myriantics.klaxon.mechanics.grapple_winch.ClientGrappleWinchConnectionManager;
import net.myriantics.klaxon.mechanics.item_usage_lockout.MinecraftClientUsageLockoutAccess;
import net.myriantics.klaxon.networking.s2c.*;
import net.myriantics.klaxon.registry.misc.KlaxonWorldEvents;
import org.jetbrains.annotations.Nullable;

public abstract class KlaxonClientPlayNetworkHandler {
    public static void send(CustomPayload customPayload) {
        ClientPlayNetworking.send(customPayload);
    }

    public static void processKlaxonWorldEvent(KlaxonWorldEventPacket packet, ClientPlayNetworking.Context context) {
        ClientWorld clientWorld = MinecraftClient.getInstance().world;
        if (clientWorld == null) return;

        int eventId = packet.packet().getEventId();
        int data = packet.packet().getData();
        BlockPos pos = packet.packet().getPos();

        WorldRenderer renderer = MinecraftClient.getInstance().worldRenderer;

        Random random = clientWorld.getRandom();

        switch (eventId) {
            case KlaxonWorldEvents.DRAGONS_BREATH_EXPLOSIVE_CATALYST_CLOUD_SPAWNS -> {
                BlockEntity entity = clientWorld.getBlockEntity(pos);
                if (entity instanceof DeepslateBlastProcessorBlockEntity deepslateBlastProcessorBlockEntity) {
                    Position outputPos = deepslateBlastProcessorBlockEntity.getExplosionOutputLocation(clientWorld.getBlockState(pos).get(DeepslateBlastProcessorBlock.HORIZONTAL_FACING));

                    for (int m = 0; m < 200; m++) {
                        float ab = random.nextFloat() * 4.0F;
                        float ag = random.nextFloat() * (float) (Math.PI * 2);
                        double n = MathHelper.cos(ag) * ab;
                        double o = 0.01 + random.nextDouble() * 0.5;
                        double p = MathHelper.sin(ag) * ab;
                        renderer.addParticle(ParticleTypes.DRAGON_BREATH, false, outputPos.getX() + n * 0.1, outputPos.getY() + 0.3, outputPos.getZ() + p * 0.1, n, o, p);
                    }

                    if (data == 1) {
                        clientWorld.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.HOSTILE, 1.0F, random.nextFloat() * 0.1F + 0.9F, false);
                    }
                }
            }
            case KlaxonWorldEvents.SPAWN_BLOCK_BREAK_PARTICLES -> {
                clientWorld.addBlockBreakParticles(pos, clientWorld.getBlockState(pos));
            }
        }
    }

    public static void blastProcessorScreenSync(BlastProcessorScreenSyncPacket packet, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            MinecraftClient client = context.client();

            if (client.player != null && client.player.currentScreenHandler instanceof DeepslateBlastProcessorScreenHandler screenHandler) {
                screenHandler.setRecipeData(
                        packet.explosionPower(),
                        packet.explosionPowerMin(),
                        packet.explosionPowerMax(),
                        packet.producesFire()
                );
            }
        });
    }

    public static void toggleDualWielding(EntityDualWieldToggleS2CPacket packet, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            MinecraftClient client = context.client();

            if (client.world != null && client.world.getEntityById(packet.entityId()) instanceof LivingEntityMixinAccess access) {
                access.klaxon$setDualWielding(packet.isDualWielding());
            }
        });
    }

    public static void grappleWinchConnectionSync(GrappleWinchConnectionSyncPacket packet, ClientPlayNetworking.Context context) {
        MinecraftClient client = context.client();

        client.execute(() -> {
            if (client.world instanceof ClientGrappleWinchConnectionManager.Access access) {
                ClientGrappleWinchConnectionManager manager = access.klaxon$get();
                @Nullable ClientGrappleWinchConnection connection = manager.fromConnectionId(packet.connectionId());
                if (connection == null) {
                    manager.connect(packet);
                } else {
                    connection.sync(packet);
                }
                manager.resetTicksSinceUpdated();
            }
        });
    }

    public static void grappleWinchConnectionDiscard(GrappleWinchConnectionDiscardPacket packet, ClientPlayNetworking.Context context) {
        MinecraftClient client = context.client();

        client.execute(() -> {
            if (client.world instanceof ClientGrappleWinchConnectionManager.Access access) {
                ClientGrappleWinchConnectionManager manager = access.klaxon$get();
                manager.disconnect(packet.connectionId(), packet.reason());
                manager.resetTicksSinceUpdated();
            }
        });
    }

    public static void triggerItemUsageLockout(ItemUsageLockoutTrigger packet, ClientPlayNetworking.Context context) {
        MinecraftClient client = context.client();

        client.execute(() -> {
            ((MinecraftClientUsageLockoutAccess) client).klaxon$setUsageLockout(true);
            if (client.player != null) {
                client.player.clearActiveItem();
            }
        });
    }
}
