package thetadev.constructionwand.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import thetadev.constructionwand.basics.ConfigServer;

public class ItemWandBasic extends ItemWand
{
	private final IItemTier tier;

	public ItemWandBasic(String name, IItemTier tier) {
		super(name, new Properties().maxDamage(tier.getMaxUses()));
		this.tier = tier;
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		return ConfigServer.getWandProperties(this).getDurability();
	}

	@Override
	public int getLimit(PlayerEntity player, ItemStack stack) {
		return Math.min(stack.getMaxDamage() - stack.getDamage(), getLimit());
	}

	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		return this.tier.getRepairMaterial().test(repair);
	}
}
