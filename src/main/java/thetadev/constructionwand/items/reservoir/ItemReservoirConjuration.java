package thetadev.constructionwand.items.reservoir;

import thetadev.constructionwand.api.IWandReservoir;
import thetadev.constructionwand.api.IWandSupplier;
import thetadev.constructionwand.items.ItemBase;
import thetadev.constructionwand.wand.WandJob;
import thetadev.constructionwand.wand.supplier.SupplierConjuration;

public class ItemReservoirConjuration extends ItemBase implements IWandReservoir
{
    public ItemReservoirConjuration(String name, Properties properties) {
        super(name, properties);
    }

    @Override
    public IWandSupplier getWandSupplier(WandJob job) {
        return new SupplierConjuration(job);
    }
}
