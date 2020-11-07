package thetadev.constructionwand.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.ConstructionWandClient;

import java.util.HashSet;
import java.util.Set;

public class PacketUndoBlocks
{
	public static final Identifier ID = ConstructionWand.loc("undo_blocks");

	public HashSet<BlockPos> undoBlocks;

	public PacketUndoBlocks(Set<BlockPos> undoBlocks) {
		this.undoBlocks = new HashSet<>(undoBlocks);
	}
	private PacketUndoBlocks(HashSet<BlockPos> undoBlocks) {
		this.undoBlocks = undoBlocks;
	}

	public PacketByteBuf encode() {
		PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
		for(BlockPos pos : undoBlocks) {
			buffer.writeBlockPos(pos);
		}
		return buffer;
	}

	public static PacketUndoBlocks decode(PacketByteBuf buffer) {
		HashSet<BlockPos> undoBlocks = new HashSet<>();

		while(buffer.isReadable()) {
			undoBlocks.add(buffer.readBlockPos());
		}
		return new PacketUndoBlocks(undoBlocks);
	}

	public static void handle(PacketContext ctx, PacketByteBuf buffer) {
		//if(!ctx.get().getDirection().getReceptionSide().isClient()) return;

		//ConstructionWand.LOGGER.debug("PacketUndoBlocks received, Blocks: " + msg.undoBlocks.size());

		PacketUndoBlocks msg = decode(buffer);
		ConstructionWandClient.instance.renderBlockPreview.undoBlocks = msg.undoBlocks;
	}
}
