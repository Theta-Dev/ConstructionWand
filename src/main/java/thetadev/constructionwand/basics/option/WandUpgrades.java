package thetadev.constructionwand.basics.option;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.api.IWandUpgrade;

import java.util.ArrayList;

public class WandUpgrades<T extends IWandUpgrade>
{
    protected final CompoundTag tag;
    protected final String key;
    protected final ArrayList<T> upgrades;
    protected final T dval;

    public WandUpgrades(CompoundTag tag, String key, T dval) {
        this.tag = tag;
        this.key = key;
        this.dval = dval;

        upgrades = new ArrayList<>();
        if(dval != null) upgrades.add(0, dval);

        deserialize();
    }

    protected void deserialize() {
        ListTag listnbt = tag.getList(key, Tag.TAG_STRING);
        boolean require_fix = false;

        for(int i = 0; i < listnbt.size(); i++) {
            String str = listnbt.getString(i);
            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(str));

            T data;
            try {
                //noinspection unchecked
                data = (T) item;
                upgrades.add(data);
            } catch(ClassCastException e) {
                ConstructionWand.LOGGER.warn("Invalid wand upgrade: " + str);
                require_fix = true;
            }
        }
        if(require_fix) serialize();
    }

    protected void serialize() {
        ListTag listnbt = new ListTag();

        for(T item : upgrades) {
            if(item == dval) continue;
            listnbt.add(StringTag.valueOf(item.getRegistryName().toString()));
        }
        tag.put(key, listnbt);
    }

    public boolean addUpgrade(T upgrade) {
        if(hasUpgrade(upgrade)) return false;

        upgrades.add(upgrade);
        serialize();
        return true;
    }

    public boolean hasUpgrade(T upgrade) {
        return upgrades.contains(upgrade);
    }

    public ArrayList<T> getUpgrades() {
        return upgrades;
    }
}
