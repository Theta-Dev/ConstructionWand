package thetadev.constructionwand.items;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.job.WandJob;

import java.util.List;

public abstract class ItemWand extends Item
{
	protected int maxBlocks;

	public ItemWand(Item.Properties properties) {
		super(properties.group(ItemGroup.TOOLS));
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		PlayerEntity player = context.getPlayer();
		Hand hand = context.getHand();
		World world = context.getWorld();

		if(world.isRemote) return ActionResultType.PASS;

		ItemStack stack = player.getHeldItem(hand);

		if(player.isSneaking()) {
			WandJob job = ConstructionWand.instance.jobHistory.getForUndo(player, world, context.getPos());
			if(job == null) return ActionResultType.FAIL;
			ConstructionWand.LOGGER.info("Starting Undo");
			return job.undo() ? ActionResultType.SUCCESS : ActionResultType.FAIL;
		}
		else {
			WandJob job = new WandJob(player, world, new BlockRayTraceResult(context.getHitVec(), context.getFace(), context.getPos(), false), stack);
			return job.doIt() ? ActionResultType.SUCCESS : ActionResultType.FAIL;
		}
	}

	public int getLimit(PlayerEntity player, ItemStack stack) {
		return maxBlocks;
	}

	public void addInformation(ItemStack itemstack, World worldIn, List<ITextComponent> lines, ITooltipFlag extraInfo) {

	}

	@Override
	public boolean canHarvestBlock(BlockState blockIn) {
		return false;
	}
}
