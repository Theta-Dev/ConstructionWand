package thetadev.constructionwand.basics;

import com.google.common.base.Enums;

public enum EnumMode implements IEnumOption
{
	DEFAULT,
	ANGEL;

	private static EnumMode[] vals = values();

	public IEnumOption fromName(String name) {
		return Enums.getIfPresent(EnumMode.class, name.toUpperCase()).or(this);
	}

	public EnumMode next() {
		return vals[(this.ordinal()+1) % vals.length];
	}

	public String getOptionKey() {
		return "mode";
	}

	public String getValue() {
		return this.name().toLowerCase();
	}

	public String getTranslationKey() {
		return getOptionKey() + "." + getValue();
	}
}
