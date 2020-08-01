package thetadev.constructionwand.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import thetadev.constructionwand.ConstructionWand;

import java.util.LinkedList;
import java.util.function.Supplier;

public class PacketUndoBlocks
{
	public LinkedList<BlockPos> undoBlocks;

	public PacketUndoBlocks(LinkedList<BlockPos> undoBlocks) {
		this.undoBlocks = undoBlocks;
	}

	public static void encode(PacketUndoBlocks msg, PacketBuffer buffer) {
		for(BlockPos pos : msg.undoBlocks) {
			buffer.writeBlockPos(pos);
		}
	}

	public static PacketUndoBlocks decode(PacketBuffer buffer) {
		LinkedList<BlockPos> undoBlocks = new LinkedList<>();

		while(buffer.isReadable()) {
			undoBlocks.add(buffer.readBlockPos());
		}
		return new PacketUndoBlocks(undoBlocks);
	}

	public static class Handler {
		public static void handle(final PacketUndoBlocks msg, final Supplier<NetworkEvent.Context> ctx) {
			if(!ctx.get().getDirection().getReceptionSide().isClient()) return;

			ConstructionWand.LOGGER.debug("PacketUndoBlocks received, Blocks: " + msg.undoBlocks.size());
			ConstructionWand.instance.renderBlockPreview.undoBlocks = msg.undoBlocks;

			ctx.get().setPacketHandled(true);
		}
	}
}
