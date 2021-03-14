package thetadev.constructionwand.wand.supplier;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.api.IWandSupplier;
import thetadev.constructionwand.basics.ReplacementRegistry;
import thetadev.constructionwand.basics.WandUtil;
import thetadev.constructionwand.basics.option.WandOptions;
import thetadev.constructionwand.basics.pool.IPool;
import thetadev.constructionwand.basics.pool.OrderedPool;
import thetadev.constructionwand.containers.ContainerManager;
import thetadev.constructionwand.wand.undo.PlaceSnapshot;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Default WandSupplier. Takes items from player inventory.
 */
public class SupplierInventory implements IWandSupplier
{
    protected final PlayerEntity player;
    protected final WandOptions options;

    protected HashMap<BlockItem, Integer> itemCounts;
    protected IPool<BlockItem> itemPool;

    public SupplierInventory(PlayerEntity player, WandOptions options) {
        this.player = player;
        this.options = options;
    }

    public void getSupply(@Nullable BlockItem target) {
        itemCounts = new LinkedHashMap<>();
        ItemStack offhandStack = player.getHeldItem(Hand.OFF_HAND);

        itemPool = new OrderedPool<>();

        // Block in offhand -> override
        if(!offhandStack.isEmpty() && offhandStack.getItem() instanceof BlockItem) {
            addBlockItem((BlockItem) offhandStack.getItem());
        }
        // Otherwise use target block
        else {
            addBlockItem(target);

            // Add replacement items
            if(options.match.get() != WandOptions.MATCH.EXACT) {
                for(Item it : ReplacementRegistry.getMatchingSet(target)) {
                    if(it instanceof BlockItem) addBlockItem((BlockItem) it);
                }
            }
        }
    }

    protected void addBlockItem(BlockItem item) {
        int count = WandUtil.countItem(player, item);
        if(count > 0) {
            itemCounts.put(item, count);
            itemPool.add(item);
        }
    }

    @Override
    @Nullable
    public PlaceSnapshot getPlaceSnapshot(World world, BlockPos pos, BlockRayTraceResult rayTraceResult,
                                          @Nullable BlockState supportingBlock) {
        if(!WandUtil.isPositionPlaceable(world, player, pos, options.replace.get(), rayTraceResult)) return null;
        itemPool.reset();

        while(true) {
            // Draw item from pool (returns null if none are left)
            BlockItem item = itemPool.draw();
            if(item == null) return null;

            int count = itemCounts.get(item);
            if(count == 0) continue;

            PlaceSnapshot placeSnapshot = PlaceSnapshot.get(world, player, rayTraceResult, pos, item, supportingBlock, options);
            if(placeSnapshot != null) {
                int ncount = count - 1;
                itemCounts.put(item, ncount);

                // Remove item from pool if there are no items left
                if(ncount == 0) itemPool.remove(item);

                return placeSnapshot;
            }
        }
    }

    @Override
    public int takeItemStack(ItemStack stack) {
        int count = stack.getCount();
        Item item = stack.getItem();

        if(player.inventory == null || player.inventory.mainInventory == null) return count;
        if(player.isCreative()) return 0;

        List<ItemStack> hotbar = WandUtil.getHotbarWithOffhand(player);
        List<ItemStack> mainInv = WandUtil.getMainInv(player);

        // Take items from main inv, loose items first
        count = takeItemsInvList(count, item, mainInv, false);
        count = takeItemsInvList(count, item, mainInv, true);

        // Take items from hotbar, containers first
        count = takeItemsInvList(count, item, hotbar, true);
        count = takeItemsInvList(count, item, hotbar, false);

        return count;
    }

    private int takeItemsInvList(int count, Item item, List<ItemStack> inv, boolean container) {
        ContainerManager containerManager = ConstructionWand.instance.containerManager;

        for(ItemStack stack : inv) {
            if(count == 0) break;

            if(container) {
                count = containerManager.useItems(player, new ItemStack(item), stack, count);
            }

            if(!container && WandUtil.stackEquals(stack, item)) {
                int toTake = Math.min(count, stack.getCount());
                stack.shrink(toTake);
                count -= toTake;
                player.inventory.markDirty();
            }
        }
        return count;
    }
}
