package thetadev.constructionwand.basics;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import net.minecraft.item.ToolMaterials;
import thetadev.constructionwand.ConstructionWand;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

@Config(name = ConstructionWand.MODID)
public class ModConfig implements ConfigData
{
    // Client
    public boolean SHIFTCTRL_MODE = false;
    public boolean SHIFTCTRL_GUI = false;

    // Misc
    public int LIMIT_CREATIVE = 2048;
    public int MAX_RANGE = 256;
    public int UNDO_HISTORY = 3;
    public boolean ANGEL_FALLING = false;
    public List<String> SIMILAR_BLOCKS = Arrays.asList("minecraft:dirt;minecraft:grass_block;minecraft:coarse_dirt;minecraft:podzol;minecraft:mycelium;minecraft:farmland;minecraft:grass_path");

    // Wand properties
    public int STONE_WAND_DURABILITY = ToolMaterials.STONE.getDurability();
    public int STONE_WAND_LIMIT = 9;
    public int STONE_WAND_ANGEL = 0;

    public int IRON_WAND_DURABILITY = ToolMaterials.STONE.getDurability();
    public int IRON_WAND_LIMIT = 27;
    public int IRON_WAND_ANGEL = 1;

    public int DIAMOND_WAND_DURABILITY = ToolMaterials.STONE.getDurability();
    public int DIAMOND_WAND_LIMIT = 128;
    public int DIAMOND_WAND_ANGEL = 4;

    public int INFINITY_WAND_LIMIT = 1024;
    public int INFINITY_WAND_ANGEL = 8;

    public int getWandDurability(String name) {
        if(name.equals("infinity_wand")) return Integer.MAX_VALUE;
        return getValue(name, "durability");
    }

    public int getWandLimit(String name) {
        return getValue(name, "limit");
    }

    public int getWandAngel(String name) {
        return getValue(name, "angel");
    }

    private int getValue(String name, String id) {
        for(Field field : getClass().getFields()) {
            if(field.getName().equals(name.toUpperCase() + "_" + id.toUpperCase())) {
                try {
                    return field.getInt(this);
                } catch(IllegalAccessException e) {
                    ConstructionWand.LOGGER.error(e);
                }
            }
        }
        ConstructionWand.LOGGER.error("Could not get value " + id + " for " + name);
        return 0;
    }
}
