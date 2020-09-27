package thetadev.constructionwand.basics;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigClient
{
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

	public static final ForgeConfigSpec.BooleanValue SHIFTCTRL_MODE;
	public static final ForgeConfigSpec.BooleanValue SHIFTCTRL_GUI;

	static {
		BUILDER.push("keys");
		BUILDER.comment("Press SHIFT+CTRL instead of SHIFT for changing wand mode/direction lock");
		SHIFTCTRL_MODE = BUILDER.define("ShiftCtrl", false);
		BUILDER.comment("Press SHIFT+CTRL instead of SHIFT for opening wand GUI");
		SHIFTCTRL_GUI = BUILDER.define("ShiftCtrlGUI", false);
		BUILDER.pop();
	}

	public static final ForgeConfigSpec SPEC = BUILDER.build();
}
