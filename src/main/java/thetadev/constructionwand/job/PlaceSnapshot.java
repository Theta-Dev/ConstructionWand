package thetadev.constructionwand.job;

import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.util.math.BlockPos;

public class PlaceSnapshot
{
	public final BlockState block;
	public final BlockPos pos;
	public final BlockItem item;

	public PlaceSnapshot(BlockPos pos, BlockState block, BlockItem item)
	{
		this.pos = pos;
		this.block = block;
		this.item = item;
	}
}
