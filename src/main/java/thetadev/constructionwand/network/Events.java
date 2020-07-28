package thetadev.constructionwand.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import thetadev.constructionwand.ConstructionWand;

@Mod.EventBusSubscriber(modid = ConstructionWand.MODID)
public class Events
{
	@SubscribeEvent
	public static void join(EntityJoinWorldEvent e) {
		if(e.getWorld().isRemote || !(e.getEntity() instanceof PlayerEntity)) return;

		PlayerEntity player = (PlayerEntity) e.getEntity();
		ConstructionWand.instance.jobHistory.updateClient(player);
	}
}
