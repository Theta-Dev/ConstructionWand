package thetadev.constructionwand.basics.option;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import thetadev.constructionwand.basics.ConfigServer;
import thetadev.constructionwand.items.ItemWand;

import javax.annotation.Nullable;

public class WandOptions
{
	public final CompoundNBT tag;

	private static final String TAG_ROOT = "wand_options";

	public enum MODE
	{
		DEFAULT,
		ANGEL;
	}
	public enum LOCK
	{
		HORIZONTAL,
		VERTICAL,
		NORTHSOUTH,
		EASTWEST,
		NOLOCK;
	}
	public enum DIRECTION
	{
		TARGET,
		PLAYER;
	}
	public enum MATCH
	{
		EXACT,
		SIMILAR,
		ANY;
	}

	public final OptionEnum<MODE> mode;
	public final OptionEnum<LOCK> lock;
	public final OptionEnum<DIRECTION> direction;
	public final OptionBoolean replace;
	public final OptionEnum<MATCH> match;
	public final OptionBoolean random;

	public final IOption<?>[] allOptions;

	public WandOptions(ItemStack wandStack) {
		ItemWand wand = (ItemWand) wandStack.getItem();
		tag = wandStack.getOrCreateChildTag(TAG_ROOT);

		mode = new OptionEnum<>(tag, "mode", MODE.class, MODE.DEFAULT, ConfigServer.getWandProperties(wand).getAngel() > 0);
		lock = new OptionEnum<>(tag, "lock", LOCK.class, LOCK.NOLOCK);
		direction = new OptionEnum<>(tag, "direction", DIRECTION.class, DIRECTION.TARGET);
		replace = new OptionBoolean(tag, "replace", true);
		match = new OptionEnum<>(tag, "match", MATCH.class, MATCH.SIMILAR);
		random = new OptionBoolean(tag, "random", false);

		allOptions = new IOption[]{mode, lock, direction, replace, match, random};
	}

	@Nullable
	public IOption<?> get(String key){
		for(IOption<?> option : allOptions) {
			if(option.getKey().equals(key)) return option;
		}
		return null;
	}

	public boolean testLock(LOCK l) {
		if(lock.get() == LOCK.NOLOCK) return true;
		return lock.get() == l;
	}
}
