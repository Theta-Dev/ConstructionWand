package thetadev.constructionwand;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.ConfigHolder;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thetadev.constructionwand.basics.ModConfig;
import thetadev.constructionwand.basics.ModStats;
import thetadev.constructionwand.basics.ReplacementRegistry;
import thetadev.constructionwand.containers.ContainerManager;
import thetadev.constructionwand.containers.ContainerRegistrar;
import thetadev.constructionwand.items.ModItems;
import thetadev.constructionwand.job.UndoHistory;
import thetadev.constructionwand.network.PacketQueryUndo;
import thetadev.constructionwand.network.PacketWandOption;


public class ConstructionWand implements ModInitializer
{
    public static final String MODID = "constructionwand";
    public static ConstructionWand instance;
    public static final Logger LOGGER = LogManager.getLogger();
    public ModConfig config;

    public ContainerManager containerManager;
    public UndoHistory undoHistory;

    public ConstructionWand() {
        instance = this;

        containerManager = new ContainerManager();
        undoHistory = new UndoHistory();
    }

    @Override
    public void onInitialize() {
        LOGGER.info("ConstructionWand says hello - may the odds be ever in your favor.");

        ConfigHolder<ModConfig> holder = AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
        config = holder.getConfig();

        AutoConfig.getConfigHolder(ModConfig.class).registerSaveListener((manager, data) -> {
            ConstructionWand.instance.config = data;
            return ActionResult.SUCCESS;
        });
        AutoConfig.getConfigHolder(ModConfig.class).registerLoadListener((manager, newData) -> {
            ConstructionWand.instance.config = newData;
            return ActionResult.SUCCESS;
        });

        ModItems.register();

        // Register packets
        ServerSidePacketRegistry.INSTANCE.register(PacketQueryUndo.ID, PacketQueryUndo::handle);
        ServerSidePacketRegistry.INSTANCE.register(PacketWandOption.ID, PacketWandOption::handle);

        // Container registry
        ContainerRegistrar.register();

        //Replacement registry
        ReplacementRegistry.init();

        // Stats
        ModStats.register();
    }

    public static Identifier loc(String name) {
        return new Identifier(MODID, name);
    }
}
