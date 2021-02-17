package thetadev.constructionwand.wand.supplier;

import net.minecraft.item.BlockItem;
import thetadev.constructionwand.block.ModBlocks;
import thetadev.constructionwand.wand.WandJob;

public class SupplierConjuration extends SupplierBlockgen
{
    public SupplierConjuration(WandJob job) {
        super(job);
    }

    @Override
    protected BlockItem getBlockItem() {
        return (BlockItem) ModBlocks.CONJURED_BLOCK.asItem();
    }
}
