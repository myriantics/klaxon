package net.myriantics.klaxon.mechanics.explosive_catalyst.transformer;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystTransformer;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystTransformerType;
import net.myriantics.klaxon.mechanics.explosive_catalyst.context.ExplosiveCatalystContext;
import net.myriantics.klaxon.recipe.explosive_catalyst.ExplosiveCatalystData;
import net.myriantics.klaxon.registry.explosive_catalyst.KlaxonExplosiveCatalystContextParams;
import net.myriantics.klaxon.registry.explosive_catalyst.KlaxonExplosiveCatalystTransformerTypes;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BaseBlockStateExplosiveCatalystTransformer extends ExplosiveCatalystTransformer {
    private final Optional<HolderSet<Block>> blocks;
    private final Optional<StatePropertiesPredicate> properties;

    public static final MapCodec<BaseBlockStateExplosiveCatalystTransformer> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RegistryCodecs.homogeneousList(Registries.BLOCK).optionalFieldOf("blocks").forGetter(i -> i.blocks),
            StatePropertiesPredicate.CODEC.optionalFieldOf("state").forGetter(i -> i.properties)
            ).apply(instance, BaseBlockStateExplosiveCatalystTransformer::new)
    );

    public BaseBlockStateExplosiveCatalystTransformer(Optional<HolderSet<Block>> blocks, Optional<StatePropertiesPredicate> properties) {
        this.blocks = blocks;
        this.properties = properties;
    }

    @Override
    public ExplosiveCatalystData transformExplosiveCatalystData(ExplosiveCatalystContext context, ExplosiveCatalystData original) {
        @Nullable BlockState state = context.get(KlaxonExplosiveCatalystContextParams.SUPPORT_STATE);
        // make sure state is present
        if (state == null) {
            return original;
        }

        // test blocks
        if (this.blocks.isPresent() && !state.is(this.blocks.get())) {
            return original;
        }

        // test properties
        if (this.properties.isPresent() && !this.properties.get().matches(state)) {
            return original;
        }

        return original.producesFire() ? original : original.copyWithFiery();
    }

    @Override
    public ExplosiveCatalystTransformerType<BaseBlockStateExplosiveCatalystTransformer> getType() {
        return KlaxonExplosiveCatalystTransformerTypes.SUPPORTING_BLOCK_STATE;
    }
}
