package thetadev.constructionwand.basics.options;

import com.google.common.base.Enums;

public enum EnumLock implements IEnumOption
{
	HORIZONTAL,
	VERTICAL,
	NORTHSOUTH,
	EASTWEST,
	NOLOCK;

	private static EnumLock[] vals = values();

	public IEnumOption fromName(String name) {
		return Enums.getIfPresent(EnumLock.class, name.toUpperCase()).or(this);
	}

	public EnumLock next(boolean dir) {
		int i = this.ordinal() + (dir ? 1:-1);
		if(i < 0) i += vals.length;

		return vals[i % vals.length];
	}

	public int getOrdinal() {
		return ordinal();
	}

	public String getOptionKey() {
		return "lock";
	}

	public String getValue() {
		return this.name().toLowerCase();
	}

	public boolean test(EnumLock lock) {
		if(this == NOLOCK) return true;
		return this == lock;
	}
}
