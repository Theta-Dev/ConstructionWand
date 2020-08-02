package thetadev.constructionwand.basics.options;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
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
			EnumReplace.YES
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
		if(nextOption == EnumMode.ANGEL && item.angelDistance == 0) nextOption = EnumMode.DEFAULT;
		setOption(nextOption);
		return nextOption;
	}

	public IEnumOption nextOption(IEnumOption option) {
		return nextOption(option, true);
	}
}
