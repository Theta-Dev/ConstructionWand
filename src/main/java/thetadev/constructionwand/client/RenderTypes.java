package thetadev.constructionwand.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;
import thetadev.constructionwand.ConstructionWand;

import java.util.OptionalDouble;

public class RenderTypes
{
    public static final RenderType TRANSLUCENT_LINES;

    protected static final RenderState.TransparencyState TRANSLUCENT_TRANSPARENCY = new RenderState.TransparencyState("translucent_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }, RenderSystem::disableBlend);
    protected static final RenderState.DepthTestState DEPTH_ALWAYS = new RenderState.DepthTestState("always", GL11.GL_ALWAYS);

    static {
        RenderType.State translucentNoDepthState = RenderType.State.getBuilder().transparency(TRANSLUCENT_TRANSPARENCY)
                .line(new RenderState.LineState(OptionalDouble.of(2)))
                .texture(new RenderState.TextureState())
                .depthTest(DEPTH_ALWAYS)
                .build(false);

        TRANSLUCENT_LINES = RenderType.makeType(
                ConstructionWand.MODID+":translucent_lines",
                DefaultVertexFormats.POSITION_COLOR,
                GL11.GL_LINES,
                256,
                translucentNoDepthState
        );
    }
}
