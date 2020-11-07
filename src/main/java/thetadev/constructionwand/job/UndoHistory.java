package thetadev.constructionwand.job;

import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.basics.ReplacementRegistry;
import thetadev.constructionwand.basics.WandUtil;
import thetadev.constructionwand.network.PacketUndoBlocks;

import java.util.*;
import java.util.stream.Collectors;

public class UndoHistory
{
	private final HashMap<UUID, PlayerEntry> history;

	public UndoHistory() {
		history = new HashMap<>();
	}

	private PlayerEntry getEntryFromPlayer(PlayerEntity player) {
		return history.computeIfAbsent(player.getUuid(), k -> new PlayerEntry());
	}

	public void add(PlayerEntity player, World world, LinkedList<PlaceSnapshot> placeSnapshots) {
		LinkedList<HistoryEntry> list = getEntryFromPlayer(player).entries;
		list.add(new HistoryEntry(placeSnapshots, world));
		while(list.size() > ConstructionWand.instance.config.UNDO_HISTORY) list.removeFirst();
	}

	public void removePlayer(PlayerEntity player) {
		history.remove(player.getUuid());
	}

	public void updateClient(PlayerEntity player, boolean ctrlDown) {
		World world = player.getEntityWorld();
		if(world.isClient) return;

		// Set state of CTRL key
		PlayerEntry playerEntry = getEntryFromPlayer(player);
		playerEntry.undoActive = ctrlDown;

		LinkedList<HistoryEntry> historyEntries = playerEntry.entries;
		Set<BlockPos> positions;

		// Send block positions of most recent entry to client
		if(historyEntries.isEmpty()) positions = Collections.emptySet();
		else {
			HistoryEntry entry = historyEntries.getLast();

			if(entry == null || !entry.world.equals(world)) positions = Collections.emptySet();
			else positions = entry.getBlockPositions();
		}

		PacketUndoBlocks packet = new PacketUndoBlocks(positions);
		ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, PacketUndoBlocks.ID, packet.encode());
	}

	public boolean isUndoActive(PlayerEntity player) {
		return getEntryFromPlayer(player).undoActive;
	}

	public boolean undo(PlayerEntity player, World world, BlockPos pos) {
		// If CTRL key is not pressed, return
		PlayerEntry playerEntry = getEntryFromPlayer(player);
		if(!playerEntry.undoActive) return false;

		// Get the most recent entry for undo
		LinkedList<HistoryEntry> historyEntries = playerEntry.entries;
		if(historyEntries.isEmpty()) return false;
		HistoryEntry entry = historyEntries.getLast();

		if(entry.world.equals(world) && entry.getBlockPositions().contains(pos)) {
			// Remove history entry, sent update to client and undo it
			historyEntries.remove(entry);
			updateClient(player, true);
			return entry.undo(player);
		}
		return false;
	}

	private static class PlayerEntry {
		public final LinkedList<HistoryEntry> entries;
		public boolean undoActive;

		public PlayerEntry() {
			entries = new LinkedList<>();
			undoActive = false;
		}
	}

	private static class HistoryEntry {
		public final LinkedList<PlaceSnapshot> placeSnapshots;
		public final World world;

		public HistoryEntry(LinkedList<PlaceSnapshot> placeSnapshots, World world) {
			this.placeSnapshots = placeSnapshots;
			this.world = world;
		}

		public Set<BlockPos> getBlockPositions() {
			return placeSnapshots.stream().map(snapshot -> snapshot.pos).collect(Collectors.toSet());
		}

		public boolean undo(PlayerEntity player) {
			for(PlaceSnapshot snapshot : placeSnapshots) {
				BlockState currentBlock = world.getBlockState(snapshot.pos);

				// If placed block is still present and can be broken, break it and return item
				if(world.canPlayerModifyAt(player, snapshot.pos) &&
						(player.isCreative() ||
								(currentBlock.getHardness(world, snapshot.pos) > -1 && world.getBlockEntity(snapshot.pos) == null && ReplacementRegistry.matchBlocks(currentBlock.getBlock(), snapshot.block.getBlock()))))
				{
					world.removeBlock(snapshot.pos, false);

					if(!player.isCreative()) {
						ItemStack stack = new ItemStack(snapshot.item);
						if(!player.inventory.insertStack(stack)) {
							player.dropItem(stack, false);
						}
					}
				}
			}
			player.inventory.markDirty();

			// Play teleport sound
			SoundEvent sound = SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT;
			world.playSound(null, WandUtil.playerPos(player), sound, SoundCategory.PLAYERS, 1.0F, 1.0F);

			return true;
		}
	}
}
