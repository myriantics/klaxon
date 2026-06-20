package net.myriantics.klaxon.mechanics.logistics.itemduct.network;

import net.myriantics.klaxon.mechanics.logistics.itemduct.DuctNode;

import java.util.ArrayList;

public class DuctNetwork {
    private final int id;
    private final ArrayList<DuctNetwork> endpoints;

    public DuctNetwork(int id) {
        this.id = id;
    }

    public void tick() {
        return;
    }

    public boolean isInvalid() {
        return false;
    }
}
