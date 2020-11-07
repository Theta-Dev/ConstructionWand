package thetadev.constructionwand.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thetadev.constructionwand.client.ClientEvents;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin
{
    @Shadow private GameMode gameMode;

    @Inject(at = @At("HEAD"), method = "interactItem", cancellable = true)
    private void interactItem(PlayerEntity player, World world, Hand hand, CallbackInfoReturnable<ActionResult> info) {
        if(gameMode == GameMode.SPECTATOR) return;

        if(ClientEvents.onRightClickItem(player, player.getStackInHand(hand))) info.setReturnValue(ActionResult.SUCCESS);
    }
}
