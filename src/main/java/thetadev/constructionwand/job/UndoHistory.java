package thetadev.constructionwand.job;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.basics.ConfigServer;
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
        return history.computeIfAbsent(player.getUniqueID(), k -> new PlayerEntry());
    }

    public void add(PlayerEntity player, World world, LinkedList<ISnapshot> placeSnapshots) {
        LinkedList<HistoryEntry> list = getEntryFromPlayer(player).entries;
        list.add(new HistoryEntry(placeSnapshots, world));
        while(list.size() > ConfigServer.UNDO_HISTORY.get()) list.removeFirst();
    }

    public void removePlayer(PlayerEntity player) {
        history.remove(player.getUniqueID());
    }

    public void updateClient(PlayerEntity player, boolean ctrlDown) {
        World world = player.getEntityWorld();
        if(world.isRemote) return;

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
        ConstructionWand.instance.HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), packet);
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
        public final LinkedList<ISnapshot> placeSnapshots;
        public final World world;

        public HistoryEntry(LinkedList<ISnapshot> placeSnapshots, World world) {
            this.placeSnapshots = placeSnapshots;
            this.world = world;
        }

        public Set<BlockPos> getBlockPositions() {
            return placeSnapshots.stream().map(ISnapshot::getPos).collect(Collectors.toSet());
        }

        public boolean undo(PlayerEntity player) {
            for(ISnapshot snapshot : placeSnapshots) {
                if(snapshot.restore(world, player) && !player.isCreative()) {
                    ItemStack stack = snapshot.getRequiredItems();

                    if(!player.inventory.addItemStackToInventory(stack)) {
                        player.dropItem(stack, false);
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
