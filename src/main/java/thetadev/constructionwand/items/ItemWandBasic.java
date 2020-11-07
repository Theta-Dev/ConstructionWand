package thetadev.constructionwand.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import thetadev.constructionwand.ConstructionWand;

public class ItemWandBasic extends ItemWand
{
	private final ToolMaterial tier;

	public ItemWandBasic(String name, ToolMaterial tier) {
		super(name, new Settings().maxDamage(ConstructionWand.instance.config.getWandDurability(name)));
		this.tier = tier;
	}

	@Override
	public int getLimit(PlayerEntity player, ItemStack stack) {
		return Math.min(stack.getMaxDamage() - stack.getDamage(), getLimit());
	}

	@Override
	public boolean canRepair(ItemStack toRepair, ItemStack repair) {
		return this.tier.getRepairIngredient().test(repair);
	}
}
