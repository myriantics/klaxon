package net.myriantics.klaxon.mechanics.configuration;

import com.mojang.serialization.MapCodec;

public record MachineConfigurationType<T extends MachineConfiguration>(MapCodec<T> codec) {
}
