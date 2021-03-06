package thetadev.constructionwand.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.basics.option.IOption;
import thetadev.constructionwand.basics.option.WandOptions;
import thetadev.constructionwand.network.PacketWandOption;

import javax.annotation.Nonnull;

public class ScreenWand extends Screen
{
    private final ItemStack wand;
    private final WandOptions wandOptions;

    private static final int BUTTON_WIDTH = 160;
    private static final int BUTTON_HEIGHT = 20;
    private static final int SPACING_WIDTH = 50;
    private static final int SPACING_HEIGHT = 30;
    private static final int N_COLS = 2;
    private static final int N_ROWS = 3;

    private static final int FIELD_WIDTH = N_COLS * (BUTTON_WIDTH + SPACING_WIDTH) - SPACING_WIDTH;
    private static final int FIELD_HEIGHT = N_ROWS * (BUTTON_HEIGHT + SPACING_HEIGHT) - SPACING_HEIGHT;

    public ScreenWand(ItemStack wand) {
        super(new StringTextComponent("ScreenWand"));
        this.wand = wand;
        wandOptions = new WandOptions(wand);
    }

    @Override
    public void init(@Nonnull Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);

        createButton(0, 0, wandOptions.cores);
        createButton(0, 1, wandOptions.lock);
        createButton(0, 2, wandOptions.direction);
        createButton(1, 0, wandOptions.replace);
        createButton(1, 1, wandOptions.match);
        createButton(1, 2, wandOptions.random);
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        drawCenteredString(matrixStack, font, wand.getDisplayName(), width / 2, height / 2 - FIELD_HEIGHT / 2 - SPACING_HEIGHT, 16777215);
    }

    @Override
    public boolean charTyped(char character, int code) {
        if(character == 'e') closeScreen();
        return super.charTyped(character, code);
    }

    private void createButton(int cx, int cy, IOption<?> option) {
        Button button = new Button(getX(cx), getY(cy), BUTTON_WIDTH, BUTTON_HEIGHT, getButtonLabel(option), bt -> clickButton(bt, option), (bt, ms, x, y) -> drawTooltip(ms, x, y, option));
        button.active = option.isEnabled();
        addButton(button);
    }

    private void clickButton(Button button, IOption<?> option) {
        option.next();
        ConstructionWand.instance.HANDLER.sendToServer(new PacketWandOption(option, false));
        button.setMessage(getButtonLabel(option));
    }

    private void drawTooltip(MatrixStack matrixStack, int mouseX, int mouseY, IOption<?> option) {
        if(isMouseOver(mouseX, mouseY)) {
            renderTooltip(matrixStack, new TranslationTextComponent(option.getDescTranslation()), mouseX, mouseY);
        }
    }

    private int getX(int n) {
        return width / 2 - FIELD_WIDTH / 2 + n * (BUTTON_WIDTH + SPACING_WIDTH);
    }

    private int getY(int n) {
        return height / 2 - FIELD_HEIGHT / 2 + n * (BUTTON_HEIGHT + SPACING_HEIGHT);
    }

    private ITextComponent getButtonLabel(IOption<?> option) {
        return new TranslationTextComponent(option.getKeyTranslation()).append(new TranslationTextComponent(option.getValueTranslation()));
    }
}
