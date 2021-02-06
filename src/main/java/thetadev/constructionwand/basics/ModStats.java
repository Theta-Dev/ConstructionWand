package thetadev.constructionwand.basics;

import net.minecraft.stats.IStatFormatter;
import net.minecraft.stats.Stats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
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
        Stats.CUSTOM.get(registryName, IStatFormatter.DEFAULT);
    }
}
