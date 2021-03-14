package thetadev.constructionwand.items.wand;


public class ItemWandInfinity extends ItemWand
{
    public ItemWandInfinity(String name, Properties properties) {
        super(name, properties.maxStackSize(1).isBurnable());
    }
}
