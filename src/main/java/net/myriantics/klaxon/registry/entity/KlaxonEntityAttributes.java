package net.myriantics.klaxon.registry.entity;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.myriantics.klaxon.KlaxonCommon;

import java.util.ArrayList;
import java.util.List;

public abstract class KlaxonEntityAttributes {
    private static final ArrayList<Holder<Attribute>> KLAXON_GENERIC_ENTITY_ATTRIBUTES =  new ArrayList<>();
    private static final ArrayList<Holder<Attribute>> KLAXON_PLAYER_ENTITY_ATTRIBUTES =  new ArrayList<>();

    public static Holder<Attribute> WINCH_CABLE_LENGTH = registerPlayer(
            "winch_cable_length",
            new RangedAttribute(
                    "klaxon.attribute.name.player.winch_cable_length",
                    0.0,
                    0.0,
                    256
            ).setSyncable(true)
    );

    private static Holder<Attribute> registerGeneric(String id, Attribute attribute) {
        Holder<Attribute> entry = Registry.registerForHolder(BuiltInRegistries.ATTRIBUTE, KlaxonCommon.locate("generic." + id), attribute);
        KLAXON_GENERIC_ENTITY_ATTRIBUTES.add(entry);
        return entry;
    }

    private static Holder<Attribute> registerPlayer(String id, Attribute attribute) {
        Holder<Attribute> entry = Registry.registerForHolder(BuiltInRegistries.ATTRIBUTE, KlaxonCommon.locate("player." + id), attribute);
        KLAXON_PLAYER_ENTITY_ATTRIBUTES.add(entry);
        return entry;
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Entity Attributes!");
    }

    public static List<Holder<Attribute>> getKlaxonGenericLivingEntityAttributes() {
        return KLAXON_GENERIC_ENTITY_ATTRIBUTES;
    }

    public static List<Holder<Attribute>> getKlaxonPlayerEntityAttributes() {
        return KLAXON_PLAYER_ENTITY_ATTRIBUTES;
    }
}
