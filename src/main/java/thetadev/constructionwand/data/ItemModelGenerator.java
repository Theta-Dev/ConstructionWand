package thetadev.constructionwand.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.items.ModItems;

import javax.annotation.Nonnull;

public class ItemModelGenerator extends ItemModelProvider
{
    public ItemModelGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, ConstructionWand.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for(RegistryObject<Item> itemObject : ModItems.ITEMS.getEntries()) {
            Item item = itemObject.get();
            String name = item.getRegistryName().getPath();

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
