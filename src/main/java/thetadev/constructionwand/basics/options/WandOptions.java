package thetadev.constructionwand.basics.options;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import thetadev.constructionwand.basics.ConfigServer;
import thetadev.constructionwand.items.ItemWand;

public class WandOptions
{
	private ItemWand item;
	private CompoundNBT tag;

	private final String TAG_ROOT = "wand_options";

	public static final IEnumOption[] options = {
			EnumMode.DEFAULT,
			EnumLock.NOLOCK,
			EnumDirection.TARGET,
			EnumReplace.YES,
			EnumMatch.SIMILAR,
			EnumRandom.NO
	};

	public WandOptions(ItemStack stack) {
		this.item = (ItemWand) stack.getItem();
		this.tag = stack.getOrCreateChildTag(TAG_ROOT);
	}

	public IEnumOption getOption(IEnumOption option) {
		return option.fromName(tag.getString(option.getOptionKey()));
	}

	public void setOption(IEnumOption option) {
		tag.putString(option.getOptionKey(), option.getValue());
	}

	public IEnumOption nextOption(IEnumOption option, boolean dir) {
		IEnumOption nextOption = getOption(option).next(dir);
		if(nextOption == EnumMode.ANGEL && ConfigServer.getWandProperties(item).getAngel() == 0) nextOption = EnumMode.DEFAULT;
		setOption(nextOption);
		return nextOption;
	}

	public IEnumOption nextOption(IEnumOption option) {
		return nextOption(option, true);
	}

	public static IEnumOption fromKey(String key) {
		for(IEnumOption option : options) {
			if(option.getOptionKey().equals(key)) return option;
		}
		return EnumMode.DEFAULT;
	}
}
