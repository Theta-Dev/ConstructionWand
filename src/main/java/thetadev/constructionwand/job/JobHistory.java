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
	private final HashMap<UUID, HistoryEntry> history;

	public JobHistory() {
		history = new HashMap<>();
	}

	private HistoryEntry getEntryFromPlayer(PlayerEntity player) {
		return history.computeIfAbsent(player.getUniqueID(), k -> new HistoryEntry());
	}

	private LinkedList<WandJob> getJobsFromPlayer(PlayerEntity player) {
		return getEntryFromPlayer(player).jobs;
	}

	public void add(WandJob job) {
		LinkedList<WandJob> list = getJobsFromPlayer(job.getPlayer());
		list.add(job);
		while(list.size() > ConfigHandler.UNDO_HISTORY.get()) list.removeFirst();
	}

	public void removePlayer(PlayerEntity player) {
		history.remove(player.getUniqueID());
	}

	public void updateClient(PlayerEntity player, boolean ctrlDown) {
		World world = player.getEntityWorld();
		if(world.isRemote) return;

		// Set state of CTRL key
		HistoryEntry entry = getEntryFromPlayer(player);
		entry.undoActive = ctrlDown;

		LinkedList<WandJob> jobs = entry.jobs;
		LinkedList<BlockPos> positions;

		// Send block positions of most recent job to client
		if(jobs.isEmpty()) positions = new LinkedList<>();
		else {
			WandJob job = jobs.getLast();
			if(job == null || !job.getWorld().equals(world)) positions = new LinkedList<>();
			else positions = job.getBlockPositions();
		}

		PacketUndoBlocks packet = new PacketUndoBlocks(positions);
		ConstructionWand.instance.HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), packet);
	}

	public boolean isUndoActive(PlayerEntity player) {
		return getEntryFromPlayer(player).undoActive;
	}

	public WandJob getForUndo(PlayerEntity player, World world, BlockPos pos) {
		// If CTRL key is not pressed, return
		HistoryEntry entry = getEntryFromPlayer(player);
		if(!entry.undoActive) return null;

		// Get the most recent job for undo
		LinkedList<WandJob> jobs = entry.jobs;
		if(jobs.isEmpty()) return null;
		WandJob job = jobs.getLast();

		if(job.getWorld().equals(world) && job.getBlockPositions().contains(pos)) {
			// Update job player entity, they could have changed by rejoin/respawn
			job.setPlayer(player);

			// Remove undo job, sent update to client and return it
			jobs.remove(job);
			updateClient(player, true);
			return job;
		}
		return null;
	}

	private static class HistoryEntry {
		public LinkedList<WandJob> jobs;
		public boolean undoActive;

		public HistoryEntry() {
			jobs = new LinkedList<>();
			undoActive = false;
		}
	}
}
