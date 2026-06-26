package net.myriantics.klaxon.item.equipment.tools;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class FabricatronItem extends Item {

    private static final int COOLDOWN_TICKS = 15;

    public FabricatronItem(Properties properties) {
        super(properties);
    }

    protected InteractionResult handleEntityUse(Player player, Level level, InteractionHand hand, Entity entity, @Nullable EntityHitResult entityHitResult) {

        ItemStack fabricatronStack = player.getItemInHand(hand);

        if (this.performCraft(fabricatronStack, player, level, hand, resultStack -> {
            if (entity.interact(player, hand).consumesAction()) {
                return resultStack;
            }

            return resultStack.use(level, player, hand).getObject();
        })) {
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        ItemStack fabricatronStack = context.getItemInHand();
        @Nullable Player player = context.getPlayer();
        InteractionHand hand = context.getHand();

        if (player == null) {
            return InteractionResult.PASS;
        }

        if (level.getBlockEntity(pos) instanceof CrafterBlockEntity crafterBlockEntity) {
            if (level.isClientSide()) {
                return InteractionResult.SUCCESS;
            }

            NonNullList<Ingredient> ingredients = NonNullList.withSize(9, Ingredient.EMPTY);
            NonNullList<ItemStack> stacks = crafterBlockEntity.getItems();

            for (int i = 0; i < ingredients.size(); i++) {
                ingredients.set(i, Ingredient.of(stacks.get(i).getItem()));
            }

            fabricatronStack.set(KlaxonDataComponentTypes.FABRICATRON_PATTERN.value(), ingredients);
            player.displayClientMessage(Component.literal("Copied crafting pattern from Crafter to Fabricatron"), true);
            return InteractionResult.SUCCESS;
        }

        if (this.performCraft(fabricatronStack, player, level, hand, itemStack -> {
            BlockHitResult hitResult = new BlockHitResult(context.getClickLocation(), context.getClickedFace(), context.getClickedPos(), context.isInside());

            if (level.getBlockState(pos).useItemOn(itemStack, level, player, hand, hitResult).consumesAction()) {
                return player.getItemInHand(hand);
            }

            if (itemStack.useOn(new UseOnContext(level, player, hand, itemStack, hitResult)).consumesAction()) {
                return player.getItemInHand(hand);
            }

            return itemStack.use(level, player, hand).getObject();
        })) {
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (this.performCraft(stack, player, level, usedHand, itemStack -> itemStack.use(level, player, usedHand).getObject())) {
            return InteractionResultHolder.success(stack);
        }

        return InteractionResultHolder.fail(stack);
    }

    protected boolean performCraft(ItemStack fabricatronStack, Player player, Level level, InteractionHand hand, UnaryOperator<ItemStack> useHandler) {
        List<Ingredient> ingredients = fabricatronStack.getOrDefault(KlaxonDataComponentTypes.FABRICATRON_PATTERN.value(), List.of());

        if (!ingredients.isEmpty()) {

            PlayerInventoryStorage playerInventory = PlayerInventoryStorage.of(player);

            ItemStack result = ItemStack.EMPTY;

            try (Transaction tx = Transaction.openOuter()) {
                CraftingInput input = this.gatherFromInventory(playerInventory, ingredients, tx);
                Optional<RecipeHolder<CraftingRecipe>> match = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, level);

                if (match.isEmpty()) {
                    tx.abort();
                    return false;
                }

                result = match.get().value().assemble(input, level.registryAccess());

                if (player.getAbilities().instabuild) {
                    tx.abort();
                } else {
                    tx.commit();
                }
            }

            if (!result.isEmpty()) {
                if (!player.getCooldowns().isOnCooldown(result.getItem())) {
                    player.setItemInHand(hand, result);
                    result = useHandler.apply(result);
                    player.setItemInHand(hand, fabricatronStack);
                }

                if (!player.getAbilities().instabuild && !level.isClientSide()) {
                    if (!player.addItem(result)) {
                        player.drop(result, false);
                    }
                }
            }

            if (!player.isCreative()) {
                player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
            }
            return true;
        }

        return false;
    }

    protected CraftingInput gatherFromInventory(PlayerInventoryStorage inventory, List<Ingredient> ingredients, Transaction tx) {
        NonNullList<ItemStack> inputStacks = NonNullList.withSize(9, ItemStack.EMPTY);

        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient selected = ingredients.get(i);

            if (selected.isEmpty()) {
                inputStacks.set(i, ItemStack.EMPTY);
                continue;
            }

            for (int j = 0; j < Inventory.INVENTORY_SIZE; j++) {
                SingleSlotStorage<ItemVariant> slot = inventory.getSlot(j);

                ItemVariant resource = slot.getResource();
                ItemStack resourceStack = resource.toStack();

                if (selected.test(resourceStack)) {
                    try (Transaction txInner = Transaction.openNested(tx)) {
                        if (slot.extract(resource, 1, tx) == 1) {
                            txInner.commit();
                            resourceStack.setCount(1);
                            inputStacks.set(i, resourceStack);
                            break;
                        } else {
                            txInner.abort();
                        }
                    }
                }
            }
        }

        return CraftingInput.of(3, 3, inputStacks);
    }

    public static InteractionResult handleEntityInteraction(Player player, Level level, InteractionHand hand, Entity entity, @Nullable EntityHitResult entityHitResult) {
        ItemStack fabricatronStack = player.getItemInHand(hand);
        if (fabricatronStack.getItem() instanceof FabricatronItem fabricatronItem && !player.getCooldowns().isOnCooldown(fabricatronItem)) {
            return fabricatronItem.handleEntityUse(player, level, hand, entity, entityHitResult);
        }

        return InteractionResult.PASS;
    }
}
