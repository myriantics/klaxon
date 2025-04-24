package net.myriantics.klaxon.datagen.custom_providers;

import com.google.common.collect.Sets;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public abstract class KlaxonDamageTypeProvider implements DataProvider {
    protected final DataOutput.PathResolver pathResolver;
    private final CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture;

    public KlaxonDamageTypeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        this.pathResolver = output.getResolver(RegistryKeys.DAMAGE_TYPE);
        this.registryLookupFuture = completableFuture;
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        return this.registryLookupFuture.thenCompose(registryLookup -> this.run(writer, registryLookup));
    }

    private CompletableFuture<?> run(DataWriter writer, RegistryWrapper.WrapperLookup registryLookup) {
        final Set<Identifier> spentIds = Sets.newHashSet();
        final List<CompletableFuture<?>> list = new ArrayList<>();

        final Map<Identifier, DamageType> damageTypeMap = this.generate(new HashMap<>());
        for (Identifier id : damageTypeMap.keySet()) {
            DamageType type = damageTypeMap.get(id);
            if (!spentIds.add(id)) {
                throw new IllegalStateException("Duplicate Damage Type ID");
            } else {
                list.add(DataProvider.writeCodecToPath(writer, registryLookup, DamageType.CODEC, type, this.pathResolver.resolveJson(id)));
            }
        }

        return CompletableFuture.allOf(list.toArray(CompletableFuture<?>[]::new));
    }

    public abstract Map<Identifier, DamageType> generate(Map<Identifier, DamageType> damageTypeMap);

    @Override
    public String getName() {
        return "Damage Types";
    }
}
