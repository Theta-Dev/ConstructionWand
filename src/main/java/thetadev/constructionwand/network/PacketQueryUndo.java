package thetadev.constructionwand.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import thetadev.constructionwand.ConstructionWand;

import java.util.function.Supplier;

public class PacketQueryUndo
{
	public static void encode(PacketQueryUndo msg, PacketBuffer buffer) { }

	public static PacketQueryUndo decode(PacketBuffer buffer) {
		return new PacketQueryUndo();
	}

	public static class Handler
	{
		public static void handle(final PacketQueryUndo msg, final Supplier<NetworkEvent.Context> ctx) {
			if(!ctx.get().getDirection().getReceptionSide().isServer()) return;

			ServerPlayerEntity player = ctx.get().getSender();
			if(player == null) return;

			ConstructionWand.instance.jobHistory.updateClient(player);

			ConstructionWand.LOGGER.debug("Undo queried");
		}
	}
}
