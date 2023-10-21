package thetadev.constructionwand.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;
import thetadev.constructionwand.ConstructionWand;

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
        public static void handle(final PacketQueryUndo msg, final CustomPayloadEvent.Context ctx) {
            if(!ctx.getDirection().getReceptionSide().isServer()) return;

            ServerPlayer player = ctx.getSender();
            if(player == null) return;

            ConstructionWand.instance.undoHistory.updateClient(player, msg.undoPressed);

            //ConstructionWand.LOGGER.debug("Undo queried");
        }
    }
}
