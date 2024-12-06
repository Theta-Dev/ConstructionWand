package thetadev.constructionwand.basics.option;

import com.google.common.base.Enums;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

public class OptionEnum<E extends Enum<E>> implements IOption<E>
{
    private final ItemStack stack;
    private final DataComponentType<E> componentType;
    private final String key;
    private final Class<E> enumClass;
    private final boolean enabled;
    private final E dval;
    private E value;

    public OptionEnum(ItemStack stack, DataComponentType<E> componentType, String key, Class<E> enumClass, E dval, boolean enabled) {
        this.stack = stack;
        this.componentType = componentType;
        this.key = key;
        this.enumClass = enumClass;
        this.enabled = enabled;
        this.dval = dval;

        value = stack.getOrDefault(componentType, dval);
    }

    public OptionEnum(ItemStack stack, DataComponentType<E> componentType, String key, Class<E> enumClass, E dval) {
        this(stack, componentType, key, enumClass, dval, true);
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public DataComponentType<?> getComponentType() {
        return componentType;
    }

    @Override
    public String getValueString() {
        return value.name().toLowerCase();
    }

    @Override
    public void setValueString(String val) {
        set(Enums.getIfPresent(enumClass, val.toUpperCase()).or(dval));
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void set(E val) {
        if(!enabled) return;
        value = val;
        stack.set(componentType, val);
    }

    @Override
    public E get() {
        return value;
    }

    @Override
    public E next(boolean dir) {
        E[] enumValues = enumClass.getEnumConstants();
        int i = value.ordinal() + (dir ? 1 : -1);
        if(i < 0) i += enumValues.length;
        set(enumValues[i % enumValues.length]);
        return value;
    }
}
