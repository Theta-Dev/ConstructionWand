package thetadev.constructionwand.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thetadev.constructionwand.basics.WandUtil;
import thetadev.constructionwand.basics.option.IOption;
import thetadev.constructionwand.basics.option.WandOptions;
import thetadev.constructionwand.items.wand.ItemWand;

import static thetadev.constructionwand.ConstructionWand.MODID;

public record PacketWandOption(String key, String value, boolean notifyMessage) implements CustomPacketPayload
{
    public static final StreamCodec<FriendlyByteBuf, PacketWandOption> CODEC = CustomPacketPayload.codec(
            PacketWandOption::encode,
            PacketWandOption::new);
    public static final Type<PacketWandOption> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "wand_option"));

    private PacketWandOption(FriendlyByteBuf buffer) {
        this(buffer.readUtf(100), buffer.readUtf(100), buffer.readBoolean());
    }

    public PacketWandOption(IOption<?> option, boolean notify) {
        this(option.getKey(), option.getValueString(), notify);
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(key);
        buffer.writeUtf(value);
        buffer.writeBoolean(notifyMessage);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static class Handler
    {
        public static void handle(final PacketWandOption msg, final IPayloadContext ctx) {
            ctx.enqueueWork(() -> {
                if (ctx.player() instanceof ServerPlayer player) {
                    ItemStack wand = WandUtil.holdingWand(player);
                    if(wand == null) return;
                    WandOptions options = new WandOptions(wand);

                    IOption<?> option = options.get(msg.key);
                    if(option == null) return;
                    option.setValueString(msg.value);

                    if(msg.notifyMessage) ItemWand.optionMessage(player, option);
                    player.getInventory().setChanged();
                }
            })
            .exceptionally(e -> {
                // Handle exception
                ctx.disconnect(Component.translatable("constructionwand.networking.wand_option.failed", e.getMessage()));
                return null;
            });
        }
    }
}
