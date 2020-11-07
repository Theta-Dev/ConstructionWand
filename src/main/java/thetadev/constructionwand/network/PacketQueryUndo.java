package thetadev.constructionwand.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import thetadev.constructionwand.ConstructionWand;

public class PacketQueryUndo
{
    public static final Identifier ID = ConstructionWand.loc("query_undo");

    public boolean undoPressed;

    public PacketQueryUndo(boolean undoPressed) {
        this.undoPressed = undoPressed;
    }

    public PacketByteBuf encode() {
        PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
        buffer.writeBoolean(undoPressed);
        return buffer;
    }

    public static PacketQueryUndo decode(PacketByteBuf buffer) {
        return new PacketQueryUndo(buffer.readBoolean());
    }

    public static void handle(PacketContext ctx, PacketByteBuf buffer) {
        //if(!ctx.get().getDirection().getReceptionSide().isServer()) return;
        ConstructionWand.LOGGER.debug("PacketQueryUndo" + ctx.getPacketEnvironment());

        PlayerEntity player = ctx.getPlayer();
        if(player == null) return;

        PacketQueryUndo msg = decode(buffer);
        ConstructionWand.instance.undoHistory.updateClient(player, msg.undoPressed);

        //ConstructionWand.LOGGER.debug("Undo queried");
    }
}
