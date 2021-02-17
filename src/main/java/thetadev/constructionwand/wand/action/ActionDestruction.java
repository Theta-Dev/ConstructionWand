package thetadev.constructionwand.wand.action;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import thetadev.constructionwand.api.IWandAction;
import thetadev.constructionwand.api.IWandSupplier;
import thetadev.constructionwand.wand.WandJob;
import thetadev.constructionwand.wand.undo.ISnapshot;

import java.util.List;

public class ActionDestruction implements IWandAction
{
    private final World world;
    private final PlayerEntity player;
    private final BlockRayTraceResult rayTraceResult;

    public ActionDestruction(WandJob wandJob) {
        world = wandJob.world;
        player = wandJob.player;
        rayTraceResult = wandJob.rayTraceResult;
    }

    @Override
    public List<ISnapshot> getSnapshots(IWandSupplier supplier) {
        // TODO: Destruction action!
        return null;
    }
}
