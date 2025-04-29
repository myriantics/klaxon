package net.myriantics.klaxon.registry.minecraft;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;

import java.util.ArrayList;
import java.util.List;

public class KlaxonEntityAttributes {
    private static final ArrayList<RegistryEntry<EntityAttribute>> KLAXON_GENERIC_ENTITY_ATTRIBUTES =  new ArrayList<>();

    public static final RegistryEntry<EntityAttribute> GENERIC_WEIGHT = registerGeneric(
            "generic.weight", new ClampedEntityAttribute("klaxon.attribute.name.generic.weight", 0.0, 0.0, 20.0)
                    .setCategory(EntityAttribute.Category.NEUTRAL)
                    .setTracked(true)
    );

    private static RegistryEntry<EntityAttribute> registerGeneric(String id, EntityAttribute attribute) {
        RegistryEntry<EntityAttribute> entry = Registry.registerReference(Registries.ATTRIBUTE, KlaxonCommon.locate(id), attribute);
        KLAXON_GENERIC_ENTITY_ATTRIBUTES.add(entry);
        return entry;
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Entity Attributes!");
    }

    public static List<RegistryEntry<EntityAttribute>> getKlaxonGenericLivingEntityAttributes() {
        return List.copyOf(KLAXON_GENERIC_ENTITY_ATTRIBUTES);
    }
}
