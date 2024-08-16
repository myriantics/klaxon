package net.myriantics.klaxon.block.blockentities.blast_processor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.myriantics.klaxon.KlaxonMain;
import net.myriantics.klaxon.recipes.blast_processing.BlastProcessorRecipe;
import net.myriantics.klaxon.recipes.item_explosion_power.ItemExplosionPowerRecipe;
import net.myriantics.klaxon.util.ItemExplosionPowerHelper;

import java.util.Optional;

import static net.myriantics.klaxon.block.blockentities.blast_processor.BlastProcessorBlockEntity.CATALYST_INDEX;
import static net.myriantics.klaxon.block.blockentities.blast_processor.BlastProcessorBlockEntity.PROCESS_ITEM_INDEX;

public class BlastProcessorScreenHandler extends ScreenHandler {
    private final BlastProcessorCraftingInventory ingredientInventory;
    private final SimpleInventory outputInventory;
    private double explosionPower;

    public double getExplosionPower() {
        return this.explosionPower;
    }

    public double getExplosionPowerMax() {
        return this.explosionPowerMax;
    }

    public double getExplosionPowerMin() {
        return this.explosionPowerMin;
    }
    private double explosionPowerMin;
    private double explosionPowerMax;
    private boolean producesFire;
    private boolean requiresFire;

    public ScreenHandlerContext context;
    public PlayerEntity player;


    // client constructor
    public BlastProcessorScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(2), ScreenHandlerContext.EMPTY);
    }

    // server constructor
    public BlastProcessorScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, ScreenHandlerContext context) {
        super(KlaxonMain.BLAST_PROCESSOR_SCREEN_HANDLER, syncId);
        checkSize(inventory, 2);
        this.ingredientInventory = new BlastProcessorCraftingInventory(this, 2, inventory);
        this.context = context;
        this.player = playerInventory.player;
        this.outputInventory = new SimpleInventory(9);
        inventory.onOpen(playerInventory.player);

        int m;
        int l;
        // machine slots
        this.addSlot(new Slot(ingredientInventory, PROCESS_ITEM_INDEX, 35, 17) {
            @Override
            public int getMaxItemCount() {
                return BlastProcessorBlockEntity.MaxItemStackCount;
            }
        });

        this.addSlot(new Slot(ingredientInventory, CATALYST_INDEX, 35, 53) {
            @Override
            public int getMaxItemCount() {
                return BlastProcessorBlockEntity.MaxItemStackCount;
            }
        });

        for (m = 0; m < 3; m++) {
            for (l = 0; l < 3; l++) {
                this.addSlot(new Slot(outputInventory, l + m * 3, 107 + l * 18, 17 + m * 18) {
                    // these are crafting output showcase slots
                    @Override
                    public boolean canTakeItems(PlayerEntity playerEntity) {
                        return false;
                    }

                    @Override
                    public boolean canInsert(ItemStack stack) {
                        return false;
                    }
                });
            }
        }

        // player inventory
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
            }
        }

        // hotbar
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
        }


    }

    @Override
    public void onContentChanged(Inventory inventory) {
        this.context.run((world, pos) -> {
            updateResult(this, world, player, ingredientInventory, outputInventory);
        });
    }

    public void updateResult(ScreenHandler handler, World world, PlayerEntity player, BlastProcessorCraftingInventory craftingInventory, SimpleInventory resultInventory) {
        player.sendMessage(Text.literal("POGGIES"));

        ItemStack processItem = craftingInventory.getStack(PROCESS_ITEM_INDEX);
        ItemStack catalystItem = craftingInventory.getStack(CATALYST_INDEX);

        RecipeManager recipeManager = world.getRecipeManager();
        Optional<ItemExplosionPowerRecipe> explPowMatch = ItemExplosionPowerHelper.getExplosionPowerData(world, catalystItem);

        explPowMatch.ifPresent(itemExplosionPowerRecipe -> explosionPower = itemExplosionPowerRecipe.getExplosionPower());
        producesFire = explPowMatch.get().producesFire();

        // is there fuel present
        if (explPowMatch.get().getExplosionPower() > 0) {

            RecipeType<BlastProcessorRecipe> blstProcType = BlastProcessorRecipe.Type.INSTANCE;
            SimpleInventory blstProcInventory = new SimpleInventory(processItem.copy());

            Optional<BlastProcessorRecipe> blstProcMatch = recipeManager.getFirstMatch(blstProcType, blstProcInventory, world);

            if (blstProcMatch.isPresent()) {

                explosionPowerMin = blstProcMatch.get().getExplosionPowerMin();
                explosionPowerMax = blstProcMatch.get().getExplosionPowerMax();
                requiresFire = blstProcMatch.get().requiresFire();

                if (explosionPower > explosionPowerMin && explosionPower <explosionPowerMax) {
                    if (requiresFire == producesFire || producesFire) {
                        resultInventory.addStack(blstProcMatch.get().getOutput(world.getRegistryManager()));
                    }
                }
            }
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.ingredientInventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int sourceSlotIndex) {
        //MinecraftClient.getInstance().player.sendMessage(Text.literal("client: " + player.getWorld().isClient));
        ItemStack newStack = ItemStack.EMPTY;
        //MinecraftClient.getInstance().player.sendMessage(Text.literal("index: " + sourceSlotIndex));
        MinecraftClient.getInstance().player.sendMessage(Text.literal("looped"));
        Slot slot = this.slots.get(sourceSlotIndex);

        //MinecraftClient.getInstance().player.sendMessage(Text.literal("start_index: " + 0));
        //MinecraftClient.getInstance().player.sendMessage(Text.literal("end_index: " + this.inventory.size()));
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (sourceSlotIndex < this.ingredientInventory.size()) {
                // machine inventory to player inventory
                if (!this.insertItem(originalStack, this.ingredientInventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                // player inventory to machine inventory
            } else {
                // yonked stacking protection logic from EnchantmentScreenHandler - unexpected enchant table carry
                for (int i = 0; i < this.ingredientInventory.size(); i++) {
                    if (this.slots.get(i).hasStack() || !this.slots.get(i).canInsert(newStack)) {
                        continue;
                    }
                    ItemStack filteredStack = newStack.copyWithCount(BlastProcessorBlockEntity.MaxItemStackCount);
                    newStack.decrement(BlastProcessorBlockEntity.MaxItemStackCount);
                    this.slots.get(i).setStack(filteredStack);
                    break;
                }
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }


        // If above fails, return original stack as new stack, so nothing changes.
        return ItemStack.EMPTY;
    }
}
