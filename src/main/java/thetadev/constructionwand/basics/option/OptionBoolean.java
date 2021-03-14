package thetadev.constructionwand.basics.option;

import net.minecraft.nbt.CompoundNBT;

public class OptionBoolean implements IOption<Boolean>
{
    private final CompoundNBT tag;
    private final String key;
    private final boolean enabled;
    private boolean value;

    public OptionBoolean(CompoundNBT tag, String key, boolean dval, boolean enabled) {
        this.tag = tag;
        this.key = key;
        this.enabled = enabled;

        if(tag.contains(key)) value = tag.getBoolean(key);
        else value = dval;
    }

    public OptionBoolean(CompoundNBT tag, String key, boolean dval) {
        this(tag, key, dval, true);
    }

    @Override
    public String getKey() {
        return key;
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
        tag.putBoolean(key, value);
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
