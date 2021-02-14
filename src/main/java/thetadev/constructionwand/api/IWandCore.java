package thetadev.constructionwand.api;

import thetadev.constructionwand.wand.WandJob;

public interface IWandCore extends IWandUpgrade
{
    int getColor();

    IWandAction getWandAction(WandJob wandJob);
}
