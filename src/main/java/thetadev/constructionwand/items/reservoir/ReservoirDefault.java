package thetadev.constructionwand.items.reservoir;

import net.minecraft.util.ResourceLocation;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.api.IWandReservoir;
import thetadev.constructionwand.api.IWandSupplier;
import thetadev.constructionwand.wand.WandJob;
import thetadev.constructionwand.wand.supplier.SupplierInventory;

public class ReservoirDefault implements IWandReservoir
{
    @Override
    public IWandSupplier getWandSupplier(WandJob job) {
        return new SupplierInventory(job);
    }

    @Override
    public ResourceLocation getRegistryName() {
        return ConstructionWand.loc("default");
    }
}
