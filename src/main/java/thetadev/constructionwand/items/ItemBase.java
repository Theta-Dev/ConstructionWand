package thetadev.constructionwand.items;

import net.minecraft.item.Item;

public class ItemBase extends Item
{
    public ItemBase(Properties properties, String name) {
        super(properties);
        setRegistryName(name);
    }
}
