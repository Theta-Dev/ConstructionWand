package thetadev.constructionwand.basics.option;

import com.google.common.base.Enums;
import net.minecraft.nbt.CompoundNBT;

public class OptionEnum<E extends Enum<E>> implements IOption<E>
{
	private final CompoundNBT tag;
	private final String key;
	private final Class<E> enumClass;
	private final boolean enabled;
	private final E dval;
	private E value;

	public OptionEnum(CompoundNBT tag, String key, Class<E> enumClass, E dval, boolean enabled) {
		this.tag = tag;
		this.key = key;
		this.enumClass = enumClass;
		this.enabled = enabled;
		this.dval = dval;

		value = Enums.getIfPresent(enumClass, tag.getString(key).toUpperCase()).or(dval);
	}

	public OptionEnum(CompoundNBT tag, String key, Class<E> enumClass, E dval) {
		this(tag, key, enumClass, dval, true);
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String getValueString() {
		return value.name().toLowerCase();
	}

	@Override
	public void setValueString(String val) {
		set(Enums.getIfPresent(enumClass, val.toUpperCase()).or(dval));
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void set(E val) {
		if(!enabled) return;
		value = val;
		tag.putString(key, getValueString());
	}

	@Override
	public E get() {
		return value;
	}

	@Override
	public E next(boolean dir) {
		E[] enumValues = enumClass.getEnumConstants();
		int i = value.ordinal() + (dir ? 1:-1);
		if(i < 0) i += enumValues.length;
		set(enumValues[i % enumValues.length]);
		return value;
	}
}
