package thetadev.constructionwand.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import static thetadev.constructionwand.ConstructionWand.MODID;

public final class ModMessages {

    private ModMessages() {
    }

    public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(MODID);

        registrar.playToClient(PacketUndoBlocks.ID, PacketUndoBlocks.CODEC, PacketUndoBlocks.Handler::handle);
        registrar.playToServer(PacketQueryUndo.ID, PacketQueryUndo.CODEC, PacketQueryUndo.Handler::handle);
        registrar.playToServer(PacketWandOption.ID, PacketWandOption.CODEC, PacketWandOption.Handler::handle);
    }

    public static void sendToServer(CustomPacketPayload message) {
        PacketDistributor.sendToServer(message);
    }

    public static void sendToPlayer(CustomPacketPayload message, ServerPlayer player) {
        player.connection.send(message);
    }
}
