package thetadev.constructionwand;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thetadev.constructionwand.basics.ConfigClient;
import thetadev.constructionwand.basics.ConfigServer;
import thetadev.constructionwand.basics.ModStats;
import thetadev.constructionwand.basics.ReplacementRegistry;
import thetadev.constructionwand.client.ClientEvents;
import thetadev.constructionwand.client.RenderBlockPreview;
import thetadev.constructionwand.containers.ContainerManager;
import thetadev.constructionwand.containers.ContainerRegistrar;
import thetadev.constructionwand.items.ModItems;
import thetadev.constructionwand.network.PacketQueryUndo;
import thetadev.constructionwand.network.PacketUndoBlocks;
import thetadev.constructionwand.network.PacketWandOption;
import thetadev.constructionwand.wand.undo.UndoHistory;


@Mod(ConstructionWand.MODID)
public class ConstructionWand
{
    public static final String MODID = "constructionwand";
    public static final String MODNAME = "ConstructionWand";

    public static ConstructionWand instance;
    public static final Logger LOGGER = LogManager.getLogger();
    private static final String PROTOCOL_VERSION = "1";
    public SimpleChannel HANDLER;

    public ContainerManager containerManager;
    public UndoHistory undoHistory;
    public RenderBlockPreview renderBlockPreview;

    public ConstructionWand() {
        instance = this;

        containerManager = new ContainerManager();
        undoHistory = new UndoHistory();

        // Register setup methods for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        MinecraftForge.EVENT_BUS.register(this);

        // Config setup
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ConfigServer.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigClient.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("ConstructionWand says hello - may the odds be ever in your favor.");

        // Register packets
        HANDLER = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
        int packetIndex = 0;
        HANDLER.registerMessage(packetIndex++, PacketUndoBlocks.class, PacketUndoBlocks::encode, PacketUndoBlocks::decode, PacketUndoBlocks.Handler::handle);
        HANDLER.registerMessage(packetIndex++, PacketQueryUndo.class, PacketQueryUndo::encode, PacketQueryUndo::decode, PacketQueryUndo.Handler::handle);
        HANDLER.registerMessage(packetIndex, PacketWandOption.class, PacketWandOption::encode, PacketWandOption::decode, PacketWandOption.Handler::handle);

        // Container registry
        ContainerRegistrar.register();

        //Replacement registry
        ReplacementRegistry.init();

        // Stats
        ModStats.register();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        renderBlockPreview = new RenderBlockPreview();
        MinecraftForge.EVENT_BUS.register(renderBlockPreview);
        MinecraftForge.EVENT_BUS.register(new ClientEvents());

        event.enqueueWork(ModItems::registerModelProperties);
        event.enqueueWork(ModItems::registerItemColors);
    }

    public static ResourceLocation loc(String name) {
        return new ResourceLocation(MODID, name);
    }
}
