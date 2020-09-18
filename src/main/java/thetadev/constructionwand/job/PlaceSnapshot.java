package thetadev.constructionwand.job;

import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.util.math.BlockPos;

public class PlaceSnapshot
{
	public BlockState block;
	public final BlockState supportingBlock;
	public final BlockPos pos;
	public final BlockItem item;

	public PlaceSnapshot(BlockPos pos, BlockState supportingBlock, BlockItem item)
	{
		this.pos = pos;
		this.supportingBlock = supportingBlock;
		this.item = item;
	}
}
