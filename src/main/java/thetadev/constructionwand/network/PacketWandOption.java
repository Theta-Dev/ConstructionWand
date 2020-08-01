package thetadev.constructionwand.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.basics.*;
import thetadev.constructionwand.basics.options.IEnumOption;
import thetadev.constructionwand.basics.options.WandOptions;
import thetadev.constructionwand.items.ItemWand;

import java.util.Arrays;
import java.util.function.Supplier;

public class PacketWandOption
{
	public byte[] options;

	public PacketWandOption(byte[] options) {
		this.options = options;
	}

	public PacketWandOption(IEnumOption option, boolean dir) {
		options = new byte[WandOptions.options.length];

		for(int i=0; i<options.length; i++) {
			if(WandOptions.options[i] == option) options[i] = (byte)(dir ? 2:1);
			else options[i] = (byte)0;
		}
	}

	public static void encode(PacketWandOption msg, PacketBuffer buffer) {
		buffer.writeByteArray(msg.options);
	}

	public static PacketWandOption decode(PacketBuffer buffer) {
		byte[] options = buffer.readByteArray(WandOptions.options.length);
		return new PacketWandOption(options);
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

			String langPrefix = ConstructionWand.MODID + ".option.";

			for(int i=0; i<WandOptions.options.length; i++) {
				if(msg.options[i] == 0) continue;

				IEnumOption opt = options.nextOption(WandOptions.options[i], msg.options[i]>1);
				ItemWand.optionMessage(player, opt);
			}

			player.inventory.markDirty();

			//ConstructionWand.LOGGER.debug("Keys: "+ Arrays.toString(msg.options));
		}
	}
}
