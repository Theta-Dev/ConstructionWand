package thetadev.constructionwand.basics;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import thetadev.constructionwand.ConstructionWand;

public class ModStats
{
    public static final ResourceLocation USE_WAND = new ResourceLocation(ConstructionWand.MODID, "use_wand");

    public static void register() {
        registerStat(USE_WAND);
    }

    private static void registerStat(ResourceLocation registryName) {
        // Compare with net.minecraft.stats.Stats#registerCustom
        Registry.register(Registry.CUSTOM_STAT, registryName.getPath(), registryName);
        Stats.CUSTOM.get(registryName, StatFormatter.DEFAULT);
    }
}
