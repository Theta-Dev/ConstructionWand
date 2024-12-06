package thetadev.constructionwand.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thetadev.constructionwand.ConstructionWand;

import java.util.HashSet;
import java.util.Set;

import static thetadev.constructionwand.ConstructionWand.MODID;

public record PacketUndoBlocks(HashSet<BlockPos> undoBlocks) implements CustomPacketPayload
{
    public static final StreamCodec<FriendlyByteBuf, PacketUndoBlocks> CODEC = CustomPacketPayload.codec(
            PacketUndoBlocks::encode,
            PacketUndoBlocks::new);
    public static final Type<PacketUndoBlocks> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "undo_blocks"));

    public PacketUndoBlocks(FriendlyByteBuf buffer) {
        this(getSet(buffer));
    }

    public PacketUndoBlocks(Set<BlockPos> undoBlocks) {
        this(new HashSet<>(undoBlocks));
    }

    private static HashSet<BlockPos> getSet(FriendlyByteBuf buffer) {
        HashSet<BlockPos> undoBlocks = new HashSet<>();

        while (buffer.isReadable()) {
            undoBlocks.add(buffer.readBlockPos());
        }
        return undoBlocks;
    }

    public void encode(FriendlyByteBuf buffer) {
        for(BlockPos pos : undoBlocks) {
            buffer.writeBlockPos(pos);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static PacketUndoBlocks decode(FriendlyByteBuf buffer) {
        HashSet<BlockPos> undoBlocks = new HashSet<>();

        while(buffer.isReadable()) {
            undoBlocks.add(buffer.readBlockPos());
        }
        return new PacketUndoBlocks(undoBlocks);
    }

    public static class Handler
    {
        public static void handle(final PacketUndoBlocks msg, final IPayloadContext ctx) {
            ctx.enqueueWork(() -> {
                //ConstructionWand.LOGGER.debug("PacketUndoBlocks received, Blocks: " + msg.undoBlocks.size());
                ConstructionWand.instance.renderBlockPreview.undoBlocks = msg.undoBlocks;
            })
            .exceptionally(e -> {
                // Handle exception
                ctx.disconnect(Component.translatable("constructionwand.networking.undo_blocks.failed", e.getMessage()));
                return null;
            });

        }
    }
}
