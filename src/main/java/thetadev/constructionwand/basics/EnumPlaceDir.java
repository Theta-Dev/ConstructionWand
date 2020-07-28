package thetadev.constructionwand.basics;

public enum EnumPlaceDir
{
	PLAYER(1),
	TARGET(2);

	public final int mask;

	EnumPlaceDir(int mask) { this.mask = mask; }

	public static EnumPlaceDir fromMask(int mask) {
		EnumPlaceDir dirs[] = {PLAYER, TARGET};

		int safeDir = WandUtil.range(mask, 0, dirs.length-1);
		return dirs[safeDir];
	}
}
