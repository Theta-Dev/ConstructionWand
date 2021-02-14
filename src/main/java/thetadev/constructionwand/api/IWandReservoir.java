package thetadev.constructionwand.api;

import thetadev.constructionwand.wand.WandJob;

public interface IWandReservoir extends IWandUpgrade
{
    IWandSupplier getWandSupplier(WandJob job);
}
