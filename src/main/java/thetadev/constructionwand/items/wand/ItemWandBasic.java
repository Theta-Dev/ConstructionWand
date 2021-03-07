package thetadev.constructionwand.items.wand;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import thetadev.constructionwand.basics.ConfigServer;

import javax.annotation.Nonnull;

public class ItemWandBasic extends ItemWand
{
    private final IItemTier tier;

    public ItemWandBasic(String name, Properties properties, IItemTier tier) {
        super(name, properties.maxDamage(tier.getMaxUses()));
        this.tier = tier;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return ConfigServer.getWandProperties(this).getDurability();
    }

    @Override
    public int remainingDurability(ItemStack stack) {
        return stack.getMaxDamage() - stack.getDamage();
    }

    @Override
    public boolean getIsRepairable(@Nonnull ItemStack toRepair, @Nonnull ItemStack repair) {
        return this.tier.getRepairMaterial().test(repair);
    }
}
