package thetadev.constructionwand.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.basics.*;

import java.util.Arrays;
import java.util.function.Supplier;

public class PacketWandOption
{
	public boolean[] options;

	public PacketWandOption(boolean[] options) {
		this.options = options;
	}

	public PacketWandOption(IEnumOption option) {
		options = new boolean[WandOptions.options.length];

		for(int i=0; i<options.length; i++) {
			options[i] = (WandOptions.options[i] == option);
		}
	}

	public static void encode(PacketWandOption msg, PacketBuffer buffer) {
		for(int i=0; i<WandOptions.options.length; i++) buffer.writeBoolean(msg.options[i]);
	}

	public static PacketWandOption decode(PacketBuffer buffer) {
		boolean[] options = new boolean[WandOptions.options.length];
		for(int i=0; i<WandOptions.options.length; i++) options[i] = buffer.readBoolean();

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

			String langPrefix = ConstructionWand.MODID + ".chat.";

			for(int i=0; i<WandOptions.options.length; i++) {
				if(!msg.options[i]) continue;

				IEnumOption opt = WandOptions.options[i];
				options.nextOption(opt);
				player.sendStatusMessage(new TranslationTextComponent(langPrefix+options.getOption(opt).getOptionKey())
						.appendSibling(new TranslationTextComponent(langPrefix + options.getOption(opt).getTranslationKey())), true);
			}

			player.inventory.markDirty();

			ConstructionWand.LOGGER.info("Keys: "+ Arrays.toString(msg.options));
		}
	}
}
