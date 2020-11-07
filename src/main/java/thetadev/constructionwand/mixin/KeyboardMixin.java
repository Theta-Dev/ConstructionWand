package thetadev.constructionwand.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thetadev.constructionwand.client.ClientEvents;

@Environment(EnvType.CLIENT)
@Mixin(Keyboard.class)
public abstract class KeyboardMixin {
    @Shadow
    MinecraftClient client;

    @Inject(method = "onKey", at = @At("RETURN"))
    private void fireKeyInput(long window, int key, int scancode, int action, int modifiers, CallbackInfo info) {
        if (window == this.client.getWindow().getHandle()) {
            ClientEvents.KeyEvent();
        }
    }
}
