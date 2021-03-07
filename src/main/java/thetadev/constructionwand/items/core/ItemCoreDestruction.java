package thetadev.constructionwand.items.core;

import thetadev.constructionwand.api.IWandAction;
import thetadev.constructionwand.api.IWandCore;
import thetadev.constructionwand.items.ItemBase;
import thetadev.constructionwand.wand.WandJob;
import thetadev.constructionwand.wand.action.ActionDestruction;

public class ItemCoreDestruction extends ItemBase implements IWandCore
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
