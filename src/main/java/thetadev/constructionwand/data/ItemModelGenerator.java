package thetadev.constructionwand.data;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.items.ModItems;

import javax.annotation.Nonnull;

public class ItemModelGenerator extends ItemModelProvider
{
    public ItemModelGenerator(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
        super(packOutput, ConstructionWand.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for(DeferredHolder<Item, ? extends Item> itemObject : ModItems.ITEMS.getEntries()) {
            Item item = itemObject.get();
            String name = itemObject.getId().getPath();

            if(item instanceof ICustomItemModel)
                ((ICustomItemModel) item).generateCustomItemModel(this, name);
            else if(item instanceof BlockItem)
                withExistingParent(name, modLoc("block/" + name));
            else withExistingParent(name, "item/generated").texture("layer0", "item/" + name);
        }
    }

    @Nonnull
    @Override
    public String getName() {
        return ConstructionWand.MODNAME + " item models";
    }
}
