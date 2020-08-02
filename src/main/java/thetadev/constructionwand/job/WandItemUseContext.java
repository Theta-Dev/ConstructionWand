package thetadev.constructionwand.job;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class WandItemUseContext extends BlockItemUseContext
{
	public WandItemUseContext(World worldIn, PlayerEntity playerIn, ItemStack stackIn, BlockRayTraceResult rayTraceResultIn)
	{
		super(worldIn, playerIn, Hand.MAIN_HAND, stackIn, rayTraceResultIn);
	}

	@Override
	public boolean canPlace() {
		return replaceClicked;
	}
}
