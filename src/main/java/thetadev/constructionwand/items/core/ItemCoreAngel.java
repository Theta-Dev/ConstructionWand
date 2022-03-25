package thetadev.constructionwand.items.core;

import thetadev.constructionwand.api.IWandAction;
import thetadev.constructionwand.wand.action.ActionAngel;

public class ItemCoreAngel extends ItemCore
{
    public ItemCoreAngel(Properties properties) {
        super(properties);
    }

    @Override
    public int getColor() {
        return 0xE9B115;
    }

    @Override
    public IWandAction getWandAction() {
        return new ActionAngel();
    }
}
