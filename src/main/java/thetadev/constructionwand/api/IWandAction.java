package thetadev.constructionwand.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import thetadev.constructionwand.basics.ConfigServer;
import thetadev.constructionwand.basics.option.WandOptions;
import thetadev.constructionwand.wand.undo.ISnapshot;

import javax.annotation.Nonnull;
import java.util.List;

public interface IWandAction
{
    @Nonnull
    List<ISnapshot> getSnapshots(World world, PlayerEntity player, BlockRayTraceResult rayTraceResult,
                                 WandOptions options, ConfigServer.WandProperties properties, int limit,
                                 IWandSupplier supplier);

    @Nonnull
    List<ISnapshot> getSnapshotsFromAir(World world, PlayerEntity player, BlockRayTraceResult rayTraceResult,
                                        WandOptions options, ConfigServer.WandProperties properties, int limit,
                                        IWandSupplier supplier);
}
