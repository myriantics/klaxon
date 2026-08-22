package net.myriantics.klaxon.mechanics.explosive_catalyst;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.myriantics.klaxon.mechanics.explosive_catalyst.context.ExplosiveCatalystContext;
import net.myriantics.klaxon.registry.KlaxonBuiltInRegistries;
import net.myriantics.klaxon.registry.explosive_catalyst.KlaxonExplosiveCatalystHandlers;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;

import java.util.Arrays;
import java.util.List;

public class ExplosiveCatalystBehavior {

    public final Holder<ExplosiveCatalystHandler> handlerHolder;
    public final List<ExplosiveCatalystTransformer> transformers;
    public final HolderSet<DataComponentType<?>> relevantComponents;
    public final int color;

    public static final Codec<ExplosiveCatalystBehavior> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            KlaxonBuiltInRegistries.EXPLOSIVE_CATALYST_HANDLERS.holderByNameCodec().fieldOf("handler").forGetter(i -> i.handlerHolder),
            ExplosiveCatalystTransformer.LIST_CODEC.fieldOf("transformers").forGetter(i -> i.transformers),
            RegistryCodecs.homogeneousList(Registries.DATA_COMPONENT_TYPE).lenientOptionalFieldOf("relevant_components", HolderSet.empty()).forGetter(i -> i.relevantComponents),
            Codec.INT.fieldOf("color").forGetter(i -> i.color)
    ).apply(instance, ExplosiveCatalystBehavior::new));

    public ExplosiveCatalystBehavior(Holder<ExplosiveCatalystHandler> handlerHolder, int color, ExplosiveCatalystTransformer... transformers) {
        this(handlerHolder, color, HolderSet.empty(), transformers);
    }

    public ExplosiveCatalystBehavior(Holder<ExplosiveCatalystHandler> handlerHolder, int color, HolderSet<DataComponentType<?>> relevantComponents, ExplosiveCatalystTransformer... transformers) {
        this(handlerHolder, Arrays.stream(transformers).toList(), relevantComponents, color);
    }

    public ExplosiveCatalystBehavior(Holder<ExplosiveCatalystHandler> handlerHolder, List<ExplosiveCatalystTransformer> transformers, HolderSet<DataComponentType<?>> relevantComponents, int color) {
        this.handlerHolder = handlerHolder;
        this.transformers = transformers;
        this.relevantComponents = relevantComponents;
        this.color = color;
    }

    public void createExplosion(ExplosiveCatalystContext context, Position detonationPosition, ExplosiveCatalystData data, boolean modifyWorld) {
        this.handlerHolder.value().createExplosion(context, detonationPosition, data, modifyWorld);
    }

    public ExplosiveCatalystData transformExplosiveCatalystData(ExplosiveCatalystContext context, ExplosiveCatalystData data) {
        for (ExplosiveCatalystTransformer transformer : this.transformers) {
            data = transformer.transformExplosiveCatalystData(context, data);
        }
        return data;
    }

    public boolean relevantComponentsMatch(DataComponentMap map1, DataComponentMap map2) {
        for (Holder<DataComponentType<?>> type : this.relevantComponents) {
            boolean presentIn1 = map1.has(type.value());
            if (presentIn1 != map2.has(type.value())) {
                return false;
            }
            if (!presentIn1) {
                continue;
            }
            if (!map1.get(type.value()).equals(map2.get(type.value()))) {
                return false;
            }
        }

        return true;
    }

    public boolean isNoOp() {
        return this.handlerHolder.equals(KlaxonExplosiveCatalystHandlers.NO_OP);
    }

    public final boolean is(ExplosiveCatalystBehavior behavior) {
        return this == behavior;
    }

    public final boolean is(Holder<ExplosiveCatalystBehavior> behaviorHolder) {
        return this.is(behaviorHolder.value());
    }

    public boolean isComponentIrrelevant(DataComponentType<?> type) {
        return !this.isComponentRelevant(type);
    }

    public boolean isComponentRelevant(DataComponentType<?> type) {
        for (Holder<DataComponentType<?>> holder : this.relevantComponents) {
            if (holder.value().equals(type)) {
                return true;
            }
        }
        return false;
    }
}