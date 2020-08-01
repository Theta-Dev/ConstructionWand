package thetadev.constructionwand.job;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import thetadev.constructionwand.basics.ConfigHandler;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.network.PacketUndoBlocks;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

public class JobHistory
{
	private final HashMap<UUID, LinkedList<WandJob>> history;

	public JobHistory() {
		history = new HashMap<>();
	}

	private LinkedList<WandJob> getJobsFromPlayer(PlayerEntity player) {
		return history.computeIfAbsent(player.getUniqueID(), k -> new LinkedList<>());
	}

	public void add(WandJob job) {
		LinkedList<WandJob> list = getJobsFromPlayer(job.getPlayer());
		list.add(job);
		while(list.size() > ConfigHandler.UNDO_HISTORY.get()) list.removeFirst();
	}

	public void updateClient(PlayerEntity player) {
		World world = player.getEntityWorld();
		if(world.isRemote) return;

		LinkedList<BlockPos> positions;
		LinkedList<WandJob> jobs = getJobsFromPlayer(player);
		if(jobs.isEmpty()) positions = new LinkedList<>();
		else {
			WandJob job = jobs.getLast();
			if(job == null || !job.getWorld().equals(world)) positions = new LinkedList<>();
			else positions = job.getBlockPositions();
		}

		PacketUndoBlocks packet = new PacketUndoBlocks(positions);
		ConstructionWand.instance.HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), packet);
	}

	public WandJob getForUndo(PlayerEntity player, World world, BlockPos pos) {
		LinkedList<WandJob> jobs = getJobsFromPlayer(player);
		if(jobs.isEmpty()) return null;
		WandJob job = jobs.getLast();

		if(job.getWorld().equals(world) && job.getBlockPositions().contains(pos)) {
			// Update job player entity, they could have changed by rejoin/respawn
			job.setPlayer(player);

			jobs.remove(job);
			return job;
		}
		return null;
	}
}
