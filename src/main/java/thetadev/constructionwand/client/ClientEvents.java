package thetadev.constructionwand.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.basics.*;
import thetadev.constructionwand.basics.options.*;
import thetadev.constructionwand.items.ItemWand;
import thetadev.constructionwand.network.PacketQueryUndo;
import thetadev.constructionwand.network.PacketWandOption;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ClientEvents
{
	private static boolean ctrlPressed = false;

	@SubscribeEvent
	public static void KeyEvent(InputEvent.KeyInputEvent event) {
		PlayerEntity player = Minecraft.getInstance().player;
		if(player == null) return;
		if(WandUtil.holdingWand(player) == null) return;

		boolean ctrlState = Screen.hasControlDown();
		if(ctrlPressed != ctrlState) {
			ctrlPressed = ctrlState;
			PacketQueryUndo packet = new PacketQueryUndo(ctrlPressed);
			ConstructionWand.instance.HANDLER.sendToServer(packet);
			//ConstructionWand.LOGGER.debug("CTRL key update: "+ctrlPressed);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void MouseScrollEvent(InputEvent.MouseScrollEvent event) {
		PlayerEntity player = Minecraft.getInstance().player;
		double scroll = event.getScrollDelta();

		if(player == null || !player.isSneaking() || (!Screen.hasControlDown() && ConfigClient.SHIFTCTRL.get()) || scroll == 0) return;

		ItemStack wand = WandUtil.holdingWand(player);
		if(wand == null) return;

		WandOptions wandOptions = new WandOptions(wand);
		ConstructionWand.instance.HANDLER.sendToServer(new PacketWandOption(wandOptions.nextOption(EnumLock.NOLOCK, scroll<0), true));
		event.setCanceled(true);
	}

	@SubscribeEvent
	public static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
		PlayerEntity player = event.getPlayer();

		if(player == null || !player.isSneaking() || (!Screen.hasControlDown() && ConfigClient.SHIFTCTRL.get())) return;

		ItemStack wand = event.getItemStack();
		if(!(wand.getItem() instanceof ItemWand)) return;

		WandOptions wandOptions = new WandOptions(wand);
		ConstructionWand.instance.HANDLER.sendToServer(new PacketWandOption(wandOptions.nextOption(EnumMode.DEFAULT), true));
	}

	@SubscribeEvent
	public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
		PlayerEntity player = event.getPlayer();
		if(player == null || !player.isSneaking() || (!Screen.hasControlDown() && ConfigClient.SHIFTCTRL.get())) return;

		ItemStack wand = event.getItemStack();
		if(!(wand.getItem() instanceof ItemWand)) return;

		Minecraft.getInstance().displayGuiScreen(new ScreenWand(wand));
		event.setCanceled(true);
	}
}
