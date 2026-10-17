package net.myriantics.klaxon.mixin.minecraft.reloadable_registries.network;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import net.myriantics.klaxon.infra.reloadable_registry.ReloadableRegistryManager;
import net.myriantics.klaxon.networking.KlaxonServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

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
        this.klaxon$handleSync(payload -> KlaxonServerPlayNetworkHandler.send(player, payload));
    }

    @Inject(
            method = "reloadResources",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/protocol/game/ClientboundUpdateRecipesPacket;<init>(Ljava/util/Collection;)V"
            )
    )
    private void klaxon$initPayloadsForResourceReload(CallbackInfo ci) {
        this.klaxon$handleSync(payload -> this.players.forEach(player -> KlaxonServerPlayNetworkHandler.send(player, payload)));
    }

    @Unique
    private void klaxon$handleSync(Consumer<CustomPacketPayload> payloadConsumer) {
        Iterator<? extends ReloadableRegistryManager.SyncInstance<? extends CustomPacketPayload>> iterator = Objects.requireNonNull(ReloadableRegistryManager.get(this.server)).syncIterator();
        while (iterator.hasNext()) {
            payloadConsumer.accept(iterator.next().synchronizer().getSyncPacket());
        }
    }
}
