package thetadev.constructionwand.job;

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PlaceSnapshot
{
	public final BlockState block;
	public final BlockPos pos;
	public final Item item;

	public PlaceSnapshot(BlockState block, BlockPos pos, Item item)
	{
		this.block = block;
		this.pos = pos;
		this.item = item;
	}

	public PlaceSnapshot(World world, BlockPos pos, Item item)
	{
		this.block = world.getBlockState(pos);
		this.pos = pos;
		this.item = item;
	}
}
