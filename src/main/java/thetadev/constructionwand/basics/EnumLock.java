package thetadev.constructionwand.basics;

public enum EnumLock {
	NORTHSOUTH(1),
	VERTICAL(2),
	VERTICALEASTWEST(3),
	EASTWEST(4),
	HORIZONTAL(5),
	VERTICALNORTHSOUTH(6),
	NOLOCK(7);

	public final int mask;

	public final static int NORTH_SOUTH_MASK = 1;
	public final static int UP_DOWN_MASK = 2;
	public final static int EAST_WEST_MASK = 4;

	EnumLock(int mask) {
		this.mask = mask;
	}

	public  static EnumLock fromMask(int inputMask) {
		EnumLock locks[] = { NORTHSOUTH, VERTICAL, VERTICALEASTWEST, EASTWEST, HORIZONTAL, VERTICALNORTHSOUTH, NOLOCK };

		int safeMask = inputMask & 7;
		return locks[safeMask - 1];
	}
}
