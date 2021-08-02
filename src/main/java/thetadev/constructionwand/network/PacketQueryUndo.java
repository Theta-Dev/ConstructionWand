package thetadev.constructionwand.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import thetadev.constructionwand.ConstructionWand;

import java.util.function.Supplier;

public class PacketQueryUndo
{
    public boolean undoPressed;

    public PacketQueryUndo(boolean undoPressed) {
        this.undoPressed = undoPressed;
    }

    public static void encode(PacketQueryUndo msg, FriendlyByteBuf buffer) {
        buffer.writeBoolean(msg.undoPressed);
    }

    public static PacketQueryUndo decode(FriendlyByteBuf buffer) {
        return new PacketQueryUndo(buffer.readBoolean());
    }

    public static class Handler
    {
        public static void handle(final PacketQueryUndo msg, final Supplier<NetworkEvent.Context> ctx) {
            if(!ctx.get().getDirection().getReceptionSide().isServer()) return;

            ServerPlayer player = ctx.get().getSender();
            if(player == null) return;

            ConstructionWand.instance.undoHistory.updateClient(player, msg.undoPressed);

            //ConstructionWand.LOGGER.debug("Undo queried");
        }
    }
}
