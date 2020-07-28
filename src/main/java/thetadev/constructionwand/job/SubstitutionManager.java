package thetadev.constructionwand.job;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import thetadev.constructionwand.ConfigHandler;

import java.util.LinkedList;
import java.util.List;

public class SubstitutionManager
{
	private final LinkedList<LinkedList<Item>> substitutions;

	public SubstitutionManager() {
		substitutions = new LinkedList<>();
	}

	public void register() {
		List<String> sets = ConfigHandler.SUBSTITUTIONS.get();

		for(String set : sets) {
			String[] keys = set.split(";");
			LinkedList<Item> list = new LinkedList<>();

			for(String key : keys) {
				Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(key));
				if(item == null) continue;
				list.add(item);
			}
			if(!list.isEmpty()) substitutions.add(list);
		}
	}

	public LinkedList<Item> getSubstitutions(Item item) {
		LinkedList<Item> res = new LinkedList<>();

		for(LinkedList<Item> set : substitutions) {
			if(set.contains(item)) {
				res = (LinkedList<Item>) set.clone();
				res.remove(item);
				return res;
			}
		}
		return res;
	}
}
