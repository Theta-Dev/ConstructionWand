package thetadev.constructionwand.items;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.basics.*;
import thetadev.constructionwand.job.AngelJob;
import thetadev.constructionwand.job.ConstructionJob;
import thetadev.constructionwand.job.TransductionJob;
import thetadev.constructionwand.job.WandJob;
import thetadev.constructionwand.network.PacketWandOption;

import java.awt.*;
import java.util.List;

import static net.minecraft.entity.player.PlayerEntity.REACH_DISTANCE;

public abstract class ItemWand extends Item
{
	public final int maxBlocks;
	public final int angelDistance;

	public ItemWand(Item.Properties properties, int maxBlocks, int angelDistance) {
		super(properties.group(ItemGroup.TOOLS));
		this.maxBlocks = maxBlocks;
		this.angelDistance = angelDistance;
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context)
	{
		PlayerEntity player = context.getPlayer();
		Hand hand = context.getHand();
		World world = context.getWorld();

		if(world.isRemote) return ActionResultType.FAIL;

		ItemStack stack = player.getHeldItem(hand);

		if(player.isSneaking()) {
			WandJob job = ConstructionWand.instance.jobHistory.getForUndo(player, world, context.getPos());
			if(job == null) return ActionResultType.FAIL;
			ConstructionWand.LOGGER.info("Starting Undo");
			return job.undo() ? ActionResultType.SUCCESS : ActionResultType.FAIL;
		}
		else {
			WandJob job = WandUtil.getJob(player, world, new BlockRayTraceResult(context.getHitVec(), context.getFace(), context.getPos(), false), stack);
			return job.doIt() ? ActionResultType.SUCCESS : ActionResultType.FAIL;
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if(world.isRemote) return ActionResult.resultFail(stack);

		if(player.isSneaking()) {
			// SHIFT + Right click: Change wand mode
			WandOptions options = new WandOptions(stack);
			IEnumOption opt = EnumMode.DEFAULT;

			options.nextOption(opt);
			ConstructionWand.LOGGER.info("Wand mode: " + options.getOption(EnumLock.NOLOCK));

			String langPrefix = ConstructionWand.MODID + ".chat.";
			player.sendStatusMessage(new TranslationTextComponent(langPrefix+options.getOption(opt).getOptionKey())
					.appendSibling(new TranslationTextComponent(langPrefix + options.getOption(opt).getTranslationKey())), true);

			player.inventory.markDirty();
			return ActionResult.resultSuccess(stack);
		}
		else {
			// Right click: Place angel block
			ConstructionWand.LOGGER.info("Place angel block");
			WandJob job = new AngelJob(player, world, stack);
			return job.doIt() ? ActionResult.resultSuccess(stack) : ActionResult.resultFail(stack);
		}
	}

	public int getLimit(PlayerEntity player, ItemStack stack) {
		return maxBlocks;
	}

	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack itemstack, World worldIn, List<ITextComponent> lines, ITooltipFlag extraInfo) {
		WandOptions options = new WandOptions(itemstack);
		String langPrefix = ConstructionWand.MODID + ".tooltip.";

		for(IEnumOption opt : WandOptions.options) {
			lines.add(new TranslationTextComponent(langPrefix + opt.getOptionKey())
					.appendSibling(new TranslationTextComponent(langPrefix + options.getOption(opt).getTranslationKey()))
					.applyTextStyle(TextFormatting.GRAY)
			);
		}
	}

	@Override
	public boolean canHarvestBlock(BlockState blockIn) {
		return false;
	}
}
