package thetadev.constructionwand.basics.options;

import com.google.common.base.Enums;

public enum EnumFluidLock implements IEnumOption
{
	IGNORE,
	STOPAT;

	private static EnumFluidLock[] vals = values();

	public IEnumOption fromName(String name) {
		return Enums.getIfPresent(EnumFluidLock.class, name.toUpperCase()).or(this);
	}

	public EnumFluidLock next(boolean dir) {
		int i = this.ordinal() + (dir ? 1:-1);
		if(i < 0) i += vals.length;

		return vals[i % vals.length];
	}

	public int getOrdinal() {
		return ordinal();
	}

	public String getOptionKey() {
		return "fluid";
	}

	public String getValue() {
		return this.name().toLowerCase();
	}

	public String getTranslationKey() {
		return getOptionKey() + "." + getValue();
	}
}
