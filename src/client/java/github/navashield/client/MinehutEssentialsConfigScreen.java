package github.navashield.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class MinehutEssentialsConfigScreen extends Screen {
    private static final int BUTTON_HEIGHT = 20;
    private static final int ROW_GAP = 24;

    private final Screen parent;
    private final List<ToggleEntry> toggleEntries = new ArrayList<>();

    public MinehutEssentialsConfigScreen(Screen parent) {
        super(Text.literal("MinehutEssentials Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        this.toggleEntries.clear();

        List<MinehutEssentialsConfig.FilterDefinition> filters = new ArrayList<>(MinehutEssentialsConfig.getDefinitions());
        int buttonWidth = Math.min(360, this.width - 40);
        int startX = (this.width - buttonWidth) / 2;
        int startY = 40;

        for (int i = 0; i < filters.size(); i++) {
            MinehutEssentialsConfig.FilterDefinition definition = filters.get(i);
            int y = startY + (i * ROW_GAP);

            ButtonWidget toggleButton = ButtonWidget.builder(getButtonText(definition), button -> {
                boolean newValue = !MinehutEssentialsConfig.isEnabled(definition.key());
                MinehutEssentialsConfig.setEnabled(definition.key(), newValue);
                button.setMessage(getButtonText(definition));
            }).dimensions(startX, y, buttonWidth, BUTTON_HEIGHT).build();

            this.addDrawableChild(toggleButton);
            this.toggleEntries.add(new ToggleEntry(toggleButton, definition));
        }

        int doneY = this.height - 28;
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Done"), button -> this.close())
                .dimensions(this.width / 2 - 75, doneY, 150, BUTTON_HEIGHT)
                .build());
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0xA0101010);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 14, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);

        for (ToggleEntry entry : this.toggleEntries) {
            if (entry.button().isHovered()) {
                context.drawTooltip(this.textRenderer, Text.literal(entry.definition().description()), mouseX, mouseY);
                break;
            }
        }
    }

    private static Text getButtonText(MinehutEssentialsConfig.FilterDefinition definition) {
        String state = MinehutEssentialsConfig.isEnabled(definition.key()) ? "ON" : "OFF";
        return Text.literal(definition.label() + ": " + state);
    }

    private record ToggleEntry(ButtonWidget button, MinehutEssentialsConfig.FilterDefinition definition) {
    }
}

