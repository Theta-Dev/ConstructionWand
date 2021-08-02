package thetadev.constructionwand.items;

import net.minecraft.world.item.Item;
import thetadev.constructionwand.ConstructionWand;

public class ItemBase extends Item
{
    public ItemBase(String name, Properties properties) {
        super(properties);
        setRegistryName(ConstructionWand.MODID, name);
    }
}
