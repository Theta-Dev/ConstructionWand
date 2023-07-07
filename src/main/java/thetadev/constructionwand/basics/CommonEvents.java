package thetadev.constructionwand.basics;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import thetadev.constructionwand.ConstructionWand;

@Mod.EventBusSubscriber(modid = ConstructionWand.MODID)
public class CommonEvents
{
    @SubscribeEvent
    public static void serverStarting(ServerStartingEvent e) {
        ReplacementRegistry.init();
    }

    @SubscribeEvent
    public static void logOut(PlayerEvent.PlayerLoggedOutEvent e) {
        Player player = e.getEntity();
        if(player.level().isClientSide) return;
        ConstructionWand.instance.undoHistory.removePlayer(player);
    }
}
