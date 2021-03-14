package thetadev.constructionwand.client;

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

import java.util.HashMap;

public class ScreenWand extends Screen
{
    private final ItemStack wand;
    private final WandOptions wandOptions;
    private final HashMap<IOption<?>, Button> optionButtons;

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
        optionButtons = new HashMap<>();
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);
        optionButtons.clear();

        createButton(0, 0, wandOptions.cores);
        createButton(0, 1, wandOptions.lock);
        createButton(0, 2, wandOptions.direction);
        createButton(1, 0, wandOptions.replace);
        createButton(1, 1, wandOptions.match);
        createButton(1, 2, wandOptions.random);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        drawCenteredString(font, wand.getDisplayName().getFormattedText(), width / 2, height / 2 - FIELD_HEIGHT / 2 - SPACING_HEIGHT, 16777215);
        optionButtons.forEach((opt, but) -> drawTooltip(mouseX, mouseY, opt, but));
    }

    @Override
    public boolean charTyped(char character, int code) {
        if(character == 'e') onClose();
        return super.charTyped(character, code);
    }

    private void createButton(int cx, int cy, IOption<?> option) {
        Button button = new Button(getX(cx), getY(cy), BUTTON_WIDTH, BUTTON_HEIGHT, getButtonLabel(option).getFormattedText(), bt -> clickButton(bt, option));
        button.active = option.isEnabled();
        addButton(button);
        optionButtons.put(option, button);
    }

    private void clickButton(Button button, IOption<?> option) {
        option.next();
        ConstructionWand.instance.HANDLER.sendToServer(new PacketWandOption(option, false));
        button.setMessage(getButtonLabel(option).getFormattedText());
    }

    private void drawTooltip(int mouseX, int mouseY, IOption<?> option, Button button) {
        if(button.isHovered()) {
            renderTooltip(new TranslationTextComponent(option.getDescTranslation()).getFormattedText(), mouseX, mouseY);
        }
    }

    private int getX(int n) {
        return width / 2 - FIELD_WIDTH / 2 + n * (BUTTON_WIDTH + SPACING_WIDTH);
    }

    private int getY(int n) {
        return height / 2 - FIELD_HEIGHT / 2 + n * (BUTTON_HEIGHT + SPACING_HEIGHT);
    }

    private ITextComponent getButtonLabel(IOption<?> option) {
        return new TranslationTextComponent(option.getKeyTranslation()).appendSibling(new TranslationTextComponent(option.getValueTranslation()));
    }
}
