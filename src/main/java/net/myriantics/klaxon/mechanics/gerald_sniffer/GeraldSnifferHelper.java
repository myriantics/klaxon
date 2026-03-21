package net.myriantics.klaxon.mechanics.gerald_sniffer;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.mixin.minecraft.gerald_sniffer.SnifferEntityInvoker;
import net.myriantics.klaxon.registry.dynamic.KlaxonLootTables;

public abstract class GeraldSnifferHelper {

    public static void onCustomNameSet(Sniffer snifferEntity, Component text) {
        SnifferEntityMixinAccess access = (SnifferEntityMixinAccess) snifferEntity;
        GeraldSnifferState state = access.klaxon$getGeraldSnifferState();

        // reset gerald sniffer state if the text doesn't pass the test.
        if (!testText(text)) {
            access.klaxon$setGeraldSnifferState(GeraldSnifferState.TRACKING_UNSUPPORTED);
            return;
        }

        // only perform the action if sniffer hasn't dug up an item before
        // nametags set state to TRACKING_UNSUPPORTED before this method is called - so they can override this check
        if (!state.equals(GeraldSnifferState.TRACKING_UNSUPPORTED)) {
            return;
        }

        Level world = snifferEntity.level();

        // check to see that we're on the server and that the gerald loot table is present
        if (!KlaxonLootTables.isLootTablePresent(world.getServer(), KlaxonLootTables.GERALD_SNIFFER_GAMEPLAY)) {
            return;
        }

        // update tracking state
        access.klaxon$setGeraldSnifferState(GeraldSnifferState.TRACKING_READY);

        lockOnToTrackingPos(snifferEntity);
    }

    public static void lockOnToTrackingPos(Sniffer snifferEntity) {
        SnifferEntityMixinAccess access = (SnifferEntityMixinAccess) snifferEntity;
        if (!access.klaxon$getGeraldSnifferState().equals(GeraldSnifferState.TRACKING_READY)) {
            return;
        }

        // instantly lock on to a searching pos
        // walking speed is greater than normal sniffer searching
        Brain<Sniffer> brain = snifferEntity.getBrain();
        ((SnifferEntityInvoker) snifferEntity).klaxon$invokeFindSniffingTargetPos().ifPresent((pos -> {
            ((SnifferEntityMixinAccess) snifferEntity).klaxon$setGeraldSnifferState(GeraldSnifferState.TRACKING_IN_PROGRESS);
            brain.eraseMemory(MemoryModuleType.SNIFF_COOLDOWN);
            brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(pos, 1.8f, 0));
            brain.setMemory(MemoryModuleType.SNIFFER_SNIFFING_TARGET, pos);
            brain.setActiveActivityIfPossible(Activity.SNIFF);
        }));
    }

    public static boolean testText(Component text) {
        return text.getString().toLowerCase().contains("gerald");
    }
}
