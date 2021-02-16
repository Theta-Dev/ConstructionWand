package thetadev.constructionwand.items.core;

import thetadev.constructionwand.api.IWandAction;
import thetadev.constructionwand.api.IWandCore;
import thetadev.constructionwand.items.ItemBase;
import thetadev.constructionwand.wand.WandJob;
import thetadev.constructionwand.wand.action.ActionAngel;

public class ItemCoreAngel extends ItemBase implements IWandCore
{
    public ItemCoreAngel(String name, Properties properties) {
        super(name, properties);
    }

    @Override
    public int getColor() {
        return 0xE9B115;
    }

    @Override
    public IWandAction getWandAction(WandJob wandJob) {
        return new ActionAngel(wandJob);
    }
}
