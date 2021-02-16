package thetadev.constructionwand.data;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.block.ModBlocks;

import javax.annotation.Nonnull;

public class BlockStateGenerator extends BlockStateProvider
{
    public BlockStateGenerator(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, ConstructionWand.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        for(Block block : ModBlocks.ALL_BLOCKS) {
            if(block instanceof ICustomBlockState)
                ((ICustomBlockState) block).generateCustomBlockState(this);
            else simpleBlock(block);
        }
    }

    @Nonnull
    @Override
    public String getName() {
        return ConstructionWand.MODNAME + " blockstates";
    }
}
