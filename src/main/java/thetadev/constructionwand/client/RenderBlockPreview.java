package thetadev.constructionwand.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import thetadev.constructionwand.basics.WandUtil;
import thetadev.constructionwand.job.WandJob;

import java.util.Set;

public class RenderBlockPreview
{
	public WandJob wandJob;
	public Set<BlockPos> undoBlocks;

	@SubscribeEvent
	public void renderBlockHighlight(DrawHighlightEvent event)
	{
		if(event.getTarget().getType() != RayTraceResult.Type.BLOCK) return;

		BlockRayTraceResult rtr = (BlockRayTraceResult) event.getTarget();
		Entity entity = event.getInfo().getRenderViewEntity();
		if(!(entity instanceof PlayerEntity)) return;
		PlayerEntity player = (PlayerEntity) entity;
		Set<BlockPos> blocks;
		float colorR=0, colorG=0, colorB=0;

		ItemStack wand = WandUtil.holdingWand(player);
		if(wand == null) return;

		if(!(player.isSneaking() && Screen.hasControlDown())) {
			if(wandJob == null || !(wandJob.getRayTraceResult().equals(rtr)) || !(wandJob.getWand().equals(wand))) {
				wandJob = WandJob.getJob(player, player.getEntityWorld(), rtr, wand);
			}

			blocks = wandJob.getBlockPositions();
		}
		else {
			blocks = undoBlocks;
			colorG = 1;
		}

		if(blocks == null || blocks.isEmpty()) return;

		renderBlockList(blocks, event.getMatrix(), event.getBuffers(), colorR, colorG, colorB);

		event.setCanceled(true);
	}

	private void renderBlockList(Set<BlockPos> blocks, MatrixStack ms, IRenderTypeBuffer buffer, float red, float green, float blue) {
		double renderPosX = Minecraft.getInstance().getRenderManager().info.getProjectedView().getX();
		double renderPosY = Minecraft.getInstance().getRenderManager().info.getProjectedView().getY();
		double renderPosZ = Minecraft.getInstance().getRenderManager().info.getProjectedView().getZ();

		ms.push();
		ms.translate(-renderPosX, -renderPosY, -renderPosZ);

		for(BlockPos block : blocks) {
			AxisAlignedBB aabb = new AxisAlignedBB(block);
			IVertexBuilder lineBuilder = buffer.getBuffer(RenderTypes.TRANSLUCENT_LINES);
			WorldRenderer.drawBoundingBox(ms, lineBuilder, aabb, red, green, blue, 0.4F);
		}
		ms.pop();
	}
}
