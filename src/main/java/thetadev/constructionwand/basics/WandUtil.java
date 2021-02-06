package thetadev.constructionwand.basics;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.world.BlockEvent;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.items.ItemWand;

import java.util.ArrayList;
import java.util.List;

public class WandUtil
{
    public static boolean stackEquals(ItemStack stackA, ItemStack stackB) {
        return ItemStack.areItemsEqual(stackA, stackB) && ItemStack.areItemStackTagsEqual(stackA, stackB);
    }

    public static boolean stackEquals(ItemStack stackA, Item item) {
        ItemStack stackB = new ItemStack(item);
        return stackEquals(stackA, stackB);
    }

    public static ItemStack holdingWand(PlayerEntity player) {
        if(player.getHeldItem(Hand.MAIN_HAND) != ItemStack.EMPTY && player.getHeldItem(Hand.MAIN_HAND).getItem() instanceof ItemWand) {
            return player.getHeldItem(Hand.MAIN_HAND);
        }
        else if(player.getHeldItem(Hand.OFF_HAND) != ItemStack.EMPTY && player.getHeldItem(Hand.OFF_HAND).getItem() instanceof ItemWand) {
            return player.getHeldItem(Hand.OFF_HAND);
        }
        return null;
    }

    public static BlockPos playerPos(PlayerEntity player) {
        return new BlockPos(player.getPositionVec());
    }

    public static Vector3d entityPositionVec(Entity entity) {
        return new Vector3d(entity.getPosX(), entity.getPosY() - entity.getYOffset() + entity.getHeight()/2, entity.getPosZ());
    }

    public static Vector3d blockPosVec(BlockPos pos) {
        return new Vector3d(pos.getX(), pos.getY(), pos.getZ());
    }

    public static List<ItemStack> getHotbar(PlayerEntity player) {
        return player.inventory.mainInventory.subList(0, 9);
    }

    public static List<ItemStack> getHotbarWithOffhand(PlayerEntity player) {
        ArrayList<ItemStack> inventory = new ArrayList<>(player.inventory.offHandInventory);
        inventory.addAll(player.inventory.mainInventory.subList(0, 9));
        return inventory;
    }

    public static List<ItemStack> getMainInv(PlayerEntity player) {
        return player.inventory.mainInventory.subList(9, player.inventory.mainInventory.size());
    }

    public static List<ItemStack> getFullInv(PlayerEntity player) {
        ArrayList<ItemStack> inventory = new ArrayList<>(player.inventory.offHandInventory);
        inventory.addAll(player.inventory.mainInventory);
        return inventory;
    }

    public static int maxRange(BlockPos p1, BlockPos p2) {
        return Math.max(Math.abs(p1.getX() - p2.getX()), Math.abs(p1.getZ() - p2.getZ()));
    }

    public static boolean isTEAllowed(BlockState state) {
        if(!state.hasTileEntity()) return true;

        ResourceLocation name = state.getBlock().getRegistryName();
        if(name == null) return false;

        String fullId = name.toString();
        String modId = name.getNamespace();

        boolean inList = ConfigServer.TE_LIST.get().contains(fullId) || ConfigServer.TE_LIST.get().contains(modId);
        boolean isWhitelist = ConfigServer.TE_WHITELIST.get();

        return isWhitelist == inList;
    }

    public static boolean placeBlock(World world, PlayerEntity player, BlockState block, BlockPos pos, BlockItem item) {
        if(!world.setBlockState(pos, block)) {
            ConstructionWand.LOGGER.info("Block could not be placed");
            return false;
        }

        // Remove block if placeEvent is canceled
        BlockSnapshot snapshot = BlockSnapshot.create(world.func_234923_W_(), world, pos);
        BlockEvent.EntityPlaceEvent placeEvent = new BlockEvent.EntityPlaceEvent(snapshot, block, player);
        MinecraftForge.EVENT_BUS.post(placeEvent);
        if(placeEvent.isCanceled()) {
            world.removeBlock(pos, false);
            return false;
        }

        ItemStack stack;
        if(item == null) stack = new ItemStack(block.getBlock().asItem());
        else {
            stack = new ItemStack(item);
            player.addStat(Stats.ITEM_USED.get(item));
        }

        // Call OnBlockPlaced method
        block.getBlock().onBlockPlacedBy(world, pos, block, player, stack);

        return true;
    }

    public static boolean removeBlock(World world, PlayerEntity player, BlockState block, BlockPos pos) {
        BlockState currentBlock = world.getBlockState(pos);

        if(world.isBlockModifiable(player, pos) &&
                (player.isCreative() ||
                        (currentBlock.getBlockHardness(world, pos) > -1 && world.getTileEntity(pos) == null &&
                                ReplacementRegistry.matchBlocks(currentBlock.getBlock(), block.getBlock())))) {

            BlockEvent.BreakEvent breakEvent = new BlockEvent.BreakEvent(world, pos, currentBlock, player);
            MinecraftForge.EVENT_BUS.post(breakEvent);
            if(breakEvent.isCanceled()) return false;

            world.removeBlock(pos, false);
            return true;
        }
        return false;
    }
}
