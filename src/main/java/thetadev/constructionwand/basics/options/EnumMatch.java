package thetadev.constructionwand.basics.options;

import com.google.common.base.Enums;

public enum EnumMatch implements IEnumOption
{
	EXACT,
	SIMILAR,
	ANY;

	private static EnumMatch[] vals = values();

	public IEnumOption fromName(String name) {
		return Enums.getIfPresent(EnumMatch.class, name.toUpperCase()).or(this);
	}

	public EnumMatch next(boolean dir) {
		int i = this.ordinal() + (dir ? 1:-1);
		if(i < 0) i += vals.length;

		return vals[i % vals.length];
	}

	public int getOrdinal() {
		return ordinal();
	}

	public String getOptionKey() {
		return "match";
	}

	public String getValue() {
		return this.name().toLowerCase();
	}
}