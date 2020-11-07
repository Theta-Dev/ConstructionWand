package thetadev.constructionwand;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import thetadev.constructionwand.basics.ModConfig;
import thetadev.constructionwand.client.RenderBlockPreview;
import thetadev.constructionwand.items.ModItems;
import thetadev.constructionwand.network.PacketUndoBlocks;

@Environment(EnvType.CLIENT)
public class ConstructionWandClient implements ClientModInitializer
{
    public static ConstructionWandClient instance;
    public RenderBlockPreview renderBlockPreview;

    public ConstructionWandClient() {
        instance = this;
    }

    @Override
    public void onInitializeClient() {
        AutoConfig.getGuiRegistry(ModConfig.class);
        renderBlockPreview = new RenderBlockPreview();
        ModItems.registerModelProperties();
        ClientSidePacketRegistry.INSTANCE.register(PacketUndoBlocks.ID, PacketUndoBlocks::handle);
    }
}
