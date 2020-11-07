package thetadev.constructionwand.containers;

import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.containers.handlers.HandlerShulkerbox;

public class ContainerRegistrar
{
	public static void register() {
		ConstructionWand.instance.containerManager.register(new HandlerShulkerbox());
	}
}
