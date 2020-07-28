package thetadev.constructionwand.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.basics.WandUtil;

import java.util.function.Supplier;

public class PacketWandOption
{
	public boolean keyMode;
	public boolean keyOption;

	public PacketWandOption(boolean keyMode, boolean keyOption)
	{
		this.keyMode = keyMode;
		this.keyOption = keyOption;
	}

	public static void encode(PacketWandOption msg, PacketBuffer buffer) {
		buffer.writeBoolean(msg.keyMode);
		buffer.writeBoolean(msg.keyOption);
	}

	public static PacketWandOption decode(PacketBuffer buffer) {
		boolean kMod = buffer.readBoolean();
		boolean kOpt = buffer.readBoolean();

		return new PacketWandOption(kMod, kOpt);
	}

	public static class Handler
	{
		public static void handle(final PacketWandOption msg, final Supplier<NetworkEvent.Context> ctx) {
			if(!ctx.get().getDirection().getReceptionSide().isServer()) return;

			ServerPlayerEntity player = ctx.get().getSender();
			if(player == null) return;

			ItemStack wand = WandUtil.holdingWand(player);
			if(wand == null) return;

			ConstructionWand.LOGGER.info("Keys MOD:"+msg.keyMode+" OPT:"+msg.keyOption);
		}
	}
}
