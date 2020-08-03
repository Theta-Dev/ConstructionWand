package thetadev.constructionwand.basics;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.network.PacketQueryUndo;

@Mod.EventBusSubscriber(modid = ConstructionWand.MODID)
public class CommonEvents
{
	@SubscribeEvent
	public static void logOut(PlayerEvent.PlayerLoggedOutEvent e) {
		PlayerEntity player = e.getPlayer();
		if(player.getEntityWorld().isRemote) return;
		ConstructionWand.instance.jobHistory.removePlayer(player);
	}
}
