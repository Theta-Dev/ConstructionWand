package thetadev.constructionwand.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

import static thetadev.constructionwand.ConstructionWand.MODID;

public final class ModMessages {
    private static SimpleChannel INSTANCE;
    private static final int PROTOCOL_VERSION = 1;

    private ModMessages() {
    }

    public static void register() {
        INSTANCE = ChannelBuilder.named(new ResourceLocation(MODID, "main")).networkProtocolVersion(PROTOCOL_VERSION).simpleChannel();
        int packetIndex = 0;

        // Server -> Client
        INSTANCE.messageBuilder(PacketUndoBlocks.class, packetIndex++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(PacketUndoBlocks::encode)
                .decoder(PacketUndoBlocks::decode)
                .consumerMainThread(PacketUndoBlocks.Handler::handle)
                .add();

        // Client -> Server
        INSTANCE.messageBuilder(PacketQueryUndo.class, packetIndex++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(PacketQueryUndo::encode)
                .decoder(PacketQueryUndo::decode)
                .consumerMainThread(PacketQueryUndo.Handler::handle)
                .add();
        INSTANCE.messageBuilder(PacketWandOption.class, packetIndex, NetworkDirection.PLAY_TO_SERVER)
                .encoder(PacketWandOption::encode)
                .decoder(PacketWandOption::decode)
                .consumerMainThread(PacketWandOption.Handler::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.send(message, PacketDistributor.SERVER.noArg());
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(message, PacketDistributor.PLAYER.with(player));
    }
}
