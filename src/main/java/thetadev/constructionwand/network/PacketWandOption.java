package thetadev.constructionwand.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import thetadev.constructionwand.basics.WandUtil;
import thetadev.constructionwand.basics.options.IEnumOption;
import thetadev.constructionwand.basics.options.WandOptions;
import thetadev.constructionwand.items.ItemWand;

import java.util.function.Supplier;

public class PacketWandOption
{
	public final IEnumOption option;
	public final boolean notify;

	public PacketWandOption(IEnumOption option, boolean notify) {
		this.option = option;
		this.notify = notify;
	}

	public static void encode(PacketWandOption msg, PacketBuffer buffer) {
		buffer.writeString(msg.option.getOptionKey());
		buffer.writeString(msg.option.getValue());
		buffer.writeBoolean(msg.notify);
	}

	public static PacketWandOption decode(PacketBuffer buffer) {
		String key = buffer.readString(100);
		String val = buffer.readString(100);

		boolean notify = buffer.readBoolean();

		return new PacketWandOption(WandOptions.fromKey(key).fromName(val), notify);
	}

	public static class Handler
	{
		public static void handle(final PacketWandOption msg, final Supplier<NetworkEvent.Context> ctx) {
			if(!ctx.get().getDirection().getReceptionSide().isServer()) return;

			ServerPlayerEntity player = ctx.get().getSender();
			if(player == null) return;

			ItemStack wand = WandUtil.holdingWand(player);
			if(wand == null) return;
			WandOptions options = new WandOptions(wand);
			options.setOption(msg.option);

			if(msg.notify) ItemWand.optionMessage(player, msg.option);

			player.inventory.markDirty();
		}
	}
}
