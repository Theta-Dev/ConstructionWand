package thetadev.constructionwand.wand.action;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import thetadev.constructionwand.api.IWandAction;
import thetadev.constructionwand.api.IWandSupplier;
import thetadev.constructionwand.basics.ConfigServer;
import thetadev.constructionwand.basics.WandUtil;
import thetadev.constructionwand.basics.option.WandOptions;
import thetadev.constructionwand.wand.undo.ISnapshot;
import thetadev.constructionwand.wand.undo.PlaceSnapshot;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

public class ActionAngel implements IWandAction
{
    @Override
    public int getLimit(ItemStack wand) {
        return ConfigServer.getWandProperties(wand.getItem()).getAngel();
    }

    @Nonnull
    @Override
    public List<ISnapshot> getSnapshots(Level world, Player player, BlockHitResult rayTraceResult,
                                        ItemStack wand, WandOptions options, IWandSupplier supplier, int limit) {
        LinkedList<ISnapshot> placeSnapshots = new LinkedList<>();

        Direction placeDirection = rayTraceResult.getDirection();
        BlockPos currentPos = rayTraceResult.getBlockPos();
        BlockState supportingBlock = world.getBlockState(currentPos);

        for(int i = 0; i < limit; i++) {
            currentPos = currentPos.offset(placeDirection.getOpposite().getNormal());

            PlaceSnapshot snapshot = supplier.getPlaceSnapshot(world, currentPos, rayTraceResult, supportingBlock);
            if(snapshot != null) {
                placeSnapshots.add(snapshot);
                break;
            }
        }
        return placeSnapshots;
    }

    @Nonnull
    @Override
    public List<ISnapshot> getSnapshotsFromAir(Level world, Player player, BlockHitResult rayTraceResult,
                                               ItemStack wand, WandOptions options, IWandSupplier supplier, int limit) {
        LinkedList<ISnapshot> placeSnapshots = new LinkedList<>();

        if(!player.isCreative() && !ConfigServer.ANGEL_FALLING.get() && player.fallDistance > 10) return placeSnapshots;

        Vec3 playerVec = WandUtil.entityPositionVec(player);
        Vec3 lookVec = player.getLookAngle().multiply(2, 2, 2);
        Vec3 placeVec = playerVec.add(lookVec);
        BlockPos currentPos = WandUtil.posFromVec(placeVec);

        PlaceSnapshot snapshot = supplier.getPlaceSnapshot(world, currentPos, rayTraceResult, null);
        if(snapshot != null) placeSnapshots.add(snapshot);

        return placeSnapshots;
    }
}
