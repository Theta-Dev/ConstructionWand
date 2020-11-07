package thetadev.constructionwand.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.basics.*;
import thetadev.constructionwand.basics.option.WandOptions;
import thetadev.constructionwand.items.ItemWand;
import thetadev.constructionwand.network.PacketQueryUndo;
import thetadev.constructionwand.network.PacketWandOption;

@Environment(EnvType.CLIENT)
public class ClientEvents
{
	private static boolean ctrlPressed = false;

	public static void registerEvents() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			PlayerEntity player = MinecraftClient.getInstance().player;
			if(player == null) return;

			ItemStack wand = WandUtil.holdingWand(player);
			if(wand == null) return;

			boolean ctrlState = Screen.hasControlDown();
			if(ctrlPressed != ctrlState) {
				ctrlPressed = ctrlState;
				PacketQueryUndo packet = new PacketQueryUndo(ctrlPressed);
				ClientSidePacketRegistry.INSTANCE.sendToServer(PacketQueryUndo.ID, packet.encode());
				//ConstructionWand.LOGGER.debug("CTRL key update: "+ctrlPressed);
			}

			/*
			if(player.isSneaking() && (ctrlPressed || !ConstructionWand.instance.config.SHIFTCTRL_GUI)) {
				MinecraftClient.getInstance().openScreen(new ScreenWand(wand));
			}

			if(player.isSneaking() && (ctrlPressed || !ConstructionWand.instance.config.SHIFTCTRL_MODE)) {
				WandOptions wandOptions = new WandOptions(wand);
				wandOptions.mode.next();
				ClientSidePacketRegistry.INSTANCE.sendToServer(PacketWandOption.ID, new PacketWandOption(wandOptions.mode, true).encode());
			}*/
		});
	}

	/*
	// SHIFT+(CTRL)+Scroll to change direction lock
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void MouseScrollEvent(InputEvent.MouseScrollEvent event) {
		PlayerEntity player = Minecraft.getInstance().player;
		double scroll = event.getScrollDelta();

		if(player == null || !player.isSneaking() || (!Screen.hasControlDown() && ConfigClient.SHIFTCTRL_MODE.get()) || scroll == 0) return;

		ItemStack wand = WandUtil.holdingWand(player);
		if(wand == null) return;

		WandOptions wandOptions = new WandOptions(wand);
		wandOptions.lock.next(scroll<0);
		ConstructionWand.instance.HANDLER.sendToServer(new PacketWandOption(wandOptions.lock, true));
		event.setCanceled(true);
	}*/
}
