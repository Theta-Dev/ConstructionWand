package thetadev.constructionwand.containers;

import net.minecraftforge.fml.ModList;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.containers.handlers.*;

public class ContainerRegistrar
{
	public static void register() {
		ConstructionWand.instance.containerManager.register(new HandlerCapability());
		ConstructionWand.instance.containerManager.register(new HandlerShulkerbox());

		if(ModList.get().isLoaded("botania")) {
			ConstructionWand.instance.containerManager.register(new HandlerBotania());
			ConstructionWand.LOGGER.info("Botania integration added");
		}
	}
}
