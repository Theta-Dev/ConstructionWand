package thetadev.constructionwand.basics;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ReplacementRegistry
{
	private static final HashMap<String, HashSet<Item>> replacements = new HashMap<>();

	static {
		add("dirt", Items.DIRT);
		add("dirt", Items.GRASS_BLOCK);
		add("dirt", Items.COARSE_DIRT);
		add("dirt", Items.PODZOL);
		add("dirt", Items.MYCELIUM);
	}

	private static void init() {
		ArrayList<String> configList = new ArrayList<>();
		configList.add("minecraft:dirt;minecraft:grass_block;minecraft:coarse_dirt");

		for(String key : configList) {
			String[] itemIDs = key.split(";");
			
		}
	}

	private static void add(String name, Item item) {
		HashSet<Item> set = replacements.get(name);
		if(set == null) {
			set = new HashSet<>();
			set.add(item);
			replacements.put(name, set);
		}
		else set.add(item);
	}

	public static Set<Item> getMatchingSet(Item item) {
		HashSet<Item> res = new HashSet<>();

		for(HashSet<Item> set : replacements.values()) {
			if(set.contains(item)) res.addAll(set);
		}
		res.remove(item);
		return res;
	}

	public static boolean matchBlocks(Block b1, Block b2) {
		if(b1 == b2) return true;

		for(HashSet<Item> set : replacements.values()) {
			if(set.contains(b1.asItem()) && set.contains(b2.asItem())) return true;
		}
		return false;
	}
}
