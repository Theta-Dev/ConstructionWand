package thetadev.constructionwand.basics;

import com.google.common.base.Enums;

public enum EnumLock implements IEnumOption
{
	NORTHSOUTH(1),
	VERTICAL(2),
	VERTICALEASTWEST(3),
	EASTWEST(4),
	HORIZONTAL(5),
	VERTICALNORTHSOUTH(6),
	NOLOCK(7);

	private static EnumLock[] vals = values();

	public final int mask;

	public final static int NORTH_SOUTH_MASK = 1;
	public final static int UP_DOWN_MASK = 2;
	public final static int EAST_WEST_MASK = 4;

	EnumLock(int mask) {
		this.mask = mask;
	}

	public static EnumLock fromMask(int inputMask) {
		int safeMask = inputMask & 7;
		return vals[safeMask - 1];
	}

	public IEnumOption fromName(String name) {
		return Enums.getIfPresent(EnumLock.class, name.toUpperCase()).or(this);
	}

	public EnumLock next() {
		return vals[(this.ordinal()+1) % vals.length];
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
