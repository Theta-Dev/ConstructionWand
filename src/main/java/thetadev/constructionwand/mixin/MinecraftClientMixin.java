package thetadev.constructionwand.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thetadev.constructionwand.client.ClientEvents;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin
{
    @Shadow public HitResult crosshairTarget;
    @Shadow protected int attackCooldown;
    @Shadow public ClientPlayerEntity player;

    @Inject(at = @At("HEAD"), method = "doAttack")
    private void doAttack(CallbackInfo info) {
        // On left clicked air
        if(attackCooldown <= 0 && crosshairTarget != null && crosshairTarget.getType() == HitResult.Type.MISS && !player.isRiding()) {
            ClientEvents.onLeftClickEmpty(player);
        }
    }
}
