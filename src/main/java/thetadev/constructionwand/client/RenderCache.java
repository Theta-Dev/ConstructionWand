package thetadev.constructionwand.client;

import net.minecraft.util.math.BlockPos;
import thetadev.constructionwand.job.WandJob;

import java.util.LinkedList;

public class RenderCache
{
	// Caches the last WandJob used for block preview to save performance
	// Also stores list of undoBlocks

	public WandJob wandJob;
	public LinkedList<BlockPos> undoBlocks;
}
