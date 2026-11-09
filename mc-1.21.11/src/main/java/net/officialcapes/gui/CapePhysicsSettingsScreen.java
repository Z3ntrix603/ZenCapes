package net.officialcapes.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.officialcapes.config.OfficialCapesConfig;

public class CapePhysicsSettingsScreen extends Screen {
    private final Screen parent;

    private ButtonWidget windBtn;
    private ButtonWidget gravityBtn;
    private ButtonWidget waveBtn;
    private ButtonWidget idleBtn;

    public CapePhysicsSettingsScreen(Screen parent) {
        super(Text.literal("Physik-Einstellungen"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        OfficialCapesConfig config = OfficialCapesConfig.getInstance();
        int centerX = this.width / 2;
        int startY = 48;
        int btnWidth = 200;
        int btnHeight = 20;
        int spacing = 26;

        // 1. Wind strength
        windBtn = ButtonWidget.builder(getWindText(), b -> {
            float[] presets = {0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f};
            config.windStrength = nextPreset(config.windStrength, presets);
            config.save();
            b.setMessage(getWindText());
        }).dimensions(centerX - btnWidth / 2, startY, btnWidth, btnHeight).build();
        this.addDrawableChild(windBtn);

        // 2. Gravity
        gravityBtn = ButtonWidget.builder(getGravityText(), b -> {
            float[] presets = {0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f};
            config.gravity = nextPreset(config.gravity, presets);
            config.save();
            b.setMessage(getGravityText());
        }).dimensions(centerX - btnWidth / 2, startY + spacing, btnWidth, btnHeight).build();
        this.addDrawableChild(gravityBtn);

        // 3. Wave Intensity
        waveBtn = ButtonWidget.builder(getWaveText(), b -> {
            float[] presets = {0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f};
            config.waveIntensity = nextPreset(config.waveIntensity, presets);
            config.save();
            b.setMessage(getWaveText());
        }).dimensions(centerX - btnWidth / 2, startY + spacing * 2, btnWidth, btnHeight).build();
        this.addDrawableChild(waveBtn);

        // 4. Idle Breeze
        idleBtn = ButtonWidget.builder(getIdleText(), b -> {
            config.idleBreeze = !config.idleBreeze;
            config.save();
            b.setMessage(getIdleText());
        }).dimensions(centerX - btnWidth / 2, startY + spacing * 3, btnWidth, btnHeight).build();
        this.addDrawableChild(idleBtn);

        // 5. Reset to Defaults
        ButtonWidget resetBtn = ButtonWidget.builder(Text.literal("Standard wiederherstellen"), b -> {
            config.windStrength = 1.0f;
            config.gravity = 1.0f;
            config.waveIntensity = 1.0f;
            config.idleBreeze = true;
            config.save();
            updateAllButtons();
        }).dimensions(centerX - btnWidth / 2, startY + spacing * 4 + 10, btnWidth, btnHeight).build();
        this.addDrawableChild(resetBtn);

        // 6. Back Button
        ButtonWidget backBtn = ButtonWidget.builder(Text.literal("Zurück"), b -> {
            this.close();
        }).dimensions(centerX - btnWidth / 2, this.height - 32, btnWidth, btnHeight).build();
        this.addDrawableChild(backBtn);
    }

    private float nextPreset(float current, float[] presets) {
        for (float p : presets) {
            if (p > current + 0.05f) {
                return p;
            }
        }
        return presets[0];
    }

    private void updateAllButtons() {
        if (windBtn != null) windBtn.setMessage(getWindText());
        if (gravityBtn != null) gravityBtn.setMessage(getGravityText());
        if (waveBtn != null) waveBtn.setMessage(getWaveText());
        if (idleBtn != null) idleBtn.setMessage(getIdleText());
    }

    private Text getWindText() {
        return Text.literal("Windstärke: §e" + String.format("%.2f", OfficialCapesConfig.getInstance().windStrength) + "x");
    }

    private Text getGravityText() {
        return Text.literal("Schwerkraft: §e" + String.format("%.2f", OfficialCapesConfig.getInstance().gravity) + "x");
    }

    private Text getWaveText() {
        return Text.literal("Wellen-Intensität: §e" + String.format("%.2f", OfficialCapesConfig.getInstance().waveIntensity) + "x");
    }

    private Text getIdleText() {
        boolean idle = OfficialCapesConfig.getInstance().idleBreeze;
        return Text.literal("Wind im Stehen (Idle): " + (idle ? "§aAN" : "§cAUS"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("§l§6Dawn / NoRisk Physik Setup"), this.width / 2, 16, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("§7Passe die Stoff- und Wellensimulation deines Umhangs an"), this.width / 2, 30, 0xAAAAAA);
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(this.parent);
    }
}
