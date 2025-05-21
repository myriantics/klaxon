package net.myriantics.klaxon.datagen.advancement;

import net.minecraft.advancement.*;
import net.minecraft.item.ItemConvertible;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public abstract class KlaxonAdvancementSubProvider {
    protected final Consumer<AdvancementEntry> consumer;
    protected final String path;

    public KlaxonAdvancementSubProvider(Consumer<AdvancementEntry> consumer, String path) {
        this.consumer = consumer;
        this.path = path;
    }

    protected abstract AdvancementEntry generateAdvancements();

    protected AdvancementEntry addTask(AdvancementEntry parent, String name, ItemConvertible display, AdvancementCriterion<?> criterion) {
        return addTask(parent, name, display, false, criterion, null);
    }

    protected AdvancementEntry addGoal(AdvancementEntry parent, String name, ItemConvertible display, AdvancementCriterion<?> criterion) {
        return addGoal(parent, name, display, false, criterion, null);
    }

    protected AdvancementEntry addChallenge(AdvancementEntry parent, String name, ItemConvertible display, AdvancementCriterion<?> criterion) {
        return addChallenge(parent, name, display, false, criterion, null);
    }

    protected AdvancementEntry addHiddenTask(AdvancementEntry parent, String name, ItemConvertible display, AdvancementCriterion<?> criterion) {
        return addTask(parent, name, display, true, criterion, null);
    }

    protected AdvancementEntry addHiddenGoal(AdvancementEntry parent, String name, ItemConvertible display, AdvancementCriterion<?> criterion) {
        return addGoal(parent, name, display, true, criterion, null);
    }

    protected AdvancementEntry addHidddenChallenge(AdvancementEntry parent, String name, ItemConvertible display, AdvancementCriterion<?> criterion) {
        return addChallenge(parent, name, display, true, criterion, null);
    }


    protected AdvancementEntry addTask(AdvancementEntry parent, String name, ItemConvertible display, boolean hidden, AdvancementCriterion<?> criterion, @Nullable AdvancementRewards.Builder rewards) {
        return addAdvancement(parent, name, display, AdvancementFrame.TASK, hidden, criterion, rewards);
    }

    protected AdvancementEntry addGoal(AdvancementEntry parent, String name, ItemConvertible display, boolean hidden, AdvancementCriterion<?> criterion, @Nullable AdvancementRewards.Builder rewards) {
        return addAdvancement(parent, name, display, AdvancementFrame.GOAL, hidden, criterion, rewards);
    }

    protected AdvancementEntry addChallenge(AdvancementEntry parent, String name, ItemConvertible display, boolean hidden, AdvancementCriterion<?> criterion, @Nullable AdvancementRewards.Builder rewards) {
        return addAdvancement(parent, name, display, AdvancementFrame.CHALLENGE, hidden, criterion, rewards);
    }

    protected AdvancementEntry addRootAdvancement(ItemConvertible display, AdvancementFrame frame, AdvancementCriterion<?> criterion) {
        return addAdvancement(null, "root", KlaxonCommon.locate("textures/gui/advancements/backgrounds/" + path + ".png"), display, frame, false, criterion, null);
    }

    protected AdvancementEntry addAdvancement(@Nullable AdvancementEntry parent, String name, ItemConvertible display, AdvancementFrame frame, boolean hidden, AdvancementCriterion<?> criterion, @Nullable AdvancementRewards.Builder rewards) {
        return addAdvancement(parent, name, null, display, frame, hidden, criterion, rewards);
    }

    protected AdvancementEntry addAdvancement(@Nullable AdvancementEntry parent, String name, Identifier backgroundId, ItemConvertible display, AdvancementFrame frame, boolean hidden, AdvancementCriterion<?> criterion, @Nullable AdvancementRewards.Builder rewards) {
        Advancement.Builder builder = Advancement.Builder.create();
        if (parent != null) {
            builder.parent(parent);
        }

        builder.display(
                        display,
                        Text.translatable("advancements.klaxon." + path + "." + name + ".title"),
                        Text.translatable("advancements.klaxon." + path + "." + name + ".description"),
                        backgroundId,
                        frame,
                        false,
                        false,
                        hidden
                )
                .criterion(name, criterion);

        if (rewards != null) {
            builder.rewards(rewards);
        }

        return builder.build(consumer, KlaxonCommon.locate(path + "/" + name).toString());
    }
}
