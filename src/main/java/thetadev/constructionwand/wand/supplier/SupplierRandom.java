package thetadev.constructionwand.wand.supplier;

import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import thetadev.constructionwand.basics.WandUtil;
import thetadev.constructionwand.basics.pool.RandomPool;
import thetadev.constructionwand.wand.WandJob;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;

public class SupplierRandom extends SupplierInventory
{
    public SupplierRandom(WandJob job) {
        super(job);
    }

    @Override
    protected void getSupply(@Nullable BlockItem target) {
        itemCounts = new LinkedHashMap<>();

        // Random mode -> add all items from hotbar
        itemPool = new RandomPool<>(player.getRNG());

        for(ItemStack stack : WandUtil.getHotbarWithOffhand(player)) {
            if(stack.getItem() instanceof BlockItem) addBlockItem((BlockItem) stack.getItem());
        }

        // Count inventory supply
        countSupply();
    }
}
