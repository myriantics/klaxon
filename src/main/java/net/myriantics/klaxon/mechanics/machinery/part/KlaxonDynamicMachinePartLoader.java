package net.myriantics.klaxon.mechanics.machinery.part;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.infra.KlaxonResourceReloadListener;
import net.myriantics.klaxon.infra.ReloadableRegistryInstance;
import net.myriantics.klaxon.mechanics.machinery.part.data.StackingStrategy;
import net.myriantics.klaxon.mechanics.machinery.part.patch.MachinePartDataComponentPatchBuilder;
import net.myriantics.klaxon.registry.KlaxonReloadableRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

@SuppressWarnings("unchecked") // i love generics
public final class KlaxonDynamicMachinePartLoader extends KlaxonResourceReloadListener<DynamicMachinePartDefinition> {

    private Set<DynamicMachinePartDefinition> definitions = null;
    private Map<Item, DataComponentPatch> partDataCache = Map.of();

    public KlaxonDynamicMachinePartLoader(ReloadableRegistryInstance<?, DynamicMachinePartDefinition> instance, HolderLookup.Provider registries) {
        super(instance, registries);
        CommonLifecycleEvents.TAGS_LOADED.register(this::tagsLoaded);
    }

    public @Nullable DataComponentPatch getDynamicPartComponentsForItem(Item item) {
        return this.partDataCache.get(item);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {

        RegistryOps<JsonElement> ops = this.registries.createSerializationContext(JsonOps.INSTANCE);

        ImmutableSet.Builder<DynamicMachinePartDefinition> definitionBuilder = ImmutableSet.builder();

        for(Map.Entry<ResourceLocation, JsonElement> entry : object.entrySet()) {
            ResourceLocation id = entry.getKey();

            try {
                definitionBuilder.add(DynamicMachinePartDefinition.CODEC.parse(ops, entry.getValue()).getOrThrow(JsonParseException::new));
            } catch (IllegalArgumentException | JsonParseException e) {
                KlaxonCommon.LOGGER.error("Parsing error loading machine part definition {}", id);
            }
        }

        this.definitions = definitionBuilder.build();
    }

    private void copyShit(MachinePartDataComponentPatchBuilder builder, DynamicMachinePartDefinition definition) {
        definition.patch().entrySet().forEach(e -> e.getValue().ifPresent(v -> builder.add((DataComponentType<Object>) e.getKey(), v, StackingStrategy.ADD)));
    }

    private void tagsLoaded(RegistryAccess access, boolean b) {
        if (this.definitions == null) {
            return;
        }

        Reference2ObjectMap<Item, MachinePartDataComponentPatchBuilder> map = new Reference2ObjectArrayMap<>();

        for (DynamicMachinePartDefinition definition : this.definitions) {
            for (ItemStack stack : definition.ingredient().getItems()) {
                MachinePartDataComponentPatchBuilder builder = map.get(stack.getItem());
                if (builder == null) {
                    builder = MachinePartDataComponentPatchBuilder.create();
                    copyShit(builder, definition);
                    map.put(stack.getItem(), builder);
                } else {
                    copyShit(builder, definition);
                }
            }
        }

        ImmutableMap.Builder<Item, DataComponentPatch> builder = ImmutableMap.builder();
        for (Map.Entry<Item, MachinePartDataComponentPatchBuilder> entry : map.entrySet()) {
            builder.put(entry.getKey(), entry.getValue().build());
        }
        this.partDataCache = builder.build();

        KlaxonCommon.LOGGER.info("Loaded machine part definitions for {} item(s) from {} definition entry(s)", this.partDataCache.size(), this.definitions.size());
    }
}
