package net.myriantics.klaxon.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorScreenHandler;
import net.myriantics.klaxon.mechanics.grapple_winch.ClientGrappleWinchConnection;
import net.myriantics.klaxon.mechanics.grapple_winch.ClientGrappleWinchConnectionManager;
import net.myriantics.klaxon.mechanics.item_usage_lockout.MinecraftClientUsageLockoutAccess;
import net.myriantics.klaxon.networking.s2c.*;
import net.myriantics.klaxon.registry.misc.KlaxonWorldEvents;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public abstract class KlaxonClientPlayNetworkHandler {
    public static void send(CustomPacketPayload customPayload) {
        ClientPlayNetworking.send(customPayload);
    }

    public static void processKlaxonWorldEvent(KlaxonWorldEventPacket packet, ClientPlayNetworking.Context context) {
        ClientLevel clientWorld = Minecraft.getInstance().level;
        if (clientWorld == null) return;

        int eventId = packet.eventId();
        int data = packet.data();
        Vector3f pos = packet.position();
        BlockPos blockPos = BlockPos.containing(pos.x, pos.y, pos.z);

        LevelRenderer renderer = Minecraft.getInstance().levelRenderer;

        RandomSource random = clientWorld.getRandom();

        switch (eventId) {
            case KlaxonWorldEvents.DRAGONS_BREATH_EXPLOSIVE_CATALYST_CLOUD_SPAWNS -> {
                for (int m = 0; m < 200; m++) {
                    float ab = random.nextFloat() * 4.0F;
                    float ag = random.nextFloat() * (float) (Math.PI * 2);
                    double n = Mth.cos(ag) * ab;
                    double o = 0.01 + random.nextDouble() * 0.5;
                    double p = Mth.sin(ag) * ab;
                    renderer.addParticle(ParticleTypes.DRAGON_BREATH, false, pos.x() + n * 0.1, pos.y() + 0.3, pos.z() + p * 0.1, n, o, p);
                }

                if (data == 1) {
                    clientWorld.playLocalSound(blockPos, SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.HOSTILE, 1.0F, random.nextFloat() * 0.1F + 0.9F, false);
                }
            }
            case KlaxonWorldEvents.SPAWN_BLOCK_BREAK_PARTICLES -> {
                clientWorld.addDestroyBlockEffect(blockPos, clientWorld.getBlockState(blockPos));
            }
        }
    }

    public static void blastProcessorScreenSync(BlastProcessorScreenSyncPacket packet, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            Minecraft client = context.client();

            if (client.player != null && client.player.containerMenu instanceof DeepslateBlastProcessorScreenHandler screenHandler) {
                screenHandler.setRecipeData(
                        packet.explosionPower(),
                        packet.explosionPowerMin(),
                        packet.explosionPowerMax(),
                        packet.producesFire()
                );
            }
        });
    }

    public static void grappleWinchConnectionSync(GrappleWinchConnectionSyncPacket packet, ClientPlayNetworking.Context context) {
        Minecraft client = context.client();

        client.execute(() -> {
            if (client.level != null) {
                ClientGrappleWinchConnectionManager manager = ClientGrappleWinchConnectionManager.get(client.level);
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
        Minecraft client = context.client();

        client.execute(() -> {
            if (client.level != null) {
                ClientGrappleWinchConnectionManager manager = ClientGrappleWinchConnectionManager.get(client.level);
                manager.disconnect(packet.connectionId(), packet.reason());
                manager.resetTicksSinceUpdated();
            }
        });
    }

    public static void triggerItemUsageLockout(ItemUsageLockoutTrigger packet, ClientPlayNetworking.Context context) {
        Minecraft client = context.client();

        client.execute(() -> {
            ((MinecraftClientUsageLockoutAccess) client).klaxon$setUsageLockout(true);
            if (client.player != null) {
                client.player.stopUsingItem();
            }
        });
    }
}
