package thetadev.constructionwand.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import thetadev.constructionwand.ConstructionWandClient;
import thetadev.constructionwand.client.RenderTypes;

@Environment(EnvType.CLIENT)
@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin
{
    @Shadow @Final private MinecraftClient client;
    @Shadow private ClientWorld world;

    @Shadow
    protected static void drawShapeOutline(MatrixStack matrixStack, VertexConsumer vertexConsumer, VoxelShape voxelShape, double d, double e, double f, float g, float h, float i, float j) {
    }

    @Inject(
            method = "render",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;pushMatrix()V", ordinal = 0),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void renderWandHighlight(
            MatrixStack matrices,
            float tickDelta,
            long limitTime,
            boolean renderBlockOutline,
            Camera camera,
            GameRenderer gameRenderer,
            LightmapTextureManager lightmapTextureManager,
            Matrix4f matrix4f,
            CallbackInfo ci,
            Profiler profiler,
            Vec3d vec3d,
            double d,
            double e,
            double f,
            Matrix4f matrix4f2,
            boolean bl,
            Frustum frustum2,
            boolean bl3,
            VertexConsumerProvider.Immediate immediate
    ) {
        profiler.swap("wand_outline");

        HitResult hitResult = this.client.crosshairTarget;
        if (renderBlockOutline && hitResult != null && hitResult.getType() == HitResult.Type.BLOCK && ConstructionWandClient.instance != null) {
            ConstructionWandClient.instance.renderBlockPreview.renderBlockHighlight(
                    client.player, world, (BlockHitResult) hitResult, matrices, immediate.getBuffer(RenderTypes.TRANSLUCENT_LINES), d, e, f
            );
        }
    }
}