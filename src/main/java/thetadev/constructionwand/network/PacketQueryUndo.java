package thetadev.constructionwand.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import thetadev.constructionwand.ConstructionWand;

import java.util.function.Supplier;

public class PacketQueryUndo
{
    public boolean undoPressed;

    public PacketQueryUndo(boolean undoPressed) {
        this.undoPressed = undoPressed;
    }

    public static void encode(PacketQueryUndo msg, PacketBuffer buffer) {
        buffer.writeBoolean(msg.undoPressed);
    }

    public static PacketQueryUndo decode(PacketBuffer buffer) {
        return new PacketQueryUndo(buffer.readBoolean());
    }

    public static class Handler
    {
        public static void handle(final PacketQueryUndo msg, final Supplier<NetworkEvent.Context> ctx) {
            if(!ctx.get().getDirection().getReceptionSide().isServer()) return;

            ServerPlayerEntity player = ctx.get().getSender();
            if(player == null) return;

            ConstructionWand.instance.undoHistory.updateClient(player, msg.undoPressed);

            //ConstructionWand.LOGGER.debug("Undo queried");
        }
    }
}
