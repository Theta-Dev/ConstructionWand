package thetadev.constructionwand.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.basics.*;
import thetadev.constructionwand.basics.options.EnumDirection;
import thetadev.constructionwand.basics.options.EnumReplace;
import thetadev.constructionwand.basics.options.EnumLock;
import thetadev.constructionwand.basics.options.IEnumOption;
import thetadev.constructionwand.network.PacketQueryUndo;
import thetadev.constructionwand.network.PacketWandOption;


public class KeyEvents
{
	private final String langPrefix = ConstructionWand.MODID + ".key.";
	private final String langCategory = langPrefix + "category";

	public final KeyBinding WAND_KEY = new KeyBinding(langPrefix+"wand", KeyConflictContext.IN_GAME, InputMappings.getInputByCode(GLFW.GLFW_KEY_N, 0), langCategory);

	public static final KeyModifier[] keyModifiers = {
			KeyModifier.NONE,
			KeyModifier.SHIFT
	};

	public static final IEnumOption[] keyOptions = {
			EnumDirection.TARGET,
			EnumReplace.YES
	};

	public KeyEvents() {
		ClientRegistry.registerKeyBinding(WAND_KEY);
	}

	@SubscribeEvent
	public void KeyEvent(InputEvent.KeyInputEvent e) {
		if(WAND_KEY.isPressed()) {
			for(int i=0; i<keyOptions.length; i++) {
				if(keyModifiers[i].isActive(null)) {
					PacketWandOption packet = new PacketWandOption(keyOptions[i], true);
					ConstructionWand.instance.HANDLER.sendToServer(packet);
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void MouseScrollEvent(InputEvent.MouseScrollEvent e) {
		Minecraft minecraft = Minecraft.getInstance();
		PlayerEntity player = minecraft.player;
		double scroll = e.getScrollDelta();

		if(player == null || !player.isSneaking() || scroll == 0 || WandUtil.holdingWand(player) == null) return;

		PacketWandOption packet = new PacketWandOption(EnumLock.NOLOCK, scroll<0);

		ConstructionWand.instance.HANDLER.sendToServer(packet);
		e.setCanceled(true);
	}

	// Send undo blocks to player sneaking with wand
	@SubscribeEvent
	public void sneak(InputUpdateEvent e) {
		if(e.getMovementInput().sneak) {
			PlayerEntity player = e.getPlayer();
			if(WandUtil.holdingWand(player) == null) return;

			PacketQueryUndo packet = new PacketQueryUndo();
			ConstructionWand.instance.HANDLER.sendToServer(packet);
		}
	}
}
