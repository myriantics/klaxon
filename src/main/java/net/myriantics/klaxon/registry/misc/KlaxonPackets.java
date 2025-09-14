package net.myriantics.klaxon.registry.misc;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorScreenHandler;
import net.myriantics.klaxon.client.GrappleWinchConnectionManager;
import net.myriantics.klaxon.component.ability.WalljumpAbilityComponent;
import net.myriantics.klaxon.entity.GrappleClawEntity;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.ClientPlayerEntityUsageAccess;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.MinecraftClientUsageLockoutAccess;
import net.myriantics.klaxon.networking.KlaxonClientPlayNetworkHandler;
import net.myriantics.klaxon.networking.s2c.*;
import net.myriantics.klaxon.networking.c2s.EntityDualWieldToggleC2SPacket;
import net.myriantics.klaxon.networking.c2s.HammerWalljumpTriggerPacket;
import net.myriantics.klaxon.util.LivingEntityMixinAccess;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.GrappleWinchConnectionData;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.PlayerEntityGrappleAccess;

public abstract class KlaxonPackets {

    public static final Identifier GRAPPLE_WINCH_CONNECTION_SYNC_S2C_ID = locateS2C("grapple_winch_connection_sync");
    public static final Identifier GRAPPLE_WINCH_CONNECTION_DISCARD_S2C_ID = locateS2C("grapple_winch_connection_discard");
    public static final Identifier ITEM_USAGE_LOCKOUT_TRIGGER_S2C_ID = locateS2C("item_usage_lockout");
    public static final Identifier BLAST_PROCESSOR_SCREEN_SYNC_PACKET_S2C_ID = locateS2C("blast_processor_screen_sync");
    public static final Identifier KLAXON_WORLD_EVENT_TRIGGER_PACKET_S2C_ID = locateS2C("klaxon_world_event");
    public static final Identifier HAMMER_WALLJUMP_TRIGGER_PACKET_C2S_ID = locateC2S("hammer_walljump_trigger_packet");
    public static final Identifier DUAL_WIELD_TOGGLE_BIDIRECTIONAL_PACKET = locateBidirectional("dual_wield_toggle");

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Packets!");

