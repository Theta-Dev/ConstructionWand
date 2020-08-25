package thetadev.constructionwand.basics.options;

import com.google.common.base.Enums;

public enum EnumDirection implements IEnumOption
{
	TARGET,
	PLAYER;

	private static EnumDirection[] vals = values();

	public IEnumOption fromName(String name) {
		return Enums.getIfPresent(EnumDirection.class, name.toUpperCase()).or(this);
	}

	public EnumDirection next(boolean dir) {
		int i = this.ordinal() + (dir ? 1:-1);
		if(i < 0) i += vals.length;

		return vals[i % vals.length];
	}

	public int getOrdinal() {
		return ordinal();
	}

	public String getOptionKey() {
		return "direction";
	}

	public String getValue() {
		return this.name().toLowerCase();
	}
}
