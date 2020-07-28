package thetadev.constructionwand.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.network.PacketWandOption;


public class KeyEvents
{
	private final String langPrefix = ConstructionWand.MODID + ".key.";
	private final String langCategory = langPrefix + "category";

	public KeyBinding keyMode = new KeyBinding(langPrefix+"mode", KeyConflictContext.IN_GAME, InputMappings.getInputByCode(GLFW.GLFW_KEY_N, 0), langCategory);
	public KeyBinding keyOption = new KeyBinding(langPrefix+"option", KeyConflictContext.IN_GAME, KeyModifier.ALT, InputMappings.getInputByCode(GLFW.GLFW_KEY_N, 0), langCategory);

	private boolean modePressed;
	private boolean optionPressed;

	public KeyEvents() {
		ClientRegistry.registerKeyBinding(keyMode);
		ClientRegistry.registerKeyBinding(keyOption);

		modePressed = false;
		optionPressed = false;
	}

	@SubscribeEvent
	public void KeyEvent(InputEvent e) {
		boolean currentModePressed = keyMode.isPressed();
		boolean currentOptionPressed = keyOption.isPressed();

		if(currentModePressed != modePressed || currentModePressed != optionPressed) {
			modePressed = currentModePressed;
			optionPressed = currentOptionPressed;

			if(modePressed || optionPressed) {
				PacketWandOption packet = new PacketWandOption(modePressed, optionPressed);
				ConstructionWand.instance.HANDLER.sendToServer(packet);
			}
		}
	}
}
