package thetadev.constructionwand.wand;

import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.api.IWandAction;
import thetadev.constructionwand.api.IWandSupplier;
import thetadev.constructionwand.basics.ModStats;
import thetadev.constructionwand.basics.WandUtil;
import thetadev.constructionwand.basics.option.WandOptions;
import thetadev.constructionwand.items.ModItems;
import thetadev.constructionwand.items.wand.ItemWand;
import thetadev.constructionwand.wand.undo.ISnapshot;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class WandJob
{
    public final PlayerEntity player;
    public final World world;
    public final BlockRayTraceResult rayTraceResult;
    public final WandOptions options;
    public final ItemStack wand;
    public final ItemWand wandItem;

    @Nullable
    public final BlockItem targetItem;

    private IWandAction wandAction;
    private IWandSupplier wandSupplier;

    private List<ISnapshot> placeSnapshots;

    public WandJob(PlayerEntity player, World world, BlockRayTraceResult rayTraceResult, ItemStack wand) {
        this(player, world, rayTraceResult, wand, getTargetItem(world, rayTraceResult));
    }

    @Nullable
    private static BlockItem getTargetItem(World world, BlockRayTraceResult rayTraceResult) {
        // Get target item
        Item tgitem = world.getBlockState(rayTraceResult.getPos()).getBlock().asItem();
        if(!(tgitem instanceof BlockItem)) return null;
        return (BlockItem) tgitem;
    }

    public WandJob(PlayerEntity player, World world, BlockRayTraceResult rayTraceResult, ItemStack wand, @Nullable BlockItem targetItem) {
        this.player = player;
        this.world = world;
        this.rayTraceResult = rayTraceResult;
        this.placeSnapshots = new LinkedList<>();
        this.targetItem = targetItem;

        // Get wand
        this.wand = wand;
        this.wandItem = (ItemWand) wand.getItem();
        options = new WandOptions(wand);
    }

    /**
     * Creates a WandJob with a dummy wand (Infinity with standard settings) for use of the wand behavior
     * in other contexts
     */
    public static WandJob withDummyWand(PlayerEntity player, World world, BlockRayTraceResult rayTraceResult, @Nullable BlockItem targetItem) {
        return new WandJob(player, world, rayTraceResult, new ItemStack(ModItems.WAND_INFINITY), targetItem);
    }

    public void getPlaceSnapshots(IWandAction wandAction, IWandSupplier wandSupplier) {
        this.wandSupplier = wandSupplier;
        wandSupplier.getSupply(targetItem);
        this.wandAction = wandAction;
        placeSnapshots = wandAction.getSnapshots(wandSupplier);
    }

    public Set<BlockPos> getBlockPositions() {
        return placeSnapshots.stream().map(ISnapshot::getPos).collect(Collectors.toSet());
    }

    public boolean doIt() {
        LinkedList<ISnapshot> executed = new LinkedList<>();

        for(ISnapshot snapshot : placeSnapshots) {
            if(wand.isEmpty() || wandItem.getLimit(player, wand) == 0) break;

            if(snapshot.execute(world, player)) {
                // If the item cant be taken, undo the placement
                if(wandSupplier.takeItemStack(snapshot.getRequiredItems()) == 0) executed.add(snapshot);
                else {
                    ConstructionWand.LOGGER.info("Item could not be taken. Remove block: " +
                            snapshot.getBlockState().getBlock().toString());
                    snapshot.forceRestore(world);
                }

                wand.damageItem(1, player, (e) -> e.sendBreakAnimation(player.swingingHand));
                player.addStat(ModStats.USE_WAND);
            }
        }
        placeSnapshots = executed;

        // Play place sound
        if(!placeSnapshots.isEmpty()) {
            SoundType sound = placeSnapshots.get(0).getBlockState().getSoundType();
            world.playSound(null, WandUtil.playerPos(player), sound.getPlaceSound(), SoundCategory.BLOCKS, sound.volume, sound.pitch);
        }

        // Add to job history for undo
        if(placeSnapshots.size() > 1) ConstructionWand.instance.undoHistory.add(player, world, placeSnapshots);

        return !placeSnapshots.isEmpty();
    }
}