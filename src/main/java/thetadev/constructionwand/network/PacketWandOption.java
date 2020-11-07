package thetadev.constructionwand.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.basics.WandUtil;
import thetadev.constructionwand.basics.option.IOption;
import thetadev.constructionwand.basics.option.WandOptions;
import thetadev.constructionwand.items.ItemWand;

public class PacketWandOption
{
	public static final Identifier ID = ConstructionWand.loc("wand_option");

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

	public PacketByteBuf encode() {
		PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
		buffer.writeString(key);
		buffer.writeString(value);
		buffer.writeBoolean(notify);
		return buffer;
	}

	public static PacketWandOption decode(PacketByteBuf buffer) {
		return new PacketWandOption(buffer.readString(100), buffer.readString(100), buffer.readBoolean());
	}

	public static void handle(PacketContext ctx, PacketByteBuf buffer) {
		if(ctx.getPacketEnvironment() != EnvType.SERVER) return;

		PlayerEntity player = ctx.getPlayer();
		if(player == null) return;

		PacketWandOption msg = decode(buffer);

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
