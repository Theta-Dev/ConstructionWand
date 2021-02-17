package thetadev.constructionwand.wand.supplier;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Items;
import thetadev.constructionwand.wand.WandJob;

public class SupplierPetrogenesis extends SupplierBlockgen
{
    public SupplierPetrogenesis(WandJob job) {
        super(job);
    }

    @Override
    protected BlockItem getBlockItem() {
        return (BlockItem) Items.COBBLESTONE;
    }
}
