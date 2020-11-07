package thetadev.constructionwand.containers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.collection.DefaultedList;

public class ContainerHelper
{
    public static final int TAG_COMPOUND = 10;

    public static void loadAllItems(CompoundTag tag, DefaultedList<ItemStack> list) {
        ListTag listnbt = tag.getList("Items", 10);

        for(int i = 0; i < listnbt.size(); ++i) {
            CompoundTag compoundnbt = listnbt.getCompound(i);
            int j = compoundnbt.getByte("Slot") & 255;
            if (j >= 0 && j < list.size()) {
                list.set(j, ItemStack.fromTag(compoundnbt));
            }
        }

    }

    public static CompoundTag saveAllItems(CompoundTag tag, DefaultedList<ItemStack> list, boolean saveEmpty) {
        ListTag listnbt = new ListTag();

        for(int i = 0; i < list.size(); ++i) {
            ItemStack itemstack = list.get(i);
            if (!itemstack.isEmpty()) {
                CompoundTag compoundnbt = new CompoundTag();
                compoundnbt.putByte("Slot", (byte)i);
                itemstack.setTag(compoundnbt);
                listnbt.add(compoundnbt);
            }
        }

        if (!listnbt.isEmpty() || saveEmpty) {
            tag.put("Items", listnbt);
        }

        return tag;
    }
}
