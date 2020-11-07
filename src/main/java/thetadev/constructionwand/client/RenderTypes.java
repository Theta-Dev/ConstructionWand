package thetadev.constructionwand.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormats;
import org.lwjgl.opengl.GL11;
import thetadev.constructionwand.ConstructionWand;

import java.util.OptionalDouble;

@Environment(EnvType.CLIENT)
public class RenderTypes
{
	public static final RenderLayer TRANSLUCENT_LINES;

	protected static final RenderPhase.Transparency TRANSLUCENT_TRANSPARENCY = new RenderPhase.Transparency("translucent_transparency", () -> {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
	}, RenderSystem::disableBlend);
	protected static final RenderPhase.DepthTest DEPTH_ALWAYS = new RenderPhase.DepthTest("always", GL11.GL_ALWAYS);

	static {
		RenderLayer.MultiPhaseParameters translucentNoDepthState = RenderLayer.MultiPhaseParameters.builder()
				.transparency(TRANSLUCENT_TRANSPARENCY)
				.lineWidth(new RenderPhase.LineWidth(OptionalDouble.of(2)))
				.texture(new RenderPhase.Texture())
				.depthTest(DEPTH_ALWAYS)
				.build(false);

		TRANSLUCENT_LINES = RenderLayer.of(
				ConstructionWand.MODID+":translucent_lines",
				VertexFormats.POSITION_COLOR,
				GL11.GL_LINES,
				256,
				translucentNoDepthState
		);
	}
}
