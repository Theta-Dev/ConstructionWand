package thetadev.constructionwand.wand.undo;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thetadev.constructionwand.basics.ConfigServer;
import thetadev.constructionwand.network.ModMessages;
import thetadev.constructionwand.network.PacketUndoBlocks;

import java.util.*;
import java.util.stream.Collectors;

public class UndoHistory
{
    private final HashMap<UUID, PlayerEntry> history;

    public UndoHistory() {
        history = new HashMap<>();
    }

    private PlayerEntry getEntryFromPlayer(Player player) {
        return history.computeIfAbsent(player.getUUID(), k -> new PlayerEntry());
    }

    public void add(Player player, Level world, List<ISnapshot> placeSnapshots) {
        LinkedList<HistoryEntry> list = getEntryFromPlayer(player).entries;
        list.add(new HistoryEntry(placeSnapshots, world));
        while(list.size() > ConfigServer.UNDO_HISTORY.get()) list.removeFirst();
    }

    public void removePlayer(Player player) {
        history.remove(player.getUUID());
    }

    public void updateClient(Player player, boolean ctrlDown) {
        Level world = player.level();
        if(world.isClientSide) return;

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
        ModMessages.sendToPlayer(packet, (ServerPlayer) player);
    }

    public boolean isUndoActive(Player player) {
        return getEntryFromPlayer(player).undoActive;
    }

    public boolean undo(Player player, Level world, BlockPos pos) {
        // If CTRL key is not pressed, return
        PlayerEntry playerEntry = getEntryFromPlayer(player);
        if(!playerEntry.undoActive) return false;

        // Get the most recent entry for undo
        LinkedList<HistoryEntry> historyEntries = playerEntry.entries;
        if(historyEntries.isEmpty()) return false;
        HistoryEntry entry = historyEntries.getLast();

        // Player has to be in the same world and near the blocks
        if(!entry.world.equals(world) || !entry.withinRange(pos)) return false;

        if(entry.undo(player)) {
            historyEntries.remove(entry);
            updateClient(player, true);
            return true;
        }
        return false;
    }

    private static class PlayerEntry
    {
        public final LinkedList<HistoryEntry> entries;
        public boolean undoActive;

        public PlayerEntry() {
            entries = new LinkedList<>();
            undoActive = false;
        }
    }

    private static class HistoryEntry
    {
        public final List<ISnapshot> placeSnapshots;
        public final Level world;

        public HistoryEntry(List<ISnapshot> placeSnapshots, Level world) {
            this.placeSnapshots = placeSnapshots;
            this.world = world;
        }

        public Set<BlockPos> getBlockPositions() {
            return placeSnapshots.stream().map(ISnapshot::getPos).collect(Collectors.toSet());
        }

        public boolean withinRange(BlockPos pos) {
            Set<BlockPos> positions = getBlockPositions();

            if(positions.contains(pos)) return true;

            for(BlockPos p : positions) {
                if(pos.closerThan(p, 3)) return true;
            }
            return false;
        }

        public boolean undo(Player player) {
            // Check first if all snapshots can be restored
            for(ISnapshot snapshot : placeSnapshots) {
                if(!snapshot.canRestore(world, player)) return false;
            }
            for(ISnapshot snapshot : placeSnapshots) {
                if(snapshot.restore(world, player) && !player.isCreative()) {
                    ItemStack stack = snapshot.getRequiredItems();

                    if(!player.getInventory().add(stack)) {
                        player.drop(stack, false);
                    }
                }
            }
            player.getInventory().setChanged();

            // Play teleport sound
            SoundEvent sound = SoundEvents.CHORUS_FRUIT_TELEPORT;
            world.playSound(null, player.blockPosition(), sound, SoundSource.PLAYERS, 1.0F, 1.0F);

            return true;
        }
    }
}
