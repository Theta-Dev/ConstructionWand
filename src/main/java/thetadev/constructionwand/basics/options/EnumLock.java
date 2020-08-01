package thetadev.constructionwand.basics.options;

import com.google.common.base.Enums;

public enum EnumLock implements IEnumOption
{
	HORIZONTAL(1),
	VERTICAL(2),
	TOP_BOTTOM(4),
	NOLOCK(7);

	private static EnumLock[] vals = values();

	public final int mask;

	public final static int M_HOR = 1;
	public final static int M_VERT = 2;
	public final static int M_TB = 4;

	EnumLock(int mask) {
		this.mask = mask;
	}

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

	public String getTranslationKey() {
		return getOptionKey() + "." + getValue();
	}
}
