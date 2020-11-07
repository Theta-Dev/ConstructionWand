package thetadev.constructionwand.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.basics.option.IOption;
import thetadev.constructionwand.basics.option.WandOptions;
import thetadev.constructionwand.job.AngelJob;
import thetadev.constructionwand.job.WandJob;

import java.util.List;

public abstract class ItemWand extends Item
{
	public final String name;

	public ItemWand(String name, Item.Settings settings) {
		super(settings.group(ItemGroup.TOOLS));
		this.name = name;
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context)
	{
		PlayerEntity player = context.getPlayer();
		Hand hand = context.getHand();
		World world = context.getWorld();

		if(world.isClient || player == null) return ActionResult.FAIL;

		ItemStack stack = player.getStackInHand(hand);

		if(player.isSneaking() && ConstructionWand.instance.undoHistory.isUndoActive(player)) {
			return ConstructionWand.instance.undoHistory.undo(player, world, context.getBlockPos()) ? ActionResult.SUCCESS : ActionResult.FAIL;
		}
		else {
			WandJob job = WandJob.getJob(player, world, new BlockHitResult(context.getHitPos(), context.getSide(), context.getBlockPos(), false), stack);
			return job.doIt() ? ActionResult.SUCCESS : ActionResult.FAIL;
		}
	}



	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getStackInHand(hand);

		if(!player.isSneaking()) {
			if(world.isClient) return TypedActionResult.fail(stack);

			// Right click: Place angel block
			//ConstructionWand.LOGGER.debug("Place angel block");
			WandJob job = new AngelJob(player, world, stack);
			return job.doIt() ? TypedActionResult.success(stack) : TypedActionResult.fail(stack);
		}
		return TypedActionResult.fail(stack);
	}

	@Override
	public boolean canRepair(ItemStack toRepair, ItemStack repair) {
		return false;
	}

	public int getLimit(PlayerEntity player, ItemStack stack) {
		return getLimit();
	}

	protected int getLimit() {
		return ConstructionWand.instance.config.getWandLimit(name);
	}

	public static int getWandMode(ItemStack stack) {
		WandOptions options = new WandOptions(stack);
		return options.mode.get().ordinal();
	}

	@Environment(EnvType.CLIENT)
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> lines, TooltipContext context) {
		ItemWand wand = (ItemWand) stack.getItem();
		WandOptions options = new WandOptions(stack);

		String langTooltip = ConstructionWand.MODID + ".tooltip.";

		if(Screen.hasShiftDown()) {
			for(int i=1; i<options.allOptions.length; i++) {
				IOption<?> opt = options.allOptions[i];
				lines.add(new TranslatableText(opt.getKeyTranslation()).formatted(Formatting.AQUA)
						.append(new TranslatableText(opt.getValueTranslation()).formatted(Formatting.GRAY))
				);
			}
		}
		else {
			IOption<?> opt = options.allOptions[0];
			lines.add(new TranslatableText(langTooltip + "blocks", getLimit()).formatted(Formatting.GRAY));
			lines.add(new TranslatableText(opt.getKeyTranslation()).formatted(Formatting.AQUA)
					.append(new TranslatableText(opt.getValueTranslation()).formatted(Formatting.WHITE)));
			lines.add(new TranslatableText(langTooltip + "shift").formatted(Formatting.AQUA));
		}
	}

	public static void optionMessage(PlayerEntity player, IOption<?> option) {
		player.sendMessage(
				new TranslatableText(option.getKeyTranslation()).formatted(Formatting.AQUA)
						.append(new TranslatableText(option.getValueTranslation()).formatted(Formatting.WHITE))
						.append(new LiteralText(" - ").formatted(Formatting.GRAY))
						.append(new TranslatableText(option.getDescTranslation()).formatted(Formatting.WHITE))
				, true);
	}
}