        // s2c
        PayloadTypeRegistry.playS2C().register(BlastProcessorScreenSyncPacket.ID, BlastProcessorScreenSyncPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(EntityDualWieldToggleS2CPacket.ID, EntityDualWieldToggleS2CPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(KlaxonWorldEventPacket.ID, KlaxonWorldEventPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(GrappleWinchConnectionSyncPacket.ID, GrappleWinchConnectionSyncPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(GrappleWinchConnectionDiscardPacket.ID, GrappleWinchConnectionDiscardPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(ItemUsageLockoutTrigger.ID, ItemUsageLockoutTrigger.PACKET_CODEC);

        // c2s
        PayloadTypeRegistry.playC2S().register(HammerWalljumpTriggerPacket.ID, HammerWalljumpTriggerPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(EntityDualWieldToggleC2SPacket.ID, EntityDualWieldToggleC2SPacket.PACKET_CODEC);
    }

    // client only
    public static void registerS2CPacketRecievers() {
        ClientPlayNetworking.registerGlobalReceiver(BlastProcessorScreenSyncPacket.ID, ((payload, context) -> {
            context.client().execute(() -> {
                MinecraftClient client = context.client();

                if (client.player != null && client.player.currentScreenHandler instanceof DeepslateBlastProcessorScreenHandler screenHandler) {
                    screenHandler.setRecipeData(
                            payload.explosionPower(),
                            payload.explosionPowerMin(),
                            payload.explosionPowerMax(),
                            payload.producesFire()
                    );
                }
            });
        }));

        ClientPlayNetworking.registerGlobalReceiver(EntityDualWieldToggleS2CPacket.ID, ((payload, context) -> {
            context.client().execute(() -> {
                MinecraftClient client = context.client();

                if (client.world != null && client.world.getEntityById(payload.entityId()) instanceof LivingEntityMixinAccess access) {
                    access.klaxon$setDualWielding(payload.isDualWielding());
                }
            });
        }));

        ClientPlayNetworking.registerGlobalReceiver(KlaxonWorldEventPacket.ID, (((payload, context) -> {
            context.client().execute(() -> {
                WorldEventS2CPacket packet = payload.packet();
                KlaxonClientPlayNetworkHandler.processKlaxonWorldEvent(packet.getEventId(), packet.getPos(), packet.getData());
            });
        })));

        ClientPlayNetworking.registerGlobalReceiver(GrappleWinchConnectionSyncPacket.ID, ((payload, context) -> {
            MinecraftClient client = context.client();

            client.execute(() -> {
                GrappleWinchConnectionData connectionData = payload.connectionData();

                if (MinecraftClient.getInstance().world instanceof ClientWorld clientWorld) {
                    Entity potentialPlayer = clientWorld.getEntityById(connectionData.playerId());
                    Entity potentialClaw = clientWorld.getEntityById(connectionData.clawId());

                    if (potentialPlayer instanceof AbstractClientPlayerEntity player) {
                        GrappleWinchConnectionManager.INSTANCE.addConnection(
                                connectionData.playerId(),
                                connectionData.clawId(),
                                player,
                                (GrappleClawEntity) potentialClaw,
                                connectionData.playerPos(),
                                connectionData.clawPos());

                        // update access shi
                        PlayerEntityGrappleAccess access = (PlayerEntityGrappleAccess) player;
                        access.klaxon$setWinchConnectionData(connectionData);
                        access.klaxon$resetWinchCableLength();
                    } else if (potentialClaw instanceof GrappleClawEntity grappleClaw) {
                        GrappleWinchConnectionManager.INSTANCE.addConnection(
                                connectionData.playerId(),
                                connectionData.clawId(),
                                null,
                                grappleClaw,
                                connectionData.playerPos(),
                                connectionData.clawPos());
                    } else {
                        // cancel other operations if client world doesn't have either entity loaded
                        return;
                    }
                }
            });
        }));

        ClientPlayNetworking.registerGlobalReceiver(GrappleWinchConnectionDiscardPacket.ID, ((payload, context) -> {
            MinecraftClient client = context.client();

            client.execute(() -> {
                GrappleWinchConnectionManager.INSTANCE.discardConnection(payload.playerId());
                if (client.world instanceof ClientWorld world && world.getEntityById(payload.playerId()) instanceof PlayerEntityGrappleAccess access) {
                    access.klaxon$setWinchConnectionData(null);
                    access.klaxon$resetWinchCableLength();
                }
            });
        }));

        ClientPlayNetworking.registerGlobalReceiver(ItemUsageLockoutTrigger.ID, ((payload, context) -> {
            MinecraftClient client = context.client();

            client.execute(() -> {
                if (client instanceof MinecraftClientUsageLockoutAccess access) {
                    access.klaxon$setUsageLockout(true);
                }
                if (client.player instanceof ClientPlayerEntityUsageAccess access) {
                    access.klaxon$setUsingItem(false);
                }
            });
        }));
    }

    // server only
    public static void initC2SRecievers() {
        ServerPlayNetworking.registerGlobalReceiver(HammerWalljumpTriggerPacket.ID, ((payload, context) -> {
            context.server().execute(() -> {
                ServerPlayerEntity player = context.player();

                WalljumpAbilityComponent component = WalljumpAbilityComponent.get(player.getMainHandStack());

                if (component != null) {
                    // run the walljump ability :D
                    component.processHammerWalljump(player, player.getWorld(), payload.pos(), payload.direction());
                }
            });
        }));

        ServerPlayNetworking.registerGlobalReceiver(EntityDualWieldToggleC2SPacket.ID, ((payload, context) -> {
            context.server().execute(() -> {
                ServerPlayerEntity player = context.player();

                if (player instanceof LivingEntityMixinAccess access) {
                    access.klaxon$setDualWielding(payload.isDualWielding());
                }
            });
        }));
    }

    private static Identifier locateS2C(String name) {
        return KlaxonCommon.locate(name + "_s2c");
    }

    private static Identifier locateC2S(String name) {
        return KlaxonCommon.locate(name + "_c2s");
    }

    private static Identifier locateBidirectional(String name) {
        return KlaxonCommon.locate(name + "_bidirectional");
    }
}
