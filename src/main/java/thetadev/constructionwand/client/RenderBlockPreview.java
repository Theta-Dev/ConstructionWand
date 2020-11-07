package thetadev.constructionwand.client;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import thetadev.constructionwand.basics.WandUtil;
import thetadev.constructionwand.job.WandJob;

import java.util.Set;

public class RenderBlockPreview
{
    public WandJob wandJob;
    public Set<BlockPos> undoBlocks;

    public void renderBlockHighlight(PlayerEntity player, World world, BlockHitResult rtr, MatrixStack ms, VertexConsumer vertexConsumer,
                                     double renderPosX, double renderPosY, double renderPosZ) {
        if(player == null) return;
        Set<BlockPos> blocks;
        float colorR = 0, colorG = 0, colorB = 0;

        ItemStack wand = WandUtil.holdingWand(player);
        if(wand == null) return;

        if(!(player.isSneaking() && Screen.hasControlDown())) {
            if(wandJob == null || !(wandJob.getHitResult().equals(rtr)) || !(wandJob.getWand().equals(wand))) {
                wandJob = WandJob.getJob(player, world, rtr, wand);
            }

            blocks = wandJob.getBlockPositions();
        }
        else {
            blocks = undoBlocks;
            colorG = 1;
        }

        if(blocks == null || blocks.isEmpty()) return;

        renderBlockList(blocks, ms, vertexConsumer, colorR, colorG, colorB, renderPosX, renderPosY, renderPosZ);
    }

    private void renderBlockList(Set<BlockPos> blocks, MatrixStack ms, VertexConsumer vertexConsumer, float red, float green, float blue,
                                 double renderPosX, double renderPosY, double renderPosZ) {
        ms.push();
        ms.translate(-renderPosX, -renderPosY, -renderPosZ);

        for(BlockPos block : blocks) {
            Box aabb = new Box(block);
            WorldRenderer.drawBox(ms, vertexConsumer, aabb, red, green, blue, 0.4F);
        }
        ms.pop();
    }
}
