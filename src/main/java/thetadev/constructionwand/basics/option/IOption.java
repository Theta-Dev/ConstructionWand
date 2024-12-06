package thetadev.constructionwand.basics.option;

import net.minecraft.core.component.DataComponentType;
import thetadev.constructionwand.ConstructionWand;

public interface IOption<T>
{
    DataComponentType<?> getComponentType();

    String getKey();

    String getValueString();

    void setValueString(String val);

    default String getKeyTranslation() {
        return ConstructionWand.MODID + ".option." + getKey();
    }

    default String getValueTranslation() {
        return ConstructionWand.MODID + ".option." + getKey() + "." + getValueString();
    }

    default String getDescTranslation() {
        return ConstructionWand.MODID + ".option." + getKey() + "." + getValueString() + ".desc";
    }

    boolean isEnabled();

    void set(T val);

    T get();

    T next(boolean dir);

    default T next() {
        return next(true);
    }
}
