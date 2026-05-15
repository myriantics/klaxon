package net.myriantics.klaxon.mechanics.explosive_catalyst;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.CommonColors;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.mechanics.explosive_catalyst.context.ExplosiveCatalystContext;
import net.myriantics.klaxon.mechanics.explosive_catalyst.definition.ExplosiveCatalystDefinition;
import net.myriantics.klaxon.registry.KlaxonRegistries;
import net.myriantics.klaxon.registry.explosive_catalyst.KlaxonExplosiveCatalystBehaviors;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import net.myriantics.klaxon.registry.misc.KlaxonColors;
import net.myriantics.klaxon.util.KlaxonMathHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

public record ExplosiveCatalystData(ResourceKey<ExplosiveCatalystBehavior> behavior, double explosionPower, boolean producesFire) implements TooltipProvider {

    private static final Style ORANGE = Style.EMPTY.withColor(KlaxonColors.ORANGE.getRGB());
    private static final Style GREY = Style.EMPTY.withColor(CommonColors.LIGHT_GRAY);

    public static final Codec<ExplosiveCatalystData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceKey.codec(KlaxonRegistries.EXPLOSIVE_CATALYST_BEHAVIOR).fieldOf("behavior").forGetter(ExplosiveCatalystData::behavior),
            Codec.DOUBLE.fieldOf("explosion_power").forGetter(ExplosiveCatalystData::explosionPower),
            Codec.BOOL.fieldOf("produces_fire").forGetter(ExplosiveCatalystData::producesFire)
    ).apply(instance, ExplosiveCatalystData::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ExplosiveCatalystData> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(KlaxonRegistries.EXPLOSIVE_CATALYST_BEHAVIOR), ExplosiveCatalystData::behavior,
            ByteBufCodecs.DOUBLE, ExplosiveCatalystData::explosionPower,
            ByteBufCodecs.BOOL, ExplosiveCatalystData::producesFire,
            ExplosiveCatalystData::new
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, Optional<ExplosiveCatalystData>> OPTIONAL_STREAM_CODEC = ByteBufCodecs.optional(STREAM_CODEC);

    public static final ExplosiveCatalystData ZERO = new ExplosiveCatalystData(KlaxonExplosiveCatalystBehaviors.NO_OP, 0.0, false);


    public ExplosiveCatalystData(ResourceKey<ExplosiveCatalystBehavior> behavior, double explosionPower, boolean producesFire) {
        this.behavior = behavior;
        this.explosionPower = KlaxonMathHelper.roundToTenth(explosionPower);
        this.producesFire = producesFire;
    }

    public ExplosiveCatalystData(double explosionPower, boolean producesFire) {
        this(KlaxonExplosiveCatalystBehaviors.DEFAULT, explosionPower, producesFire);
    }


    public boolean matchesConditions(double explosionPowerMin, double explosionPowerMax) {
        return explosionPowerMin <= explosionPower && explosionPower <= explosionPowerMax;
    }

    public ExplosiveCatalystData copyWithPower(double explosionPower) {
        return new ExplosiveCatalystData(this.behavior, explosionPower, this.producesFire);
    }

    public ExplosiveCatalystData copyWithFiery() {
        return new ExplosiveCatalystData(this.behavior, this.explosionPower, true);
    }

    public @NotNull Holder<ExplosiveCatalystBehavior> behavior(Level level) {
        return behavior(level.registryAccess());
    }

    public @NotNull Holder<ExplosiveCatalystBehavior> behavior(HolderLookup.Provider provider) {
        Optional<HolderLookup.RegistryLookup<ExplosiveCatalystBehavior>> reg = provider.lookup(KlaxonRegistries.EXPLOSIVE_CATALYST_BEHAVIOR);
        Optional<Holder.Reference<ExplosiveCatalystBehavior>> selected = reg.get().get(this.behavior);
        return selected.orElse(reg.get().getOrThrow(KlaxonExplosiveCatalystBehaviors.NO_OP));
    }

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
        tooltipAdder.accept(Component.translatable("klaxon.text.tooltip.explosive_catalyst_data").setStyle(GREY));
        tooltipAdder.accept(Component.translatable("klaxon.text.tooltip.explosive_catalyst_data.catalyst_behavior", this.behavior.location()).setStyle(GREY));
        tooltipAdder.accept(Component.translatable("klaxon.text.tooltip.explosive_catalyst_data.explosion_power", Component.literal(String.valueOf(this.explosionPower))).setStyle(GREY));
        if (this.producesFire) {
            tooltipAdder.accept(Component.translatable("klaxon.text.tooltip.explosive_catalyst_data.produces_fire").setStyle(ORANGE));
        }
    }

    public static @Nullable ExplosiveCatalystData findEffective(ExplosiveCatalystContext context, Holder<Item> itemHolder) {
        return findEffective(context, itemHolder.value());
    }

    public static @Nullable ExplosiveCatalystData findEffective(ExplosiveCatalystContext context, ItemLike itemLike) {
        return findEffective(context, itemLike.asItem());
    }

    public static @Nullable ExplosiveCatalystData findEffective(ExplosiveCatalystContext context, Item item) {
        return findEffective(context, item.getDefaultInstance());
    }

    public static @Nullable ExplosiveCatalystData findEffective(ExplosiveCatalystContext context, ItemStack catalyst) {
        @Nullable ExplosiveCatalystData raw = findRaw(context.level(), catalyst);
        if (raw == null) {
            return null;
        } else {
            return raw.behavior(context.level()).value().transformExplosiveCatalystData(context, raw);
        }
    }

    public static @Nullable ExplosiveCatalystData findRaw(Level level, ItemStack catalyst) {
        return findRaw(level.registryAccess(), catalyst);
    }

    public static @Nullable ExplosiveCatalystData findRaw(HolderLookup.Provider provider, ItemStack catalyst) {
        if (catalyst.get(KlaxonDataComponentTypes.EXPLOSIVE_CATALYST_DATA.value()) instanceof ExplosiveCatalystData data) {
            return data;
        } else {
            return ExplosiveCatalystDefinition.find(catalyst, provider);
        }
    }
}
