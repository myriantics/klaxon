package net.myriantics.klaxon.mixin.minecraft.machine_part;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.myriantics.klaxon.mechanics.machinery.part.KlaxonDynamicMachinePartLoader;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin implements KlaxonDynamicMachinePartLoader.Access {

    @Shadow
    public abstract @NotNull MinecraftServer getServer();

    @Override
    public KlaxonDynamicMachinePartLoader klaxon$getDynamicMachinePartLoader() {
        return ((KlaxonDynamicMachinePartLoader.Access) this.getServer()).klaxon$getDynamicMachinePartLoader();
    }
}
