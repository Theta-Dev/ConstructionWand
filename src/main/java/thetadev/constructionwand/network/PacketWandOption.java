package thetadev.constructionwand.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import thetadev.constructionwand.basics.WandUtil;
import thetadev.constructionwand.basics.option.IOption;
import thetadev.constructionwand.basics.option.WandOptions;
import thetadev.constructionwand.items.ItemWand;

import java.util.function.Supplier;

public class PacketWandOption
{
	public final String key;
	public final String value;
	public final boolean notify;

	public PacketWandOption(IOption<?> option, boolean notify) {
		this(option.getKey(), option.getValueString(), notify);
	}

	private PacketWandOption(String key, String value, boolean notify) {
		this.key = key;
		this.value = value;
		this.notify = notify;
	}

	public static void encode(PacketWandOption msg, PacketBuffer buffer) {
		buffer.writeString(msg.key);
		buffer.writeString(msg.value);
		buffer.writeBoolean(msg.notify);
	}

	public static PacketWandOption decode(PacketBuffer buffer) {
		return new PacketWandOption(buffer.readString(100), buffer.readString(100), buffer.readBoolean());
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

			IOption<?> option = options.get(msg.key);
			if(option == null) return;
			option.setValueString(msg.value);

			if(msg.notify) ItemWand.optionMessage(player, option);
			player.inventory.markDirty();
		}
	}
}
