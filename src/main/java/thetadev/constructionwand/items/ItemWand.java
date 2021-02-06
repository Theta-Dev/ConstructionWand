package thetadev.constructionwand.items;

import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
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
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.basics.ConfigServer;
import thetadev.constructionwand.basics.option.IOption;
import thetadev.constructionwand.basics.option.WandOptions;
import thetadev.constructionwand.job.AngelJob;
import thetadev.constructionwand.job.WandJob;

import java.util.List;

public abstract class ItemWand extends Item
{
    public ItemWand(String name, Item.Properties properties) {
        super(properties.group(ItemGroup.TOOLS));
        setRegistryName(ConstructionWand.loc(name));
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context)
    {
        PlayerEntity player = context.getPlayer();
        Hand hand = context.getHand();
        World world = context.getWorld();

        if(world.isRemote || player == null) return ActionResultType.FAIL;

        ItemStack stack = player.getHeldItem(hand);

        if(player.isSneaking() && ConstructionWand.instance.undoHistory.isUndoActive(player)) {
            return ConstructionWand.instance.undoHistory.undo(player, world, context.getPos()) ? ActionResultType.SUCCESS : ActionResultType.FAIL;
        }
        else {
            WandJob job = WandJob.getJob(player, world, new BlockRayTraceResult(context.getHitVec(), context.getFace(), context.getPos(), false), stack);
            return job.doIt() ? ActionResultType.SUCCESS : ActionResultType.FAIL;
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if(!player.isSneaking()) {
            if(world.isRemote) return ActionResult.resultFail(stack);

            // Right click: Place angel block
            //ConstructionWand.LOGGER.debug("Place angel block");
            WandJob job = new AngelJob(player, world, stack);
            return job.doIt() ? ActionResult.resultSuccess(stack) : ActionResult.resultFail(stack);
        }
        return ActionResult.resultFail(stack);
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
        return getLimit();
    }

    protected int getLimit() {
        return ConfigServer.getWandProperties(this).getLimit();
    }

    public static int getWandMode(ItemStack stack) {
        WandOptions options = new WandOptions(stack);
        return options.mode.get().ordinal();
    }

    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack itemstack, World worldIn, List<ITextComponent> lines, ITooltipFlag extraInfo) {
        ItemWand wand = (ItemWand) itemstack.getItem();
        WandOptions options = new WandOptions(itemstack);

        String langTooltip = ConstructionWand.MODID + ".tooltip.";

        if(Screen.hasShiftDown()) {
            for(int i=1; i<options.allOptions.length; i++) {
                IOption<?> opt = options.allOptions[i];
                lines.add(new TranslationTextComponent(opt.getKeyTranslation()).mergeStyle(TextFormatting.AQUA)
                        .append(new TranslationTextComponent(opt.getValueTranslation()).mergeStyle(TextFormatting.GRAY))
                );
            }
        }
        else {
            IOption<?> opt = options.allOptions[0];
            lines.add(new TranslationTextComponent(langTooltip + "blocks", getLimit()).mergeStyle(TextFormatting.GRAY));
            lines.add(new TranslationTextComponent(opt.getKeyTranslation()).mergeStyle(TextFormatting.AQUA)
                    .append(new TranslationTextComponent(opt.getValueTranslation()).mergeStyle(TextFormatting.WHITE)));
            lines.add(new TranslationTextComponent(langTooltip + "shift").mergeStyle(TextFormatting.AQUA));
        }
    }

    public static void optionMessage(PlayerEntity player, IOption<?> option) {
        player.sendStatusMessage(
                new TranslationTextComponent(option.getKeyTranslation()).mergeStyle(TextFormatting.AQUA)
                        .append(new TranslationTextComponent(option.getValueTranslation()).mergeStyle(TextFormatting.WHITE))
                        .append(new StringTextComponent(" - ").mergeStyle(TextFormatting.GRAY))
                        .append(new TranslationTextComponent(option.getDescTranslation()).mergeStyle(TextFormatting.WHITE))
                , true);
    }
}
