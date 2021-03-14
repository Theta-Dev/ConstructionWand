package thetadev.constructionwand.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import thetadev.constructionwand.basics.option.WandOptions;
import thetadev.constructionwand.wand.undo.ISnapshot;

import javax.annotation.Nonnull;
import java.util.List;

public interface IWandAction
{
    int getLimit(ItemStack wand);

    @Nonnull
    List<ISnapshot> getSnapshots(World world, PlayerEntity player, BlockRayTraceResult rayTraceResult,
                                 ItemStack wand, WandOptions options, IWandSupplier supplier, int limit);

    @Nonnull
    List<ISnapshot> getSnapshotsFromAir(World world, PlayerEntity player, BlockRayTraceResult rayTraceResult,
                                        ItemStack wand, WandOptions options, IWandSupplier supplier, int limit);
}
