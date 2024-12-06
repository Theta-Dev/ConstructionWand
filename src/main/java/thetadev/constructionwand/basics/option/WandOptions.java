package thetadev.constructionwand.basics.option;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import thetadev.constructionwand.api.IWandCore;
import thetadev.constructionwand.api.IWandUpgrade;
import thetadev.constructionwand.basics.ReplacementRegistry;
import thetadev.constructionwand.component.ModDataComponents;
import thetadev.constructionwand.items.core.CoreDefault;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.function.IntFunction;

public class WandOptions
{
    public enum LOCK implements StringRepresentable
    {
        HORIZONTAL(0),
        VERTICAL(1),
        NORTHSOUTH(2),
        EASTWEST(3),
        NOLOCK(4);

        public static final Codec<LOCK> CODEC = StringRepresentable.fromEnum(LOCK::values);
        private static final IntFunction<LOCK> BY_ID = ByIdMap.continuous(LOCK::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final StreamCodec<ByteBuf, LOCK> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, LOCK::getId);

        private final int id;

        LOCK(int id) {
            this.id = id;
        }

        public int getId() {
            return this.id;
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    public enum DIRECTION implements StringRepresentable
    {
        TARGET(0),
        PLAYER(1);

        public static final Codec<DIRECTION> CODEC = StringRepresentable.fromEnum(DIRECTION::values);
        private static final IntFunction<DIRECTION> BY_ID = ByIdMap.continuous(DIRECTION::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final StreamCodec<ByteBuf, DIRECTION> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, DIRECTION::getId);

        private final int id;

        DIRECTION(int id) {
            this.id = id;
        }

        public int getId() {
            return this.id;
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    public enum MATCH implements StringRepresentable
    {
        EXACT(0),
        SIMILAR(1),
        ANY(2);

        public static final Codec<MATCH> CODEC = StringRepresentable.fromEnum(MATCH::values);
        private static final IntFunction<MATCH> BY_ID = ByIdMap.continuous(MATCH::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final StreamCodec<ByteBuf, MATCH> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, MATCH::getId);

        private final int id;

        MATCH(int id) {
            this.id = id;
        }

        public int getId() {
            return this.id;
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    public final WandUpgradesSelectable<IWandCore> cores;

    public final OptionEnum<LOCK> lock;
    public final OptionEnum<DIRECTION> direction;
    public final OptionBoolean replace;
    public final OptionEnum<MATCH> match;
    public final OptionBoolean random;

    public final IOption<?>[] allOptions;

    public WandOptions(ItemStack wandStack) {
        cores = new WandUpgradesSelectable<>(wandStack, ModDataComponents.CORES.get(), "cores", new CoreDefault());

        lock = new OptionEnum<>(wandStack, ModDataComponents.LOCK.get(),"lock", LOCK.class, LOCK.NOLOCK);
        direction = new OptionEnum<>(wandStack, ModDataComponents.DIRECTION.get(),"direction", DIRECTION.class, DIRECTION.TARGET);
        replace = new OptionBoolean(wandStack, ModDataComponents.REPLACE.get(),"replace", true);
        match = new OptionEnum<>(wandStack, ModDataComponents.MATCH.get(),"match", MATCH.class, MATCH.SIMILAR);
        random = new OptionBoolean(wandStack, ModDataComponents.RANDOM.get(),"random", false);

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
