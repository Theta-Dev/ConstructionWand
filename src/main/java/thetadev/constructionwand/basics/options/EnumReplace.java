package thetadev.constructionwand.basics.options;

import com.google.common.base.Enums;

public enum EnumReplace implements IEnumOption
{
	YES,
	NO;

	private static EnumReplace[] vals = values();

	public IEnumOption fromName(String name) {
		return Enums.getIfPresent(EnumReplace.class, name.toUpperCase()).or(this);
	}

	public EnumReplace next(boolean dir) {
		int i = this.ordinal() + (dir ? 1:-1);
		if(i < 0) i += vals.length;

		return vals[i % vals.length];
	}

	public int getOrdinal() {
		return ordinal();
	}

	public String getOptionKey() {
		return "replace";
	}

	public String getValue() {
		return this.name().toLowerCase();
	}
}
