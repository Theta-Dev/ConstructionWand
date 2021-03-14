package thetadev.constructionwand.client;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
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
import thetadev.constructionwand.items.wand.ItemWand;
import thetadev.constructionwand.wand.WandJob;

import java.util.Set;

public class RenderBlockPreview
{
    public WandJob wandJob;
    public Set<BlockPos> undoBlocks;

    @SubscribeEvent
    public void renderBlockHighlight(DrawBlockHighlightEvent event) {
        if(event.getTarget().getType() != RayTraceResult.Type.BLOCK) return;

        BlockRayTraceResult rtr = (BlockRayTraceResult) event.getTarget();
        Entity entity = event.getInfo().getRenderViewEntity();
        if(!(entity instanceof PlayerEntity)) return;
        PlayerEntity player = (PlayerEntity) entity;
        Set<BlockPos> blocks = null;
        float colorR = 0, colorG = 0, colorB = 0;

        ItemStack wand = WandUtil.holdingWand(player);
        if(wand == null) return;

        if(!(player.isSneaking() && ClientEvents.isOptKeyDown())) {
            if(wandJob == null || !compareRTR(wandJob.rayTraceResult, rtr) || !(wandJob.wand.equals(wand))) {
                wandJob = ItemWand.getWandJob(player, player.getEntityWorld(), rtr, wand);
            }
            blocks = wandJob.getBlockPositions();
        }
        else {
            blocks = undoBlocks;
            colorG = 1;
        }

        if(blocks == null || blocks.isEmpty()) return;

		renderBlockList(blocks, colorR, colorG, colorB);

		event.setCanceled(true);
	}

	private void renderBlockList(Set<BlockPos> blocks, float red, float green, float blue) {
		for(BlockPos block : blocks) {
			double partialTicks = event.getPartialTicks();
			double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
			double d1 = player.lastTickPosY + player.getEyeHeight() + (player.posY - player.lastTickPosY) * partialTicks;
			double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

			AxisAlignedBB aabb = new AxisAlignedBB(block).offset(-d0, -d1, -d2);
			drawBoundingBox(aabb, red, green, blue, 0.4F);
		}
	}

	private static void drawBoundingBox(AxisAlignedBB box, float red, float green, float blue, float alpha) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(3, DefaultVertexFormats.POSITION_COLOR);

		//Base
		buffer.pos(box.minX, box.minY, box.minZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(box.maxX, box.minY, box.minZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(box.maxX, box.minY, box.maxZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(box.minX, box.minY, box.maxZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(box.minX, box.minY, box.minZ).color(red, green, blue, alpha).endVertex();
		//Side1
		buffer.pos(box.minX, box.maxY, box.minZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(box.minX, box.maxY, box.maxZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(box.minX, box.minY, box.maxZ).color(red, green, blue, alpha).endVertex();
		//Side2
		buffer.pos(box.minX, box.maxY, box.maxZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(box.maxX, box.maxY, box.maxZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(box.maxX, box.minY, box.maxZ).color(red, green, blue, alpha).endVertex();
		//Side3
		buffer.pos(box.maxX, box.maxY, box.maxZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(box.maxX, box.maxY, box.minZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(box.maxX, box.minY, box.minZ).color(red, green, blue, alpha).endVertex();
		//Side4
		buffer.pos(box.maxX, box.maxY, box.minZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(box.minX, box.maxY, box.minZ).color(red, green, blue, alpha).endVertex();

		tessellator.draw();
	}
}
