package net.myriantics.klaxon.mixin.minecraft.machine_part;

import net.minecraft.server.MinecraftServer;
import net.myriantics.klaxon.mechanics.machinery.part.KlaxonDynamicMachinePartLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements KlaxonDynamicMachinePartLoader.Access {

    @Shadow
    private MinecraftServer.ReloadableResources resources;

    @Override
    public KlaxonDynamicMachinePartLoader klaxon$getDynamicMachinePartLoader() {
        return ((KlaxonDynamicMachinePartLoader.Access) this.resources.managers()).klaxon$getDynamicMachinePartLoader();
    }
}
