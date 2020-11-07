package thetadev.constructionwand.mixin;

import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thetadev.constructionwand.ConstructionWand;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin
{
    @Inject(at = @At("HEAD"), method = "remove")
    private void remove(ServerPlayerEntity player, CallbackInfo info) {
        if(player.getEntityWorld().isClient) return;
        ConstructionWand.instance.undoHistory.removePlayer(player);
    }
}
