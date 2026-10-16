package net.myriantics.klaxon.mixin.minecraft.reloadable_registries.network;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.Holder;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import net.myriantics.klaxon.infra.KlaxonResourceReloadListener;
import net.myriantics.klaxon.infra.ReloadableRegistryInstance;
import net.myriantics.klaxon.infra.ClientSynced;
import net.myriantics.klaxon.networking.KlaxonServerPlayNetworkHandler;
import net.myriantics.klaxon.registry.KlaxonBuiltInRegistries;
import net.myriantics.klaxon.registry.KlaxonReloadableRegistries;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
    @Shadow
    @Final
    private MinecraftServer server;

    @Shadow
    @Final
    private List<ServerPlayer> players;

    @Inject(
            method = "placeNewPlayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/protocol/game/ClientboundUpdateRecipesPacket;<init>(Ljava/util/Collection;)V",
                    ordinal = 0
            )
    )
    private void klaxon$syncReloadableRegistriesOnPlayerJoin(Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci) {
        for (CustomPacketPayload payload : this.klaxon$gatherSyncPackets()) {
            KlaxonServerPlayNetworkHandler.send(player, payload);
        }
    }

    @Inject(
            method = "reloadResources",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/protocol/game/ClientboundUpdateRecipesPacket;<init>(Ljava/util/Collection;)V"
            )
    )
    private void klaxon$initPayloadsForResourceReload(CallbackInfo ci) {
        List<CustomPacketPayload> payloads = this.klaxon$gatherSyncPackets();
        payloads.forEach(payload -> this.players.forEach(player -> KlaxonServerPlayNetworkHandler.send(player, payload)));
    }

    @SuppressWarnings("unchecked")
    @Unique
    private List<CustomPacketPayload> klaxon$gatherSyncPackets() {
        ImmutableList.Builder<CustomPacketPayload> builder = ImmutableList.builderWithExpectedSize(KlaxonBuiltInRegistries.RELOADABLE_REGISTRY.size());

        for (ReloadableRegistryInstance<? extends KlaxonResourceReloadListener<?>, ?, ?> instance : KlaxonBuiltInRegistries.RELOADABLE_REGISTRY) {
            KlaxonResourceReloadListener<?> listener = ((KlaxonReloadableRegistries.Access) this.server).klaxon$getRegistry(
                    (Holder<ReloadableRegistryInstance<KlaxonResourceReloadListener<Object>, Object, CustomPacketPayload>>) (Object) instance.asHolder()
            );

            if (listener instanceof ClientSynced<?> clientSynced) {
                builder.add(clientSynced.getSyncPacket());
            }
        }

        return builder.build();
    }
}
