package thetadev.constructionwand.basics;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;
import thetadev.constructionwand.ConstructionWand;

import java.util.function.Supplier;

public class ModStats
{
    public static final DeferredRegister<ResourceLocation> CUSTOM_STATS = DeferredRegister.create(BuiltInRegistries.CUSTOM_STAT, ConstructionWand.MODID);

    public static final ResourceLocation USE_WAND = ResourceLocation.fromNamespaceAndPath(ConstructionWand.MODID, "use_wand");
    public static final Supplier<ResourceLocation> USE_WAND_STAT = CUSTOM_STATS.register("use_wand", () -> USE_WAND);
}
