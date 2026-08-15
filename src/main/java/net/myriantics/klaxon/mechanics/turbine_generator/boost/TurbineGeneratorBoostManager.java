package net.myriantics.klaxon.mechanics.turbine_generator.boost;

import net.minecraft.resources.ResourceLocation;
import net.myriantics.klaxon.block.machines.energy.generators.turbine.TurbineGeneratorBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public final class TurbineGeneratorBoostManager {
    private final TurbineGeneratorBlockEntity blockEntity;
    private final HashMap<ResourceLocation, TurbineGeneratorBoostInstance.AddToBase> additionBoostMap = new HashMap<>();
    private final HashMap<ResourceLocation, TurbineGeneratorBoostInstance.MultiplyFinal> multiplicationBoostMap = new HashMap<>();
    private long addedBoostCache = 0;
    private double boostMultiplierCache = 1;

    public TurbineGeneratorBoostManager(TurbineGeneratorBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    public void tick() {
        boolean additionRemovalPerformed = false;
        for (ResourceLocation rl : this.additionBoostMap.keySet()) {
            if (!this.additionBoostMap.get(rl).survivesDecay()) {
                this.additionBoostMap.remove(rl);
                additionRemovalPerformed = true;
            }
        }
        boolean multiplicationRemovalPerformed = false;
        for (ResourceLocation rl : this.multiplicationBoostMap.keySet()) {
            if (!this.multiplicationBoostMap.get(rl).survivesDecay()) {
                this.multiplicationBoostMap.remove(rl);
                multiplicationRemovalPerformed = true;
            }
        }
        if (additionRemovalPerformed) {
            this.recomputeAdditionCache();
        }
        if (multiplicationRemovalPerformed) {
            this.recomputeMultiplicationCache();
        }
    }

    public long modify(long base) {
        return (long) ((base + this.addedBoostCache) * this.boostMultiplierCache);
    }

    private void recomputeAdditionCache() {
        long total = 0;
        for (TurbineGeneratorBoostInstance.AddToBase addToBase : this.additionBoostMap.values()) {
            total += addToBase.getAddedValue();
        }
        this.addedBoostCache = total;
    }

    private void recomputeMultiplicationCache() {
        double total = 1;
        for (TurbineGeneratorBoostInstance.MultiplyFinal multiplyFinal : this.multiplicationBoostMap.values()) {
            total *= multiplyFinal.getMultiplyValue();
        }
        this.boostMultiplierCache = total;
    }

    public void additionBoost(ResourceLocation boostRl, long boostAmount, int boostDuration) {
        @Nullable TurbineGeneratorBoostInstance.AddToBase existing = this.additionBoostMap.get(boostRl);
        if (existing == null || existing.getAddedValue() != boostAmount) {
            this.additionBoostMap.put(boostRl, new TurbineGeneratorBoostInstance.AddToBase(boostDuration, boostAmount));
            this.recomputeAdditionCache();
        } else {
            existing.refresh(boostDuration);
        }
    }

    public void multiplicationBoost(ResourceLocation boostRl, double boostMultiplier, int boostDuration) {
        @Nullable TurbineGeneratorBoostInstance.MultiplyFinal existing = this.multiplicationBoostMap.get(boostRl);
        if (existing == null || existing.getMultiplyValue() != boostMultiplier) {
            this.multiplicationBoostMap.put(boostRl, new TurbineGeneratorBoostInstance.MultiplyFinal(boostDuration, boostMultiplier));
            this.recomputeMultiplicationCache();
        } else {
            existing.refresh(boostDuration);
        }
    }
}
