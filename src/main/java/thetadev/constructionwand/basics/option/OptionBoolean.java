package thetadev.constructionwand.basics.option;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

public class OptionBoolean implements IOption<Boolean>
{
    private final ItemStack stack;
    private final DataComponentType<Boolean> componentType;
    private final String key;
    private final boolean enabled;
    private boolean value;

    public OptionBoolean(ItemStack stack, DataComponentType<Boolean> componentType, String key, boolean dval, boolean enabled) {
        this.stack = stack;
        this.componentType = componentType;
        this.key = key;
        this.enabled = enabled;

        value = stack.getOrDefault(componentType, dval);
    }

    public OptionBoolean(ItemStack stack, DataComponentType<Boolean> componentType, String key, boolean dval) {
        this(stack, componentType, key, dval, true);
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public DataComponentType<Boolean> getComponentType() {
        return componentType;
    }

    @Override
    public String getValueString() {
        return value ? "yes" : "no";
    }

    @Override
    public void setValueString(String val) {
        set(val.equals("yes"));
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void set(Boolean val) {
        if(!enabled) return;
        value = val;
        stack.set(componentType, val);
    }

    @Override
    public Boolean get() {
        return value;
    }

    @Override
    public Boolean next(boolean dir) {
        set(!value);
        return value;
    }
}
