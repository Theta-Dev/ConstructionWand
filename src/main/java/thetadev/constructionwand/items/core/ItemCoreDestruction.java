package thetadev.constructionwand.items.core;

import thetadev.constructionwand.api.IWandAction;
import thetadev.constructionwand.wand.action.ActionDestruction;

public class ItemCoreDestruction extends ItemCore
{
    public ItemCoreDestruction(String name, Properties properties) {
        super(name, properties);
    }

    @Override
    public int getColor() {
        return 0xFF0000;
    }

    @Override
    public IWandAction getWandAction() {
        return new ActionDestruction();
    }
}
