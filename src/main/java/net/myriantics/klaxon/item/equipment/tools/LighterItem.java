package net.myriantics.klaxon.item.equipment.tools;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementTriggers;
import net.myriantics.klaxon.registry.misc.KlaxonAttachmentTypes;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

public class LighterItem extends FlintAndSteelItem {

    public static final Ingredient LIGHTER_REPAIR_MATERIALS = Ingredient.of(KlaxonItemTags.LIGHTER_REPAIR_MATERIALS);

    public LighterItem(Properties settings) {
        super(settings);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
        super.releaseUsing(stack, level, livingEntity, timeCharged);
        livingEntity.stopUsingItem();
        Integer firePlacedCount = livingEntity.removeAttached(KlaxonAttachmentTypes.STEEL_LIGHTER_FIRE_PLACEMENT_TRACKER);
        if (firePlacedCount != null && firePlacedCount > 7 && livingEntity instanceof ServerPlayer player) {
            KlaxonAdvancementTriggers.triggerErectFirewall(player);
        }
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        // useOn logic called on client via mixin
        super.onUseTick(level, livingEntity, stack, remainingUseDuration);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player != null && !player.isUsingItem()) {
            this.startUsing(player, context.getHand());
        }
        InteractionResult result = super.useOn(context);
        if (result.indicateItemUse()) {
            player.modifyAttached(KlaxonAttachmentTypes.STEEL_LIGHTER_FIRE_PLACEMENT_TRACKER, integer -> integer == null ? 0 : integer + 1);
        }
        return super.useOn(context);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        this.startUsing(player, usedHand);
        return super.use(level, player, usedHand);
    }

    private void startUsing(Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        player.setAttached(KlaxonAttachmentTypes.STEEL_LIGHTER_FIRE_PLACEMENT_TRACKER, 0);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack ingredient) {
        return LIGHTER_REPAIR_MATERIALS.test(ingredient) || super.isValidRepairItem(stack, ingredient);
    }
}
