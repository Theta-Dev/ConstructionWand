package thetadev.constructionwand.containers;

import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.containers.handlers.HandlerCapability;
import thetadev.constructionwand.containers.handlers.HandlerShulkerbox;

public class ContainerRegistrar
{
    public static void register() {
        ConstructionWand.instance.containerManager.register(new HandlerCapability());
        ConstructionWand.instance.containerManager.register(new HandlerShulkerbox());

        /*
        TODO: Reenable this when Botania gets ported to 1.17

        if(ModList.get().isLoaded("botania")) {
            ConstructionWand.instance.containerManager.register(new HandlerBotania());
            ConstructionWand.LOGGER.info("Botania integration added");
        }
        */
    }
}
