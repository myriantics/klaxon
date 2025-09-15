package net.myriantics.klaxon.registry.entity;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.myriantics.klaxon.KlaxonCommon;

import java.util.ArrayList;
import java.util.List;

public abstract class KlaxonEntityAttributes {
    private static final ArrayList<RegistryEntry<EntityAttribute>> KLAXON_GENERIC_ENTITY_ATTRIBUTES =  new ArrayList<>();
    private static final ArrayList<RegistryEntry<EntityAttribute>> KLAXON_PLAYER_ENTITY_ATTRIBUTES =  new ArrayList<>();

    public static RegistryEntry<EntityAttribute> WINCH_CABLE_LENGTH = registerPlayer(
            "winch_cable_length",
            new ClampedEntityAttribute(
                    "klaxon.attribute.name.player.winch_cable_length",
                    0.0,
                    0.0,
                    256
            ).setTracked(true)
    );

    private static RegistryEntry<EntityAttribute> registerGeneric(String id, EntityAttribute attribute) {
        RegistryEntry<EntityAttribute> entry = Registry.registerReference(Registries.ATTRIBUTE, KlaxonCommon.locate("generic." + id), attribute);
        KLAXON_GENERIC_ENTITY_ATTRIBUTES.add(entry);
        return entry;
    }

    private static RegistryEntry<EntityAttribute> registerPlayer(String id, EntityAttribute attribute) {
        RegistryEntry<EntityAttribute> entry = Registry.registerReference(Registries.ATTRIBUTE, KlaxonCommon.locate("player." + id), attribute);
        KLAXON_PLAYER_ENTITY_ATTRIBUTES.add(entry);
        return entry;
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Entity Attributes!");
    }

    public static List<RegistryEntry<EntityAttribute>> getKlaxonGenericLivingEntityAttributes() {
        return KLAXON_GENERIC_ENTITY_ATTRIBUTES;
    }

    public static List<RegistryEntry<EntityAttribute>> getKlaxonPlayerEntityAttributes() {
        return KLAXON_PLAYER_ENTITY_ATTRIBUTES;
    }
}
