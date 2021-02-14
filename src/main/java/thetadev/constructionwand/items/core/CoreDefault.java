package thetadev.constructionwand.items.core;

import net.minecraft.util.ResourceLocation;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.api.IWandAction;
import thetadev.constructionwand.api.IWandCore;
import thetadev.constructionwand.wand.WandJob;
import thetadev.constructionwand.wand.action.ActionConstruction;

public class CoreDefault implements IWandCore
{
    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public IWandAction getWandAction(WandJob wandJob) {
        return new ActionConstruction(wandJob);
    }

    @Override
    public ResourceLocation getRegistryName() {
        return ConstructionWand.loc("default");
    }
}
