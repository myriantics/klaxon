package net.myriantics.klaxon.mechanics.turbine_generator.boost;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.myriantics.klaxon.block.machines.energy.generators.turbine.TurbineGeneratorBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class TurbineGeneratorBoostManager {
    private final TurbineGeneratorBlockEntity blockEntity;
    private @Nullable TurbineGeneratorBoostInstance dynamicPowerSource;
    private final Map<ResourceLocation, TurbineGeneratorBoostInstance> boostInstanceMap = new HashMap<>();

    public TurbineGeneratorBoostManager(TurbineGeneratorBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    public long processBoosts() {
        return 0;
    }

    public void applyOrRefreshBoost(ResourceLocation id, TurbineGeneratorBoostSupplier supplier, boolean overrideDynamicPowerSource) {
        if (overrideDynamicPowerSource) {
            if (this.dynamicPowerSource == null) {
                this.dynamicPowerSource = supplier.create();
            } else {
                // this.dynamicPowerSource.refresh();
            }
        } else {
            @Nullable TurbineGeneratorBoostInstance boostInstance = this.boostInstanceMap.get(id);
            if (boostInstance == null) {

            }
        }
    }

    public interface TurbineGeneratorBoostSupplier {
        TurbineGeneratorBoostInstance create();
    }
}
