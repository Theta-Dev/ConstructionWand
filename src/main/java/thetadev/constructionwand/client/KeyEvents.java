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
import thetadev.constructionwand.basics.*;
import thetadev.constructionwand.network.PacketWandOption;

import java.util.Arrays;


public class KeyEvents
{
	private final String langPrefix = ConstructionWand.MODID + ".key.";
	private final String langCategory = langPrefix + "category";

	public final KeyBinding[] keys = {
			//new KeyBinding(langPrefix+"mode", KeyConflictContext.IN_GAME, InputMappings.getInputByCode(GLFW.GLFW_KEY_N, 0), langCategory),
			new KeyBinding(langPrefix+"lock", KeyConflictContext.IN_GAME, InputMappings.getInputByCode(GLFW.GLFW_KEY_N, 0), langCategory),
			new KeyBinding(langPrefix+"direction", KeyConflictContext.IN_GAME, KeyModifier.SHIFT, InputMappings.getInputByCode(GLFW.GLFW_KEY_N, 0), langCategory),
			new KeyBinding(langPrefix+"fluid", KeyConflictContext.IN_GAME, KeyModifier.CONTROL, InputMappings.getInputByCode(GLFW.GLFW_KEY_N, 0), langCategory)
	};

	public static final IEnumOption[] keyOptions = {
			//EnumMode.DEFAULT,
			EnumLock.NOLOCK,
			EnumDirection.TARGET,
			EnumFluidLock.IGNORE
	};

	public KeyEvents() {
		for(KeyBinding key : keys) ClientRegistry.registerKeyBinding(key);
	}

	@SubscribeEvent
	public void KeyEvent(InputEvent e) {
		boolean sendPacket = false;

		for(int i=0; i<keyOptions.length; i++) {
			if(keys[i].isPressed()) {
				PacketWandOption packet = new PacketWandOption(keyOptions[i]);
				ConstructionWand.instance.HANDLER.sendToServer(packet);
			}
		}
	}
}
