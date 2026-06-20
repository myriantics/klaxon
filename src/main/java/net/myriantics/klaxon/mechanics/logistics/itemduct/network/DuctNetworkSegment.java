package net.myriantics.klaxon.mechanics.logistics.itemduct.network;

import net.myriantics.klaxon.mechanics.logistics.itemduct.DuctNode;

public class DuctNetworkSegment {
    private final DuctNetworkSegment next
    private final DuctNode endpoint;
    private final DuctNode[] middleNodes;
    private final DuctNode entrypoint;

    private float velocity = 0;

    public DuctNetworkSegment(DuctNode endpoint, DuctNode[] middleNodes, DuctNode entrypoint) {
        this.endpoint = endpoint;
        this.middleNodes = middleNodes;
        this.entrypoint = entrypoint;
    }


    public void tick() {
        if (this.endpoint.push()) {
            DuctNode last = this.endpoint;
            for (DuctNode node : this.middleNodes) {
                last.setPayload(node.getPayload());
                last = node;
            }
        }
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }
}
