package thetadev.constructionwand.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
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
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.basics.WandUtil;
import thetadev.constructionwand.job.WandJob;

import java.util.LinkedList;

public class RenderBlockPreview
{
	@SubscribeEvent
	public void renderAdditionalBlockBounds(DrawHighlightEvent event)
	{
		if(event.getTarget().getType() != RayTraceResult.Type.BLOCK) return;

		BlockRayTraceResult rtr = (BlockRayTraceResult)event.getTarget();
		Entity entity = event.getInfo().getRenderViewEntity();
		if(!(entity instanceof PlayerEntity)) return;
		PlayerEntity player = (PlayerEntity) entity;
		LinkedList<BlockPos> blocks;
		float colorR=0, colorG=0, colorB=0;

		ItemStack wand = WandUtil.holdingWand(player);
		if(wand == null) return;

		if(player.isSneaking()) {
			blocks = ConstructionWand.instance.renderCache.undoBlocks;
			if(blocks == null || !blocks.contains(rtr.getPos())) return;
			colorG=1;
		}
		else {
			WandJob cachedJob = ConstructionWand.instance.renderCache.wandJob;

			if(cachedJob == null || !(cachedJob.getRayTraceResult().equals(rtr)) || !(cachedJob.getWand().equals(wand))) {
				ConstructionWand.instance.renderCache.wandJob  = new WandJob(player, player.getEntityWorld(), rtr, wand);
			}
			blocks = ConstructionWand.instance.renderCache.wandJob.getBlockPositions();
		}

		if(blocks == null || blocks.isEmpty()) return;

		MatrixStack ms = event.getMatrix();
		IRenderTypeBuffer buffer = event.getBuffers();
		ms.push();

		for(BlockPos block : blocks) {

			double partialTicks = event.getPartialTicks();
			double d0 = player.lastTickPosX + (player.getPosX() - player.lastTickPosX) * partialTicks;
			double d1 = player.lastTickPosY + player.getEyeHeight() + (player.getPosY() - player.lastTickPosY) * partialTicks;
			double d2 = player.lastTickPosZ + (player.getPosZ() - player.lastTickPosZ) * partialTicks;

			AxisAlignedBB aabb = new AxisAlignedBB(block).offset(-d0, -d1, -d2);
			IVertexBuilder lineBuilder = buffer.getBuffer(RenderTypes.TRANSLUCENT_LINES);
			WorldRenderer.drawBoundingBox(ms, lineBuilder, aabb, colorR, colorG, colorB, 0.4F);
		}
		ms.pop();

		event.setCanceled(true);
	}
}
