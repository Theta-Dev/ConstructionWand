package thetadev.constructionwand.items;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.basics.*;
import thetadev.constructionwand.basics.options.EnumLock;
import thetadev.constructionwand.basics.options.EnumMode;
import thetadev.constructionwand.basics.options.IEnumOption;
import thetadev.constructionwand.basics.options.WandOptions;
import thetadev.constructionwand.job.AngelJob;
import thetadev.constructionwand.job.JobHistory;
import thetadev.constructionwand.job.WandJob;

import java.util.List;

public abstract class ItemWand extends Item
{
	public final int maxBlocks;
	public final int angelDistance;

	public ItemWand(Item.Properties properties, int maxBlocks, int angelDistance) {
		super(properties.group(ItemGroup.TOOLS));
		this.maxBlocks = maxBlocks;
		this.angelDistance = angelDistance;
		addPropertyOverride(new ResourceLocation(ConstructionWand.MODID, "wand_mode"),
				(stack, worldIn, entityIn) -> getWandMode(stack));
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context)
	{
		PlayerEntity player = context.getPlayer();
		Hand hand = context.getHand();
		World world = context.getWorld();

		if(world.isRemote) return ActionResultType.FAIL;

		ItemStack stack = player.getHeldItem(hand);

		if(player.isSneaking() && ConstructionWand.instance.jobHistory.isUndoActive(player)) {
			WandJob job = ConstructionWand.instance.jobHistory.getForUndo(player, world, context.getPos());
			if(job == null) return ActionResultType.FAIL;
			//ConstructionWand.LOGGER.debug("Starting Undo");
			return job.undo() ? ActionResultType.SUCCESS : ActionResultType.FAIL;
		}
		else {
			WandJob job = WandJob.getJob(player, world, new BlockRayTraceResult(context.getHitVec(), context.getFace(), context.getPos(), false), stack);
			return job.doIt() ? ActionResultType.SUCCESS : ActionResultType.FAIL;
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if(world.isRemote) return new ActionResult<>(ActionResultType.FAIL, stack);

		if(player.isSneaking()) {
			// SHIFT + Right click: Change wand mode
			WandOptions options = new WandOptions(stack);
			IEnumOption opt = EnumMode.DEFAULT;
			opt = options.nextOption(opt);

			//ConstructionWand.LOGGER.debug("Wand mode: " + options.getOption(EnumLock.NOLOCK));

			optionMessage(player, opt);

			player.inventory.markDirty();
			return new ActionResult<>(ActionResultType.SUCCESS, stack);
		}
		else {
			// Right click: Place angel block
			//ConstructionWand.LOGGER.debug("Place angel block");
			WandJob job = new AngelJob(player, world, stack);
			return new ActionResult<>(job.doIt() ? ActionResultType.SUCCESS : ActionResultType.FAIL, stack);
		}
	}

	@Override
	public boolean canHarvestBlock(BlockState blockIn) {
		return false;
	}

	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		return false;
	}

	public int getLimit(PlayerEntity player, ItemStack stack) {
		return maxBlocks;
	}

	public static int getWandMode(ItemStack stack) {
		WandOptions options = new WandOptions(stack);
		return options.getOption(EnumMode.DEFAULT).getOrdinal();
	}

	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack itemstack, World worldIn, List<ITextComponent> lines, ITooltipFlag extraInfo) {
		ItemWand wand = (ItemWand) itemstack.getItem();
		WandOptions options = new WandOptions(itemstack);

		String langPrefix = ConstructionWand.MODID + ".option.";
		String langTooltip = ConstructionWand.MODID + ".tooltip.";

		if(Screen.hasShiftDown()) {
			for(int i=1; i<WandOptions.options.length; i++) {
				IEnumOption opt = WandOptions.options[i];
				lines.add(new TranslationTextComponent(langPrefix + opt.getOptionKey()).applyTextStyle(TextFormatting.AQUA)
						.appendSibling(new TranslationTextComponent(langPrefix + options.getOption(opt).getTranslationKey()).applyTextStyle(TextFormatting.GRAY))
				);
			}
		}
		else {
			IEnumOption opt = WandOptions.options[0];
			lines.add(new TranslationTextComponent(langTooltip + "blocks", wand.maxBlocks).applyTextStyle(TextFormatting.GRAY));
			lines.add(new TranslationTextComponent(langPrefix+opt.getOptionKey()).applyTextStyle(TextFormatting.AQUA)
					.appendSibling(new TranslationTextComponent(langPrefix+options.getOption(opt).getTranslationKey()).applyTextStyle(TextFormatting.WHITE)));
			lines.add(new TranslationTextComponent(langTooltip + "shift").applyTextStyle(TextFormatting.AQUA));
		}
	}

	public static void optionMessage(PlayerEntity player, IEnumOption option) {
		String langPrefix = ConstructionWand.MODID + ".option.";

		player.sendStatusMessage(
				new TranslationTextComponent(langPrefix+option.getOptionKey()).applyTextStyle(TextFormatting.AQUA)
						.appendSibling(new TranslationTextComponent(langPrefix+option.getTranslationKey()).applyTextStyle(TextFormatting.WHITE))
						.appendSibling(new StringTextComponent(" - ").applyTextStyle(TextFormatting.GRAY))
						.appendSibling(new TranslationTextComponent(langPrefix+option.getTranslationKey()+".desc").applyTextStyle(TextFormatting.WHITE))
				, true);
	}
}
