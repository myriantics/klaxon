package net.myriantics.klaxon.mechanics.logistics.itemduct.network;

import net.minecraft.server.level.ServerLevel;

import java.util.ArrayList;

public class DuctNetworkManager {
    private final ServerLevel serverLevel;
    private final ArrayList<DuctNetwork> ductNetworks = new ArrayList<>();
    private int latestNetworkId = 0;

    public DuctNetworkManager(ServerLevel serverLevel) {
        this.serverLevel = serverLevel;
    }

    public void tick() {
        this.ductNetworks.removeIf(DuctNetwork::isInvalid);
    }

    public interface Access {
        DuctNetworkManager klaxon$getDuctNetworkManager(ServerLevel serverLevel);
    }
}
