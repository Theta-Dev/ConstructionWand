package thetadev.constructionwand.job;

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PlaceSnapshot
{
	public BlockState block;
	public BlockState supportingBlock;
	public BlockPos pos;
	public Item item;

	public PlaceSnapshot(BlockPos pos, BlockState supportingBlock)
	{
		this.pos = pos;
		this.supportingBlock = supportingBlock;
	}
}
