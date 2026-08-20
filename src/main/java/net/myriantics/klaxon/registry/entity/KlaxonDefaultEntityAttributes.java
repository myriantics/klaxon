package net.myriantics.klaxon.registry.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.entity.entities.mob.ominous_deepslate_blast_processor.OminousDeepslateBlastProcessorEntity;

public abstract class KlaxonDefaultEntityAttributes {

    static {
        register(KlaxonEntityTypes.OMINOUS_DEEPSLATE_BLAST_PROCESSOR, OminousDeepslateBlastProcessorEntity.createAttributes());
    }

    private static <T extends LivingEntity> void register(Holder<EntityType<T>> holder, AttributeSupplier.Builder builder) {
        register(holder.value(), builder);
    }

    private static <T extends LivingEntity> void register(EntityType<T> type, AttributeSupplier.Builder builder) {
        FabricDefaultAttributeRegistry.register(type, builder);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Default Entity Attributes!");
    }
}
