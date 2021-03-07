package thetadev.constructionwand.api;

public interface IWandCore extends IWandUpgrade
{
    int getColor();

    IWandAction getWandAction();
}
