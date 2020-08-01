package thetadev.constructionwand.basics;

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

	public static final ForgeConfigSpec.IntValue ANGEL_STONE;
	public static final ForgeConfigSpec.IntValue ANGEL_IRON;
	public static final ForgeConfigSpec.IntValue ANGEL_DIAMOND;
	public static final ForgeConfigSpec.IntValue ANGEL_INFINITY;

	public static final ForgeConfigSpec.IntValue UNDO_HISTORY;
	public static final ForgeConfigSpec.BooleanValue ANGEL_FALLING;

	static {
		BUILDER.comment("Wand durability");
		BUILDER.push("durability");
		DURABILITY_STONE = BUILDER.defineInRange("StoneWand", ItemTier.STONE.getMaxUses(), 1, Integer.MAX_VALUE);
		DURABILITY_IRON = BUILDER.defineInRange("IronWand", ItemTier.IRON.getMaxUses(), 1, Integer.MAX_VALUE);
		DURABILITY_DIAMOND = BUILDER.defineInRange("DiamondWand", ItemTier.DIAMOND.getMaxUses(), 1, Integer.MAX_VALUE);
		BUILDER.pop();

		BUILDER.comment("Wand block limit");
		BUILDER.push("block_limit");
		LIMIT_STONE = BUILDER.defineInRange("StoneWand", 9, 1, Integer.MAX_VALUE);
		LIMIT_IRON = BUILDER.defineInRange("IronWand", 27, 1, Integer.MAX_VALUE);
		LIMIT_DIAMOND = BUILDER.defineInRange("DiamondWand", 128, 1, Integer.MAX_VALUE);
		LIMIT_INFINITY = BUILDER.defineInRange("InfinityWand", 1024, 1, Integer.MAX_VALUE);
		BUILDER.comment("Infinity Wand used in creative mode");
		LIMIT_CREATIVE = BUILDER.defineInRange("InfinityWandCreative", 2048, 1, Integer.MAX_VALUE);
		BUILDER.pop();

		BUILDER.comment("Max placement distance with angel mode (0 to disable angel mode)");
		BUILDER.push("angel_distance");
		ANGEL_STONE = BUILDER.defineInRange("StoneWand", 0, 0, Integer.MAX_VALUE);
		ANGEL_IRON = BUILDER.defineInRange("IronWand", 1, 0, Integer.MAX_VALUE);
		ANGEL_DIAMOND = BUILDER.defineInRange("DiamondWand", 4, 0, Integer.MAX_VALUE);
		ANGEL_INFINITY = BUILDER.defineInRange("InfinityWand", 8, 0, Integer.MAX_VALUE);
		BUILDER.pop();

		BUILDER.push("misc");
		BUILDER.comment("Number of operations that can be undone");
		UNDO_HISTORY = BUILDER.defineInRange("UndoHistory", 3, 0, Integer.MAX_VALUE);
		BUILDER.comment("Place blocks below you while falling > 10 blocks with angel mode (Can be used to save you from drops/the void)");
		ANGEL_FALLING = BUILDER.define("AngelFalling", false);
		BUILDER.pop();
	}

	public static final ForgeConfigSpec SPEC = BUILDER.build();
}
