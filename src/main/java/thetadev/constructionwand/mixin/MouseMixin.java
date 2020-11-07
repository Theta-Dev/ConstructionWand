package thetadev.constructionwand.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import thetadev.constructionwand.client.ClientEvents;

@Environment(EnvType.CLIENT)
@Mixin(Mouse.class)
public abstract class MouseMixin
{
    @Inject(method = "onMouseScroll", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSpectator()Z", shift = At.Shift.BEFORE), cancellable = true)
    private void onMouseScroll(long window, double d, double e, CallbackInfo info, double scrollDelta, float i) {
        if(ClientEvents.MouseScrollEvent(scrollDelta)) {
            info.cancel();
        }
    }
}