package net.officialcapes.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.officialcapes.config.OfficialCapesConfig;

public class CapePhysicsSettingsScreen extends Screen {
    private final Screen parent;

    private Button windBtn;
    private Button gravityBtn;
    private Button waveBtn;
    private Button idleBtn;

    public CapePhysicsSettingsScreen(Screen parent) {
        super(Component.literal("Physik-Einstellungen"));
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

        windBtn = Button.builder(getWindText(), b -> {
            float[] presets = {0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f};
            config.windStrength = nextPreset(config.windStrength, presets);
            config.save();
            b.setMessage(getWindText());
        }).bounds(centerX - btnWidth / 2, startY, btnWidth, btnHeight).build();
        this.addRenderableWidget(windBtn);

        gravityBtn = Button.builder(getGravityText(), b -> {
            float[] presets = {0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f};
            config.gravity = nextPreset(config.gravity, presets);
            config.save();
            b.setMessage(getGravityText());
        }).bounds(centerX - btnWidth / 2, startY + spacing, btnWidth, btnHeight).build();
        this.addRenderableWidget(gravityBtn);

        waveBtn = Button.builder(getWaveText(), b -> {
            float[] presets = {0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f};
            config.waveIntensity = nextPreset(config.waveIntensity, presets);
            config.save();
            b.setMessage(getWaveText());
        }).bounds(centerX - btnWidth / 2, startY + spacing * 2, btnWidth, btnHeight).build();
        this.addRenderableWidget(waveBtn);

        idleBtn = Button.builder(getIdleText(), b -> {
            config.idleBreeze = !config.idleBreeze;
            config.save();
            b.setMessage(getIdleText());
        }).bounds(centerX - btnWidth / 2, startY + spacing * 3, btnWidth, btnHeight).build();
        this.addRenderableWidget(idleBtn);

        Button resetBtn = Button.builder(Component.literal("Standard wiederherstellen"), b -> {
            config.windStrength = 1.0f;
            config.gravity = 1.0f;
            config.waveIntensity = 1.0f;
            config.idleBreeze = true;
            config.save();
            updateAllButtons();
        }).bounds(centerX - btnWidth / 2, startY + spacing * 4 + 10, btnWidth, btnHeight).build();
        this.addRenderableWidget(resetBtn);

        Button backBtn = Button.builder(Component.literal("Zurück"), b -> {
            this.onClose();
        }).bounds(centerX - btnWidth / 2, this.height - 32, btnWidth, btnHeight).build();
        this.addRenderableWidget(backBtn);
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

    private Component getWindText() {
        return Component.literal("Windstärke: §e" + String.format("%.2f", OfficialCapesConfig.getInstance().windStrength) + "x");
    }

    private Component getGravityText() {
        return Component.literal("Schwerkraft: §e" + String.format("%.2f", OfficialCapesConfig.getInstance().gravity) + "x");
    }

    private Component getWaveText() {
        return Component.literal("Wellen-Intensität: §e" + String.format("%.2f", OfficialCapesConfig.getInstance().waveIntensity) + "x");
    }

    private Component getIdleText() {
        boolean idle = OfficialCapesConfig.getInstance().idleBreeze;
        return Component.literal("Wind im Stehen (Idle): " + (idle ? "§aAN" : "§cAUS"));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        super.extractRenderState(extractor, mouseX, mouseY, delta);
        extractor.centeredText(this.font, Component.literal("§l§6Dawn / NoRisk Physik Setup"), this.width / 2, 16, 0xFFFFFF);
        extractor.centeredText(this.font, Component.literal("§7Passe die Stoff- und Wellensimulation deines Umhangs an"), this.width / 2, 30, 0xAAAAAA);
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreenAndShow(this.parent);
    }
}
