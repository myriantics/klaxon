package net.myriantics.klaxon.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonMain;

public class KlaxonEntities {
    public static final EntityType<EnderPearlPlateEntity> ENDER_PEARL_PLATE = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(KlaxonMain.MOD_ID, "ender_pearl_plate"),
            FabricEntityTypeBuilder.<EnderPearlPlateEntity>create(SpawnGroup.MISC)
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build());
}
