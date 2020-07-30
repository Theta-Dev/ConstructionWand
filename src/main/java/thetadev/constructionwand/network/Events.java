package thetadev.constructionwand.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.basics.WandUtil;

@Mod.EventBusSubscriber(modid = ConstructionWand.MODID)
public class Events
{
	// Send undo blocks to player on join
	/*
	@SubscribeEvent
	public static void join(EntityJoinWorldEvent e) {
		if(e.getWorld().isRemote || !(e.getEntity() instanceof PlayerEntity)) return;

		PlayerEntity player = (PlayerEntity) e.getEntity();
		ConstructionWand.instance.jobHistory.updateClient(player);
	}*/
	// Send undo blocks to player sneaking with wand
	@SubscribeEvent
	public static void sneak(InputUpdateEvent e) {
		if(e.getMovementInput().sneaking) {
			PlayerEntity player = e.getPlayer();
			if(WandUtil.holdingWand(player) == null) return;

			PacketQueryUndo packet = new PacketQueryUndo();
			ConstructionWand.instance.HANDLER.sendToServer(packet);
		}
	}

	// Prevent block interactions while holding wand
	@SubscribeEvent
	public static void rightClickBlock(PlayerInteractEvent.RightClickBlock e) {
		if(e.getWorld().isRemote) return;
		PlayerEntity player = e.getPlayer();
		if(WandUtil.holdingWand(player) == null) return;

		e.setUseBlock(Event.Result.DENY);
	}
}
