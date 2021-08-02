package thetadev.constructionwand.items.wand;


public class ItemWandInfinity extends ItemWand
{
    public ItemWandInfinity(String name, Properties properties) {
        super(name, properties.stacksTo(1).fireResistant());
    }
}
