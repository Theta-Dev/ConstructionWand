package thetadev.constructionwand.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thetadev.constructionwand.ConstructionWand;

import static thetadev.constructionwand.ConstructionWand.MODID;

public record PacketQueryUndo(boolean undoPressed) implements CustomPacketPayload
{
    public static final StreamCodec<FriendlyByteBuf, PacketQueryUndo> CODEC = CustomPacketPayload.codec(
            PacketQueryUndo::encode,
            PacketQueryUndo::new);
    public static final Type<PacketQueryUndo> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "query_undo"));

    public PacketQueryUndo(FriendlyByteBuf buf) {
        this(buf.readBoolean());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(undoPressed);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static class Handler
    {
        public static void handle(final PacketQueryUndo msg, final IPayloadContext ctx) {
            ctx.enqueueWork(() -> {
                if (ctx.player() instanceof ServerPlayer player) {
                    ConstructionWand.instance.undoHistory.updateClient(player, msg.undoPressed);

                    //ConstructionWand.LOGGER.debug("Undo queried");
                }
            })
            .exceptionally(e -> {
                // Handle exception
                ctx.disconnect(Component.translatable("constructionwand.networking.query_undo.failed", e.getMessage()));
                return null;
            });
        }
    }
}
