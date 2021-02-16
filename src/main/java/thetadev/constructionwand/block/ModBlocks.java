package thetadev.constructionwand.block;

import net.minecraft.block.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBlocks
{
    public static final Block CONJURED_BLOCK = new BlockConjured("conjured_block");

    public static final Block[] ALL_BLOCKS = {
            CONJURED_BLOCK
    };

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(ALL_BLOCKS);
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderLayers() {
        RenderTypeLookup.setRenderLayer(CONJURED_BLOCK, RenderType.getTranslucent());
    }
}
