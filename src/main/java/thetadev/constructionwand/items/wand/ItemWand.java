package thetadev.constructionwand.items.wand;

import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
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
import net.minecraftforge.client.model.generators.ModelFile;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.api.IWandCore;
import thetadev.constructionwand.basics.WandUtil;
import thetadev.constructionwand.basics.option.IOption;
import thetadev.constructionwand.basics.option.WandOptions;
import thetadev.constructionwand.data.ICustomItemModel;
import thetadev.constructionwand.data.ItemModelGenerator;
import thetadev.constructionwand.items.ItemBase;
import thetadev.constructionwand.wand.WandJob;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class ItemWand extends ItemBase implements ICustomItemModel
{
    public ItemWand(String name, Properties properties) {
        super(name, properties);
    }

    @Nonnull
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        Hand hand = context.getHand();
        World world = context.getWorld();

        if(world.isRemote || player == null) return ActionResultType.FAIL;

        ItemStack stack = player.getHeldItem(hand);

        if(player.isSneaking() && ConstructionWand.instance.undoHistory.isUndoActive(player)) {
            return ConstructionWand.instance.undoHistory.undo(player, world, context.getPos()) ? ActionResultType.SUCCESS : ActionResultType.FAIL;
        }
        else {
            WandJob job = getWandJob(player, world, new BlockRayTraceResult(context.getHitVec(), context.getFace(), context.getPos(), false), stack);
            return job.doIt() ? ActionResultType.SUCCESS : ActionResultType.FAIL;
        }
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, PlayerEntity player, @Nonnull Hand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if(!player.isSneaking()) {
            if(world.isRemote) return ActionResult.resultFail(stack);

            // Right click: Place angel block
            WandJob job = getWandJob(player, world, BlockRayTraceResult.createMiss(player.getLookVec(),
                    WandUtil.fromVector(player.getLookVec()), WandUtil.playerPos(player)), stack);
            return job.doIt() ? ActionResult.resultSuccess(stack) : ActionResult.resultFail(stack);
        }
        return ActionResult.resultFail(stack);
    }

    public static WandJob getWandJob(PlayerEntity player, World world, @Nullable BlockRayTraceResult rayTraceResult, ItemStack wand) {
        WandJob wandJob = new WandJob(player, world, rayTraceResult, wand);
        wandJob.getPlaceSnapshots();

        return wandJob;
    }

    @Override
    public boolean canHarvestBlock(@Nonnull BlockState blockIn) {
        return false;
    }

    @Override
    public boolean getIsRepairable(@Nonnull ItemStack toRepair, @Nonnull ItemStack repair) {
        return false;
    }

    public int remainingDurability(ItemStack stack) {
        return Integer.MAX_VALUE;
    }

    @OnlyIn(Dist.CLIENT)
    public void addInformation(@Nonnull ItemStack itemstack, World worldIn, @Nonnull List<ITextComponent> lines, @Nonnull ITooltipFlag extraInfo) {
        WandOptions options = new WandOptions(itemstack);
        int limit = options.cores.get().getWandAction().getLimit(itemstack);

        String langTooltip = ConstructionWand.MODID + ".tooltip.";

        // +SHIFT tooltip: show all options + installed cores
        if(Screen.hasShiftDown()) {
            for(int i = 1; i < options.allOptions.length; i++) {
                IOption<?> opt = options.allOptions[i];
                lines.add(new TranslationTextComponent(opt.getKeyTranslation()).mergeStyle(TextFormatting.AQUA)
                        .append(new TranslationTextComponent(opt.getValueTranslation()).mergeStyle(TextFormatting.GRAY))
                );
            }
            if(!options.cores.getUpgrades().isEmpty()) {
                lines.add(new StringTextComponent(""));
                lines.add(new TranslationTextComponent(langTooltip + "cores").mergeStyle(TextFormatting.GRAY));

                for(IWandCore core : options.cores.getUpgrades()) {
                    lines.add(new TranslationTextComponent(options.cores.getKeyTranslation() + "." + core.getRegistryName().toString()));
                }
            }
        }
        // Default tooltip: show block limit + active wand core
        else {
            IOption<?> opt = options.allOptions[0];
            lines.add(new TranslationTextComponent(langTooltip + "blocks", limit).mergeStyle(TextFormatting.GRAY));
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

    @Override
    public void generateCustomItemModel(ItemModelGenerator generator, String name) {
        ModelFile wandWithCore = generator.withExistingParent(name + "_core", "item/handheld")
                .texture("layer0", generator.modLoc("item/" + name))
                .texture("layer1", generator.modLoc("item/overlay_core"));

        generator.withExistingParent(name, "item/handheld")
                .texture("layer0", generator.modLoc("item/" + name))
                .override()
                .predicate(generator.modLoc("using_core"), 1)
                .model(wandWithCore).end();

    }
}
