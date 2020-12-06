package thetadev.constructionwand.basics;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigClient
{
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

	public static final ForgeConfigSpec.BooleanValue SHIFTCTRL_MODE;
	public static final ForgeConfigSpec.BooleanValue SHIFTCTRL_GUI;

	static {
		BUILDER.comment("This is the Client config for ConstructionWand.",
				"If you're not familiar with Forge's new split client/server config, let me explain:",
				"Client config is stored in the /config folder and only contains client specific settings like graphics and keybinds.",
				"Mod behavior is configured in the Server config, which is world-specific and thus located",
				"in the /saves/myworld/serverconfig folder. If you want to change the serverconfig for all",
				"new worlds, copy the config files in the /defaultconfigs folder.");

		BUILDER.push("keys");
		BUILDER.comment("Press SHIFT+CTRL instead of SHIFT for changing wand mode/direction lock");
		SHIFTCTRL_MODE = BUILDER.define("ShiftCtrl", false);
		BUILDER.comment("Press SHIFT+CTRL instead of SHIFT for opening wand GUI");
		SHIFTCTRL_GUI = BUILDER.define("ShiftCtrlGUI", false);
		BUILDER.pop();
	}

	public static final ForgeConfigSpec SPEC = BUILDER.build();
}
