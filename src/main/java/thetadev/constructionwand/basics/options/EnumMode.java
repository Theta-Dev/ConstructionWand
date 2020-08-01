package thetadev.constructionwand.basics.options;

import com.google.common.base.Enums;

public enum EnumMode implements IEnumOption
{
	DEFAULT,
	ANGEL;

	private static EnumMode[] vals = values();

	public IEnumOption fromName(String name) {
		return Enums.getIfPresent(EnumMode.class, name.toUpperCase()).or(this);
	}

	public EnumMode next(boolean dir) {
		int i = this.ordinal() + (dir ? 1:-1);
		if(i < 0) i += vals.length;

		return vals[i % vals.length];
	}

	public int getOrdinal() {
		return ordinal();
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
