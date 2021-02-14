package thetadev.constructionwand.api;

import thetadev.constructionwand.wand.undo.ISnapshot;

import java.util.List;

public interface IWandAction
{
    List<ISnapshot> getSnapshots(IWandSupplier supplier);
}
