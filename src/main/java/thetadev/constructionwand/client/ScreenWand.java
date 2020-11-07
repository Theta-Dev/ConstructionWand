package thetadev.constructionwand.client;

import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.basics.option.IOption;
import thetadev.constructionwand.basics.option.WandOptions;
import thetadev.constructionwand.network.PacketWandOption;

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

	private static final int FIELD_WIDTH = N_COLS * (BUTTON_WIDTH+SPACING_WIDTH) - SPACING_WIDTH;
	private static final int FIELD_HEIGHT = N_ROWS * (BUTTON_HEIGHT+SPACING_HEIGHT) - SPACING_HEIGHT;

	public ScreenWand(ItemStack wand) {
		super(new LiteralText("ScreenWand"));
		this.wand = wand;
		wandOptions = new WandOptions(wand);
	}

	@Override
	public void init(MinecraftClient minecraft, int width, int height) {
		super.init(minecraft, width, height);

		createButton(0, 0, wandOptions.mode);
		createButton(0, 1, wandOptions.lock);
		createButton(0, 2, wandOptions.direction);
		createButton(1, 0, wandOptions.replace);
		createButton(1, 1, wandOptions.match);
		createButton(1, 2, wandOptions.random);
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		drawCenteredText(matrixStack, textRenderer, wand.getName(), width/2, height/2 - FIELD_HEIGHT/2 - SPACING_HEIGHT, 16777215);
	}

	@Override
	public boolean charTyped(char character, int code) {
		if(character == 'e') onClose();
		return super.charTyped(character, code);
	}

	private void createButton(int cx, int cy, IOption<?> option) {
		ButtonWidget button = new ButtonWidget(getX(cx), getY(cy), BUTTON_WIDTH, BUTTON_HEIGHT, getButtonLabel(option), bt -> clickButton(bt, option), (bt, ms, x, y) -> drawTooltip(ms, x, y, option));
		button.active = option.isEnabled();
		addButton(button);
	}

	private void clickButton(ButtonWidget button, IOption<?> option) {
		option.next();
		ClientSidePacketRegistry.INSTANCE.sendToServer(PacketWandOption.ID, new PacketWandOption(option, false).encode());
		button.setMessage(getButtonLabel(option));
	}

	private void drawTooltip(MatrixStack matrixStack, int mouseX, int mouseY, IOption<?> option) {
		if(isMouseOver(mouseX, mouseY)) {
			renderTooltip(matrixStack, new TranslatableText(option.getDescTranslation()), mouseX, mouseY);
		}
	}

	private int getX(int n) {
		return width/2 - FIELD_WIDTH/2 + n*(BUTTON_WIDTH+SPACING_WIDTH);
	}

	private int getY(int n) {
		return height/2 - FIELD_HEIGHT/2 + n*(BUTTON_HEIGHT+SPACING_HEIGHT);
	}

	private Text getButtonLabel(IOption<?> option) {
		return new TranslatableText(option.getKeyTranslation()).append(new TranslatableText(option.getValueTranslation()));
	}
}
