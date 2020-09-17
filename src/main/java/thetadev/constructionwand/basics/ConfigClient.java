package thetadev.constructionwand.basics;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigClient
{
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

	public static final ForgeConfigSpec.BooleanValue SHIFTCTRL;

	static {
		BUILDER.push("keys");
		BUILDER.comment("Press SHIFT+CTRL to show wand options / scroll to change direction lock");
		SHIFTCTRL = BUILDER.define("ShiftCtrl", false);
		BUILDER.pop();
	}

	public static final ForgeConfigSpec SPEC = BUILDER.build();
}
