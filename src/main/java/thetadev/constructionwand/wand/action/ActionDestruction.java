package thetadev.constructionwand.wand.action;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import thetadev.constructionwand.api.IWandAction;
import thetadev.constructionwand.api.IWandSupplier;
import thetadev.constructionwand.basics.ConfigServer;
import thetadev.constructionwand.basics.option.WandOptions;
import thetadev.constructionwand.wand.undo.ISnapshot;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ActionDestruction implements IWandAction
{
    @Nonnull
    @Override
    public List<ISnapshot> getSnapshots(World world, PlayerEntity player, BlockRayTraceResult rayTraceResult,
                                        WandOptions options, ConfigServer.WandProperties properties, int limit,
                                        IWandSupplier wandSupplier) {
        // TODO: Destruction action!
        return new ArrayList<>();
    }

    @Nonnull
    @Override
    public List<ISnapshot> getSnapshotsFromAir(World world, PlayerEntity player, BlockRayTraceResult rayTraceResult,
                                               WandOptions options, ConfigServer.WandProperties properties,
                                               int limit, IWandSupplier supplier) {
        return new ArrayList<>();
    }
}
