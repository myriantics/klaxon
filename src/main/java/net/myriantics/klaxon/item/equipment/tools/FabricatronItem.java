package net.myriantics.klaxon.item.equipment.tools;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class FabricatronItem extends Item {

    private static final int COOLDOWN_TICKS = 15;

    public FabricatronItem(Properties properties) {
        super(properties);
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
            InteractionResult result = itemStack.useOn(new UseOnContext(level, player, hand, itemStack, new BlockHitResult(context.getClickLocation(), context.getClickedFace(), context.getClickedPos(), context.isInside())));
            if (!itemStack.isEmpty() && result == InteractionResult.PASS) {
                itemStack.use(level, player, hand);
            }
        })) {
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (this.performCraft(stack, player, level, usedHand, itemStack -> itemStack.use(level, player, usedHand))) {
            return InteractionResultHolder.success(stack);
        }

        return InteractionResultHolder.fail(stack);
    }

    protected boolean performCraft(ItemStack fabricatronStack, Player player, Level level, InteractionHand hand, Consumer<ItemStack> useHandler) {
        List<Ingredient> ingredients = fabricatronStack.getOrDefault(KlaxonDataComponentTypes.FABRICATRON_PATTERN.value(), List.of());

        if (!ingredients.isEmpty()) {
            CraftingInput testingInput = this.gatherFromInventory(player.getInventory(), ingredients, s -> s.copyWithCount(1));

            Optional<RecipeHolder<CraftingRecipe>> match = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, testingInput, level);

            if (match.isEmpty()) {
                return false;
            }

            CraftingInput realInput = player.getAbilities().instabuild || level.isClientSide() ? testingInput : this.gatherFromInventory(player.getInventory(), ingredients, s -> s.split(1));

            ItemStack result = match.get().value().assemble(realInput, level.registryAccess());

            if (!result.isEmpty()) {
                player.setItemInHand(hand, result);
                useHandler.accept(result);
                player.setItemInHand(hand, fabricatronStack);
            }

            if (!player.getAbilities().instabuild && !result.isEmpty() && !level.isClientSide()) {
                if (!player.addItem(result)) {
                    player.drop(result, false);
                }
            }

            if (!player.isCreative()) {
                player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
            }
            return true;
        }

        return false;
    }

    protected CraftingInput gatherFromInventory(Inventory inventory, List<Ingredient> ingredients, UnaryOperator<ItemStack> splitter) {
        List<ItemStack> inventoryStacks = inventory.items.stream().filter(stack -> !stack.isEmpty()).toList();
        NonNullList<ItemStack> inputStacks = NonNullList.withSize(9, ItemStack.EMPTY);

        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient selected = ingredients.get(i);

            if (selected.isEmpty()) {
                inputStacks.set(i, ItemStack.EMPTY);
                continue;
            }

            for (ItemStack inventoryStack : inventoryStacks) {
                if (selected.test(inventoryStack)) {
                    inputStacks.set(i, splitter.apply(inventoryStack));
                    break;
                }
            }
        }

        return CraftingInput.of(3, 3, inputStacks);
    }
}
