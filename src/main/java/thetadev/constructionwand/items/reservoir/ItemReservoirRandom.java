package thetadev.constructionwand.items.reservoir;

import thetadev.constructionwand.api.IWandReservoir;
import thetadev.constructionwand.api.IWandSupplier;
import thetadev.constructionwand.items.ItemBase;
import thetadev.constructionwand.wand.WandJob;
import thetadev.constructionwand.wand.supplier.SupplierRandom;

public class ItemReservoirRandom extends ItemBase implements IWandReservoir
{
    public ItemReservoirRandom(Properties properties, String name) {
        super(properties, name);
    }

    @Override
    public IWandSupplier getWandSupplier(WandJob job) {
        return new SupplierRandom(job);
    }
}
