package thetadev.constructionwand.client;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import thetadev.constructionwand.basics.WandUtil;
import thetadev.constructionwand.job.WandJob;

import java.util.LinkedList;

public class RenderBlockPreview
{
	public WandJob wandJob;
	public LinkedList<BlockPos> undoBlocks;

	@SubscribeEvent
	public void renderAdditionalBlockBounds(DrawBlockHighlightEvent event)
	{
		if(event.getTarget().getType() != RayTraceResult.Type.BLOCK) return;

		BlockRayTraceResult rtr = (BlockRayTraceResult) event.getTarget();
		Entity entity = event.getInfo().getRenderViewEntity();
		if(!(entity instanceof PlayerEntity)) return;
		PlayerEntity player = (PlayerEntity) entity;
		LinkedList<BlockPos> blocks;
		float colorR=0, colorG=0, colorB=0;

		ItemStack wand = WandUtil.holdingWand(player);
		if(wand == null) return;

		if(player.isSneaking()) {
			blocks = undoBlocks;
			colorG=1;
		}
		else {
			if(wandJob == null || !(wandJob.getRayTraceResult().equals(rtr)) || !(wandJob.getWand().equals(wand))) {
				wandJob = WandJob.getJob(player, player.getEntityWorld(), rtr, wand);
			}
			blocks = wandJob.getBlockPositions();
		}

		if(blocks == null || blocks.isEmpty()) return;

		for(BlockPos block : blocks) {

			double partialTicks = event.getPartialTicks();
			double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
			double d1 = player.lastTickPosY + player.getEyeHeight() + (player.posY - player.lastTickPosY) * partialTicks;
			double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

			AxisAlignedBB aabb = new AxisAlignedBB(block).offset(-d0, -d1, -d2);
			WorldRenderer.drawSelectionBoundingBox(aabb, colorR, colorG, colorB, 0.4F);
		}

		event.setCanceled(true);
	}
}
