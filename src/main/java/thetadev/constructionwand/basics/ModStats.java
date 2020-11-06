package thetadev.constructionwand.basics;

import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import thetadev.constructionwand.ConstructionWand;

public class ModStats
{
	public static final Identifier USE_WAND = new Identifier(ConstructionWand.MODID, "use_wand");

	public static void register() {
		registerStat(USE_WAND);
	}

	private static void registerStat(Identifier registryName) {
		// Compare with net.minecraft.stats.Stats#registerCustom
		Registry.register(Registry.CUSTOM_STAT, registryName.getPath(), registryName);
		Stats.CUSTOM.getOrCreateStat(registryName, StatFormatter.DEFAULT.DEFAULT);
	}
}
