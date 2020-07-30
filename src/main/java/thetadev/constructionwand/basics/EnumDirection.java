package thetadev.constructionwand.basics;

import com.google.common.base.Enums;

public enum EnumDirection implements IEnumOption
{
	TARGET,
	PLAYER;

	private static EnumDirection[] vals = values();

	public IEnumOption fromName(String name) {
		return Enums.getIfPresent(EnumDirection.class, name.toUpperCase()).or(this);
	}

	public EnumDirection next() {
		return vals[(this.ordinal()+1) % vals.length];
	}

	public String getOptionKey() {
		return "direction";
	}

	public String getValue() {
		return this.name().toLowerCase();
	}

	public String getTranslationKey() {
		return getOptionKey() + "." + getValue();
	}
}
