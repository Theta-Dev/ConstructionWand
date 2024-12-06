package thetadev.constructionwand.component;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredRegister;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.basics.option.WandOptions;

import java.util.function.Supplier;

public class ModDataComponents
{
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, ConstructionWand.MODID);

    public static final Supplier<DataComponentType<WandOptions.LOCK>> LOCK = DATA_COMPONENT_TYPES.register("lock", () ->
            DataComponentType.<WandOptions.LOCK>builder()
                    .persistent(WandOptions.LOCK.CODEC)
                    .networkSynchronized(WandOptions.LOCK.STREAM_CODEC)
                    .build());

    public static final Supplier<DataComponentType<WandOptions.DIRECTION>> DIRECTION = DATA_COMPONENT_TYPES.register("direction", () ->
            DataComponentType.<WandOptions.DIRECTION>builder()
                    .persistent(WandOptions.DIRECTION.CODEC)
                    .networkSynchronized(WandOptions.DIRECTION.STREAM_CODEC)
                    .build());

    public static final Supplier<DataComponentType<Boolean>> REPLACE = DATA_COMPONENT_TYPES.register("replace", () ->
            DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build());

    public static final Supplier<DataComponentType<WandOptions.MATCH>> MATCH = DATA_COMPONENT_TYPES.register("match", () ->
            DataComponentType.<WandOptions.MATCH>builder()
                    .persistent(WandOptions.MATCH.CODEC)
                    .networkSynchronized(WandOptions.MATCH.STREAM_CODEC)
                    .build());

    public static final Supplier<DataComponentType<Boolean>> RANDOM = DATA_COMPONENT_TYPES.register("random", () ->
            DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build());

    public static final Supplier<DataComponentType<CompoundTag>> CORES = DATA_COMPONENT_TYPES.register("cores", () ->
            DataComponentType.<CompoundTag>builder()
                    .persistent(CompoundTag.CODEC)
                    .networkSynchronized(ByteBufCodecs.COMPOUND_TAG)
                    .build());
}
