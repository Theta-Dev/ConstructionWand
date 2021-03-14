package thetadev.constructionwand.basics.option;

import net.minecraft.nbt.CompoundNBT;
import thetadev.constructionwand.api.IWandUpgrade;

public class WandUpgradesSelectable<T extends IWandUpgrade> extends WandUpgrades<T> implements IOption<T>
{
    private byte selector;

    public WandUpgradesSelectable(CompoundNBT tag, String key, T dval) {
        super(tag, key, dval);
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getValueString() {
        return get().getRegistryName().toString();
    }

    @Override
    public void setValueString(String val) {
        for(byte i = 0; i < upgrades.size(); i++) {
            if(upgrades.get(i).getRegistryName().toString().equals(val)) {
                selector = i;
                serializeSelector();
                return;
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return upgrades.size() > 1;
    }

    @Override
    public void set(T val) {
        selector = (byte) upgrades.indexOf(val);
        fixSelector();
        serializeSelector();
    }

    @Override
    public T get() {
        fixSelector();
        return upgrades.get(selector);
    }

    @Override
    public T next(boolean dir) {
        selector++;
        fixSelector();
        serializeSelector();
        return get();
    }

    private void fixSelector() {
        if(selector < 0 || selector >= upgrades.size()) selector = 0;
    }

    @Override
    protected void deserialize() {
        super.deserialize();

        selector = tag.getByte(key + "_sel");
        fixSelector();
    }

    @Override
    protected void serialize() {
        super.serialize();

        serializeSelector();
    }

    private void serializeSelector() {
        tag.putByte(key + "_sel", selector);
    }
}
