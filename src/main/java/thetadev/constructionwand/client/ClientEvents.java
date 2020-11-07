package thetadev.constructionwand.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.basics.WandUtil;
import thetadev.constructionwand.basics.option.WandOptions;
import thetadev.constructionwand.items.ItemWand;
import thetadev.constructionwand.network.PacketQueryUndo;
import thetadev.constructionwand.network.PacketWandOption;

@Environment(EnvType.CLIENT)
public class ClientEvents
{
	private static boolean ctrlPressed = false;

	public static void KeyEvent() {
		PlayerEntity player = MinecraftClient.getInstance().player;
		if(player == null) return;
		if(WandUtil.holdingWand(player) == null) return;

		boolean ctrlState = Screen.hasControlDown();
		if(ctrlPressed != ctrlState) {
			ctrlPressed = ctrlState;
			PacketQueryUndo packet = new PacketQueryUndo(ctrlPressed);
			ClientSidePacketRegistry.INSTANCE.sendToServer(PacketQueryUndo.ID, packet.encode());
			//ConstructionWand.LOGGER.info("CTRL key update: "+ctrlPressed);
		}
	}

	// SHIFT+(CTRL)+Scroll to change direction lock
	public static boolean MouseScrollEvent(double scroll) {
		PlayerEntity player = MinecraftClient.getInstance().player;

		if(player == null || !player.isSneaking() || (!Screen.hasControlDown() && ConstructionWand.instance.config.SHIFTCTRL_MODE) || scroll == 0) return false;

		ItemStack wand = WandUtil.holdingWand(player);
		if(wand == null) return false;

		WandOptions wandOptions = new WandOptions(wand);
		wandOptions.lock.next(scroll<0);
		ClientSidePacketRegistry.INSTANCE.sendToServer(PacketWandOption.ID, new PacketWandOption(wandOptions.lock, true).encode());
		return true;
	}

	// SHIFT+(CTRL)+Left click wand to change mode
	public static void onLeftClickEmpty(PlayerEntity player) {
		if(player == null || !player.isSneaking() || (!Screen.hasControlDown() && ConstructionWand.instance.config.SHIFTCTRL_MODE)) return;

		ItemStack wand = player.getMainHandStack();
		if(wand == null || !(wand.getItem() instanceof ItemWand)) return;

		WandOptions wandOptions = new WandOptions(wand);
		wandOptions.mode.next();
		ClientSidePacketRegistry.INSTANCE.sendToServer(PacketWandOption.ID, new PacketWandOption(wandOptions.mode, true).encode());
	}

	// SHIFT+Right click wand to open GUI
	public static boolean onRightClickItem(PlayerEntity player, ItemStack wand) {
		if(player == null || !player.isSneaking() || (!Screen.hasControlDown() && ConstructionWand.instance.config.SHIFTCTRL_GUI)) return false;

		if(wand == null || !(wand.getItem() instanceof ItemWand)) return false;

		MinecraftClient.getInstance().openScreen(new ScreenWand(wand));
		return true;
	}
}
