package net.myriantics.klaxon.mechanics.gerald_sniffer;

import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.myriantics.klaxon.mixin.minecraft.gerald_sniffer.SnifferEntityInvoker;
import net.myriantics.klaxon.registry.dynamic.KlaxonLootTables;

public abstract class GeraldSnifferHelper {

    public static void onCustomNameSet(SnifferEntity snifferEntity, Text text) {
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

        World world = snifferEntity.getWorld();

        // check to see that we're on the server and that the gerald loot table is present
        if (!KlaxonLootTables.isLootTablePresent(world.getServer(), KlaxonLootTables.GERALD_SNIFFER_GAMEPLAY)) {
            return;
        }

        // update tracking state
        access.klaxon$setGeraldSnifferState(GeraldSnifferState.TRACKING_READY);

        lockOnToTrackingPos(snifferEntity);
    }

    public static void lockOnToTrackingPos(SnifferEntity snifferEntity) {
        SnifferEntityMixinAccess access = (SnifferEntityMixinAccess) snifferEntity;
        if (!access.klaxon$getGeraldSnifferState().equals(GeraldSnifferState.TRACKING_READY)) {
            return;
        }

        // instantly lock on to a searching pos
        // walking speed is greater than normal sniffer searching
        Brain<SnifferEntity> brain = snifferEntity.getBrain();
        ((SnifferEntityInvoker) snifferEntity).klaxon$invokeFindSniffingTargetPos().ifPresent((pos -> {
            ((SnifferEntityMixinAccess) snifferEntity).klaxon$setGeraldSnifferState(GeraldSnifferState.TRACKING_IN_PROGRESS);
            brain.forget(MemoryModuleType.SNIFF_COOLDOWN);
            brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(pos, 1.8f, 0));
            brain.remember(MemoryModuleType.SNIFFER_SNIFFING_TARGET, pos);
            brain.doExclusively(Activity.SNIFF);
        }));
    }

    public static boolean testText(Text text) {
        return text.getString().toLowerCase().contains("gerald");
    }
}
