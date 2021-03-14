package thetadev.constructionwand.basics;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import thetadev.constructionwand.ConstructionWand;

@Mod.EventBusSubscriber(modid = ConstructionWand.MODID)
public class CommonEvents
{
    @SubscribeEvent
    public static void logOut(PlayerEvent.PlayerLoggedOutEvent e) {
        PlayerEntity player = e.getPlayer();
        if(player.getEntityWorld().isRemote) return;
        ConstructionWand.instance.undoHistory.removePlayer(player);
    }
}
