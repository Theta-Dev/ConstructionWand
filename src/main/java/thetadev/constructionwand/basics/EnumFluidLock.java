package thetadev.constructionwand.basics;

import com.google.common.base.Enums;

public enum EnumFluidLock implements IEnumOption
{
	IGNORE,
	STOPAT;

	private static EnumFluidLock[] vals = values();

	public IEnumOption fromName(String name) {
		return Enums.getIfPresent(EnumFluidLock.class, name.toUpperCase()).or(this);
	}

	public EnumFluidLock next() {
		return vals[(this.ordinal()+1) % vals.length];
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
