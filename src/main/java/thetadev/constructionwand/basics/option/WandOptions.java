package thetadev.constructionwand.basics.option;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import thetadev.constructionwand.api.IWandCore;
import thetadev.constructionwand.api.IWandUpgrade;
import thetadev.constructionwand.basics.ReplacementRegistry;
import thetadev.constructionwand.items.core.CoreDefault;

import javax.annotation.Nullable;

public class WandOptions
{
    public final CompoundNBT tag;

    private static final String TAG_ROOT = "wand_options";

    public enum LOCK
    {
        HORIZONTAL,
        VERTICAL,
        NORTHSOUTH,
        EASTWEST,
        NOLOCK
    }

    public enum DIRECTION
    {
        TARGET,
        PLAYER
    }

    public enum MATCH
    {
        EXACT,
        SIMILAR,
        ANY
    }

    public final WandUpgradesSelectable<IWandCore> cores;

    public final OptionEnum<LOCK> lock;
    public final OptionEnum<DIRECTION> direction;
    public final OptionBoolean replace;
    public final OptionEnum<MATCH> match;
    public final OptionBoolean random;

    public final IOption<?>[] allOptions;

    public WandOptions(ItemStack wandStack) {
        tag = wandStack.getOrCreateChildTag(TAG_ROOT);

        cores = new WandUpgradesSelectable<>(tag, "cores", new CoreDefault());

        lock = new OptionEnum<>(tag, "lock", LOCK.class, LOCK.NOLOCK);
        direction = new OptionEnum<>(tag, "direction", DIRECTION.class, DIRECTION.TARGET);
        replace = new OptionBoolean(tag, "replace", true);
        match = new OptionEnum<>(tag, "match", MATCH.class, MATCH.SIMILAR);
        random = new OptionBoolean(tag, "random", false);

        allOptions = new IOption[]{cores, lock, direction, replace, match, random};
    }

    @Nullable
    public IOption<?> get(String key) {
        for(IOption<?> option : allOptions) {
            if(option.getKey().equals(key)) return option;
        }
        return null;
    }

    public boolean testLock(LOCK l) {
        if(lock.get() == LOCK.NOLOCK) return true;
        return lock.get() == l;
    }

    public boolean matchBlocks(Block b1, Block b2) {
        switch(match.get()) {
            case EXACT:
                return b1 == b2;
            case SIMILAR:
                return ReplacementRegistry.matchBlocks(b1, b2);
            case ANY:
                return b1 != Blocks.AIR && b2 != Blocks.AIR;
        }
        return false;
    }

    public boolean hasUpgrade(IWandUpgrade upgrade) {
        if(upgrade instanceof IWandCore) return cores.hasUpgrade((IWandCore) upgrade);
        return false;
    }

    public boolean addUpgrade(IWandUpgrade upgrade) {
        if(upgrade instanceof IWandCore) return cores.addUpgrade((IWandCore) upgrade);
        return false;
    }
}
