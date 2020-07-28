package thetadev.constructionwand;

import net.minecraft.item.Item;
import net.minecraft.item.ItemTier;
import net.minecraft.item.Items;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;

public class ConfigHandler
{
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

	// Durability
	public static final ForgeConfigSpec.IntValue DURABILITY_STONE;
	public static final ForgeConfigSpec.IntValue DURABILITY_IRON;
	public static final ForgeConfigSpec.IntValue DURABILITY_DIAMOND;

	public static final ForgeConfigSpec.IntValue LIMIT_STONE;
	public static final ForgeConfigSpec.IntValue LIMIT_IRON;
	public static final ForgeConfigSpec.IntValue LIMIT_DIAMOND;
	public static final ForgeConfigSpec.IntValue LIMIT_INFINITY;
	public static final ForgeConfigSpec.IntValue LIMIT_CREATIVE;

	public static final ForgeConfigSpec.ConfigValue<List<String>> SUBSTITUTIONS;

	public static final ForgeConfigSpec.IntValue UNDO_HISTORY;

	private static final List<String> defaultSubs =
			Arrays.asList("minecraft:dirt;minecraft:coarse_dirt;minecraft:grass_block;minecraft:grass_path;minecraft:mycelium;minecraft:podzol");

	static {
		BUILDER.comment("Wand durability");
		BUILDER.push("durability");
		DURABILITY_STONE = BUILDER.defineInRange("Stone Wand", ItemTier.STONE.getMaxUses(), 1, Integer.MAX_VALUE);
		DURABILITY_IRON = BUILDER.defineInRange("Iron Wand", ItemTier.IRON.getMaxUses(), 1, Integer.MAX_VALUE);
		DURABILITY_DIAMOND = BUILDER.defineInRange("Diamond Wand", ItemTier.DIAMOND.getMaxUses(), 1, Integer.MAX_VALUE);
		BUILDER.pop();

		BUILDER.comment("Wand block limit");
		BUILDER.push("block_limit");
		LIMIT_STONE = BUILDER.defineInRange("Stone Wand", 9, 1, Integer.MAX_VALUE);
		LIMIT_IRON = BUILDER.defineInRange("Iron Wand", 27, 1, Integer.MAX_VALUE);
		LIMIT_DIAMOND = BUILDER.defineInRange("Diamond Wand", 128, 1, Integer.MAX_VALUE);
		LIMIT_INFINITY = BUILDER.defineInRange("Infinity Wand", 512, 1, Integer.MAX_VALUE);
		LIMIT_CREATIVE = BUILDER.defineInRange("Infinity Wand (Creative)", 1024, 1, Integer.MAX_VALUE);
		BUILDER.pop();

		BUILDER.comment("Wand substitutions");
		BUILDER.push("substitutions");
		SUBSTITUTIONS = BUILDER.define("substitute", defaultSubs);
		BUILDER.pop();

		BUILDER.push("misc");
		UNDO_HISTORY = BUILDER.defineInRange("Undo History", 3, 0, Integer.MAX_VALUE);
		BUILDER.pop();
	}

	public static final ForgeConfigSpec SPEC = BUILDER.build();
}
