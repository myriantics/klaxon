package net.myriantics.klaxon.mechanics.explosive_catalyst.context;

import net.minecraft.resources.ResourceLocation;

public class ExplosiveCatalystContextParam<T> {
    private final ResourceLocation name;

    public ExplosiveCatalystContextParam(ResourceLocation name) {
        this.name = name;
    }

    public ResourceLocation getName() {
        return this.name;
    }
}
