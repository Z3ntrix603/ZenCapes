package net.officialcapes.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.officialcapes.cape.CapeCategory;
import net.officialcapes.cape.CapeDefinition;
import net.officialcapes.cape.CapeRegistry;
import net.officialcapes.config.OfficialCapesConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OfficialCapesScreen extends Screen {

    private static final int WIN_WIDTH = 384;
    private static final int WIN_HEIGHT = 224;
    private static final int SIDEBAR_WIDTH = 110;
    private static final int HEADER_HEIGHT = 24;

    private static final int GRID_COLS = 4;
    private static final int GRID_ROWS = 3;
    private static final int SLOTS_PER_PAGE = GRID_COLS * GRID_ROWS; // 12
    private static final int SLOT_W = 34;
    private static final int SLOT_H = 40;
    private static final int SLOT_SPACING = 3;

    // Modern Obsidian Dark UI Colors
    private static final int COLOR_BACKDROP = 0x85000000;
    private static final int COLOR_WIN_BG = 0xF4090E18;
    private static final int COLOR_WIN_HEADER = 0xFF0B1220;
    private static final int COLOR_WIN_BORDER = 0xFF1E293B;

    private static final int COLOR_SIDEBAR_BG = 0xFF080D16;
    private static final int COLOR_SIDEBAR_ITEM = 0xFF0F172A;
    private static final int COLOR_SIDEBAR_SELECTED = 0xFF1E293B;
    private static final int COLOR_CYAN = 0xFF00E5FF;
    private static final int COLOR_ACCENT = 0xFF38BDF8;

    private static final int COLOR_SLOT_BG = 0xFF0D1424;
    private static final int COLOR_SLOT_BORDER = 0xFF1C2840;
    private static final int COLOR_SLOT_HOVER = 0xFF2563EB;
    private static final int COLOR_SLOT_SELECTED = 0xFFFFFFFF;
    private static final int COLOR_TEXT_WHITE = 0xFFFFFFFF;
    private static final int COLOR_TEXT_GRAY = 0xFF94A3B8;
    private static final int COLOR_SUCCESS = 0xFF4ADE80;

    public enum Tab {
        ALL("Alle (36)", CapeCategory.ALL),
        MINECON("Minecon (6)", CapeCategory.MINECON),
        STAFF("Mitarbeiter (4)", CapeCategory.STAFF),
        EVENTS("Events (9)", CapeCategory.EVENTS),
        COMMUNITY("Community (8)", CapeCategory.COMMUNITY),
        SPECIAL("Spezial (9)", CapeCategory.SPECIAL),
        PHYSICS("Physik Setup", null);

        final String displayName;
        final CapeCategory category;

        Tab(String displayName, CapeCategory category) {
            this.displayName = displayName;
            this.category = category;
        }
    }

    private final Screen parent;
    protected Tab activeTab = Tab.ALL;
    private int currentPage = 0;
    private String statusMessage = "Gespeichert. Cape ausgerüstet.";
    private TextFieldWidget searchBox;
    private String searchQuery = "";
    private CapeDefinition selectedCape;
    private CapeDefinition hoveredCape = null;
    private final List<CapeDefinition> filteredCapes = new ArrayList<>();

    public OfficialCapesScreen(Screen parent) {
        super(Text.literal("ZenCapes"));
        this.parent = parent;
    }

    public void setActiveTab(Tab tab) {
        this.activeTab = tab;
    }

    @Override
    protected void init() {
        super.init();
        OfficialCapesConfig config = OfficialCapesConfig.getInstance();
        if (selectedCape == null) {
            selectedCape = CapeRegistry.get(config.selectedCapeId);
            if (selectedCape == null) {
                selectedCape = CapeRegistry.getDefaultCape();
            }
        }
        rebuildUI();
    }

    private void updateFilteredCapes() {
        filteredCapes.clear();
        if (activeTab.category == null) return;

        List<CapeDefinition> source = CapeRegistry.getByCategory(activeTab.category);
        String q = searchQuery.toLowerCase().trim();
        for (CapeDefinition c : source) {
            if (q.isEmpty() || c.getName().toLowerCase().contains(q) || c.getDescription().toLowerCase().contains(q)) {
                filteredCapes.add(c);
            }
        }
    }

    private void rebuildUI() {
        String lastSearch = searchBox != null ? searchBox.getText() : searchQuery;
        this.clearChildren();

        int winX = (this.width - WIN_WIDTH) / 2;
        int winY = (this.height - WIN_HEIGHT) / 2;

        // Close button '✕'
        this.addDrawableChild(ButtonWidget.builder(Text.literal("✕"), b -> close())
                .dimensions(winX + WIN_WIDTH - 18, winY + 4, 14, 14).build());

        int contentX = winX + SIDEBAR_WIDTH + 10;
        int contentY = winY + HEADER_HEIGHT + 4;

        if (activeTab == Tab.PHYSICS) {
            buildPhysicsUI(winX, winY, contentX, contentY);
        } else {
            buildCapesUI(winX, winY, contentX, contentY, lastSearch);
        }
    }

    private void buildCapesUI(int winX, int winY, int contentX, int contentY, String lastSearch) {
        OfficialCapesConfig config = OfficialCapesConfig.getInstance();

        // Search field
        searchBox = new TextFieldWidget(this.textRenderer, contentX, contentY + 1, 80, 14, Text.literal("Suche"));
        searchBox.setPlaceholder(Text.literal("§7Suche..."));
        searchBox.setText(lastSearch);
        searchBox.setChangedListener(text -> {
            this.searchQuery = text;
            this.currentPage = 0;
            updateFilteredCapes();
        });
        this.addDrawableChild(searchBox);
        this.searchQuery = lastSearch;
        updateFilteredCapes();

        // Cape toggle button
        String capeToggleText = "Cape: " + (config.enabled ? "§a[AN]" : "§c[AUS]");
        this.addDrawableChild(ButtonWidget.builder(Text.literal(capeToggleText), b -> {
            config.toggleCape();
            statusMessage = config.enabled ? "Cape aktiviert!" : "Cape ausgeblendet.";
            rebuildUI();
        }).dimensions(contentX + 84, contentY + 1, 62, 14).build());

        // Physics toggle button
        String physToggleText = "Physik: " + (config.physicsEnabled ? "§a[AN]" : "§7[AUS]");
        this.addDrawableChild(ButtonWidget.builder(Text.literal(physToggleText), b -> {
            config.physicsEnabled = !config.physicsEnabled;
            config.save();
            statusMessage = config.physicsEnabled ? "Cloth-Physik aktiv!" : "Physik deaktiviert.";
            rebuildUI();
        }).dimensions(contentX + 148, contentY + 1, 68, 14).build());

        // Pagination buttons
        int totalPages = Math.max(1, (filteredCapes.size() + SLOTS_PER_PAGE - 1) / SLOTS_PER_PAGE);
        if (currentPage >= totalPages) currentPage = totalPages - 1;
        if (currentPage < 0) currentPage = 0;

        int rightAlign = winX + WIN_WIDTH - 8;
        this.addDrawableChild(ButtonWidget.builder(Text.literal("<"), b -> {
            if (currentPage > 0) {
                currentPage--;
                rebuildUI();
            }
        }).dimensions(rightAlign - 24, contentY + 1, 11, 14).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal(">"), b -> {
            if (currentPage < totalPages - 1) {
                currentPage++;
                rebuildUI();
            }
        }).dimensions(rightAlign - 12, contentY + 1, 11, 14).build());

        // Equip / Action Button in Inspector Card
        if (selectedCape != null) {
            int cardX = contentX + 150;
            int cardY = contentY + 19;
            int cardW = 98;
            int cardH = 128;
            boolean isEquipped = selectedCape.getId().equals(config.selectedCapeId);
            Text btnText = Text.literal(isEquipped ? "§a✓ Ausgerüstet" : "§bAusrüsten");

            this.addDrawableChild(ButtonWidget.builder(btnText, b -> {
                config.selectCape(selectedCape.getId());
                config.enabled = true;
                statusMessage = "Gespeichert. " + selectedCape.getName() + " ausgerüstet!";
                rebuildUI();
            }).dimensions(cardX + 8, cardY + cardH - 17, cardW - 16, 14).build());
        }
    }

    private void buildPhysicsUI(int winX, int winY, int contentX, int contentY) {
        OfficialCapesConfig config = OfficialCapesConfig.getInstance();

        // Main physics toggle
        String toggleText = "Physik-Simulation: " + (config.physicsEnabled ? "§a[AKTIV]" : "§c[AUS]");
        this.addDrawableChild(ButtonWidget.builder(Text.literal(toggleText), b -> {
            config.physicsEnabled = !config.physicsEnabled;
            config.save();
            statusMessage = config.physicsEnabled ? "Physik eingeschaltet!" : "Physik ausgeschaltet.";
            rebuildUI();
        }).dimensions(contentX, contentY + 16, 160, 16).build());

        // Wind strength stepper
        this.addDrawableChild(ButtonWidget.builder(Text.literal("§c-"), b -> {
            config.windStrength = Math.max(0.2f, (float) (Math.round((config.windStrength - 0.2f) * 10.0) / 10.0));
            config.save();
            statusMessage = String.format(Locale.ROOT, "Windstärke: %.1fx", config.windStrength);
            rebuildUI();
        }).dimensions(contentX + 118, contentY + 38, 18, 14).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("§a+"), b -> {
            config.windStrength = Math.min(2.5f, (float) (Math.round((config.windStrength + 0.2f) * 10.0) / 10.0));
            config.save();
            statusMessage = String.format(Locale.ROOT, "Windstärke: %.1fx", config.windStrength);
            rebuildUI();
        }).dimensions(contentX + 140, contentY + 38, 18, 14).build());

        // Gravity stepper
        this.addDrawableChild(ButtonWidget.builder(Text.literal("§c-"), b -> {
            config.gravity = Math.max(0.2f, (float) (Math.round((config.gravity - 0.2f) * 10.0) / 10.0));
            config.save();
            statusMessage = String.format(Locale.ROOT, "Schwerkraft: %.1fx", config.gravity);
            rebuildUI();
        }).dimensions(contentX + 118, contentY + 56, 18, 14).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("§a+"), b -> {
            config.gravity = Math.min(2.5f, (float) (Math.round((config.gravity + 0.2f) * 10.0) / 10.0));
            config.save();
            statusMessage = String.format(Locale.ROOT, "Schwerkraft: %.1fx", config.gravity);
            rebuildUI();
        }).dimensions(contentX + 140, contentY + 56, 18, 14).build());

        // Wave intensity stepper
        this.addDrawableChild(ButtonWidget.builder(Text.literal("§c-"), b -> {
            config.waveIntensity = Math.max(0.2f, (float) (Math.round((config.waveIntensity - 0.2f) * 10.0) / 10.0));
            config.save();
            statusMessage = String.format(Locale.ROOT, "Wellen-Dynamik: %.1fx", config.waveIntensity);
            rebuildUI();
        }).dimensions(contentX + 118, contentY + 74, 18, 14).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("§a+"), b -> {
            config.waveIntensity = Math.min(2.5f, (float) (Math.round((config.waveIntensity + 0.2f) * 10.0) / 10.0));
            config.save();
            statusMessage = String.format(Locale.ROOT, "Wellen-Dynamik: %.1fx", config.waveIntensity);
            rebuildUI();
        }).dimensions(contentX + 140, contentY + 74, 18, 14).build());

        // Idle breeze toggle
        String breezeText = "Leichte Brise: " + (config.idleBreeze ? "§a[AN]" : "§cAUS");
        this.addDrawableChild(ButtonWidget.builder(Text.literal(breezeText), b -> {
            config.idleBreeze = !config.idleBreeze;
            config.save();
            statusMessage = config.idleBreeze ? "Breeze-Effekt aktiv." : "Breeze deaktiviert.";
            rebuildUI();
        }).dimensions(contentX, contentY + 94, 160, 16).build());

        // Reset button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("↺ Standard wiederherstellen"), b -> {
            config.windStrength = 1.0f;
            config.gravity = 1.0f;
            config.waveIntensity = 1.0f;
            config.idleBreeze = true;
            config.save();
            statusMessage = "Standardwerte wiederhergestellt.";
            rebuildUI();
        }).dimensions(contentX, contentY + 116, 160, 16).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Dark backdrop
        context.fill(0, 0, this.width, this.height, COLOR_BACKDROP);

        int winX = (this.width - WIN_WIDTH) / 2;
        int winY = (this.height - WIN_HEIGHT) / 2;

        // Rounded outer window frame
        drawRoundedBox(context, winX, winY, WIN_WIDTH, WIN_HEIGHT, COLOR_WIN_BG, COLOR_WIN_BORDER);

        // Header Background
        context.fill(winX + 2, winY + 2, winX + WIN_WIDTH - 2, winY + HEADER_HEIGHT, COLOR_WIN_HEADER);
        context.fill(winX + 2, winY + HEADER_HEIGHT, winX + WIN_WIDTH - 2, winY + HEADER_HEIGHT + 1, COLOR_WIN_BORDER);

        // Header Title
        context.drawText(this.textRenderer, Text.literal("ZenCapes"), winX + 10, winY + 7, COLOR_TEXT_WHITE, true);

        // Sidebar Background
        int sidebarY = winY + HEADER_HEIGHT + 1;
        context.fill(winX + 2, sidebarY, winX + SIDEBAR_WIDTH, winY + WIN_HEIGHT - 2, COLOR_SIDEBAR_BG);
        context.fill(winX + SIDEBAR_WIDTH, sidebarY, winX + SIDEBAR_WIDTH + 1, winY + WIN_HEIGHT - 2, COLOR_WIN_BORDER);

        // Sidebar Category Tabs
        Tab[] tabs = Tab.values();
        int tabH = 20;
        int tabSpacing = 4;
        for (int i = 0; i < tabs.length; i++) {
            Tab t = tabs[i];
            int ty = sidebarY + 4 + i * (tabH + tabSpacing);
            boolean isSel = (t == activeTab);
            boolean isHov = mouseX >= winX + 4 && mouseX < winX + SIDEBAR_WIDTH - 4 && mouseY >= ty && mouseY < ty + tabH;

            int bg = isSel ? COLOR_SIDEBAR_SELECTED : (isHov ? COLOR_SIDEBAR_ITEM : COLOR_SIDEBAR_BG);
            drawRoundedBox(context, winX + 4, ty, SIDEBAR_WIDTH - 8, tabH, bg, isSel ? COLOR_ACCENT : COLOR_WIN_BORDER);

            int textColor = isSel ? COLOR_TEXT_WHITE : (isHov ? COLOR_CYAN : COLOR_TEXT_GRAY);
            context.drawText(this.textRenderer, Text.literal(t.displayName), winX + 8, ty + 6, textColor, isSel);
        }

        int contentX = winX + SIDEBAR_WIDTH + 10;
        int contentY = winY + HEADER_HEIGHT + 4;

        if (activeTab == Tab.PHYSICS) {
            renderPhysicsContent(context, contentX, contentY);
        } else {
            renderCapesContent(context, mouseX, mouseY, winX, winY, contentX, contentY);
        }

        // Status message
        context.drawText(this.textRenderer, Text.literal(statusMessage), contentX, winY + WIN_HEIGHT - 14, COLOR_SUCCESS, true);

        // Hotkey hint
        context.drawText(this.textRenderer, Text.literal("§7[C] Menü | [V] Cape"), winX + WIN_WIDTH - 110, winY + WIN_HEIGHT - 14, COLOR_TEXT_GRAY, false);

        super.render(context, mouseX, mouseY, delta);

        // Tooltip rendered above widgets
        if (hoveredCape != null && activeTab != Tab.PHYSICS) {
            String tip = hoveredCape.getName() + " §7[" + hoveredCape.getCategory().getDisplayName() + "]";
            int tw = this.textRenderer.getWidth(tip) + 8;
            int tx = Math.min(mouseX + 8, this.width - tw - 4);
            int ty = mouseY - 14;
            drawRoundedBox(context, tx - 2, ty - 2, tw + 4, 15, 0xF2090E18, COLOR_CYAN);
            context.drawText(this.textRenderer, Text.literal(tip), tx + 2, ty + 2, COLOR_TEXT_WHITE, true);
        }
    }

    private void renderCapesContent(DrawContext context, int mouseX, int mouseY, int winX, int winY, int contentX, int contentY) {
        OfficialCapesConfig config = OfficialCapesConfig.getInstance();
        int totalPages = Math.max(1, (filteredCapes.size() + SLOTS_PER_PAGE - 1) / SLOTS_PER_PAGE);
        String pageStr = (currentPage + 1) + " / " + totalPages;
        int pStrWidth = this.textRenderer.getWidth(pageStr);
        int rightAlign = winX + WIN_WIDTH - 8;
        context.drawText(this.textRenderer, Text.literal(pageStr), rightAlign - 28 - pStrWidth, contentY + 4, COLOR_TEXT_GRAY, true);

        int gridStartY = contentY + 19;
        int startIndex = currentPage * SLOTS_PER_PAGE;
        int endIndex = Math.min(filteredCapes.size(), startIndex + SLOTS_PER_PAGE);

        hoveredCape = null;

        for (int i = startIndex; i < endIndex; i++) {
            CapeDefinition cape = filteredCapes.get(i);
            int slotIdx = i - startIndex;
            int col = slotIdx % GRID_COLS;
            int row = slotIdx / GRID_COLS;

            int sx = contentX + col * (SLOT_W + SLOT_SPACING);
            int sy = gridStartY + row * (SLOT_H + SLOT_SPACING);

            boolean isHov = mouseX >= sx && mouseX < sx + SLOT_W && mouseY >= sy && mouseY < sy + SLOT_H;
            boolean isEquipped = cape.getId().equals(config.selectedCapeId);
            boolean isSelected = (cape == selectedCape);

            if (isHov) hoveredCape = cape;

            int borderColor = isEquipped ? COLOR_SLOT_SELECTED : (isSelected ? COLOR_CYAN : (isHov ? COLOR_SLOT_HOVER : COLOR_SLOT_BORDER));
            drawRoundedBox(context, sx, sy, SLOT_W, SLOT_H, COLOR_SLOT_BG, borderColor);

            if (isEquipped) {
                // Double border
                context.fill(sx + 2, sy + 2, sx + SLOT_W - 2, sy + 3, COLOR_SLOT_SELECTED);
                context.fill(sx + 2, sy + SLOT_H - 3, sx + SLOT_W - 2, sy + SLOT_H - 2, COLOR_SLOT_SELECTED);
                context.fill(sx + 2, sy + 3, sx + 3, sy + SLOT_H - 3, COLOR_SLOT_SELECTED);
                context.fill(sx + SLOT_W - 3, sy + 3, sx + SLOT_W - 2, sy + SLOT_H - 3, COLOR_SLOT_SELECTED);
            }

            // Mini cape preview (16x26)
            int mx = sx + (SLOT_W - 16) / 2;
            int my = sy + (SLOT_H - 26) / 2;
            context.drawTexturedQuad(cape.getTexture(), mx, my, mx + 16, my + 26, 1.0f / 64.0f, 11.0f / 64.0f, 1.0f / 32.0f, 17.0f / 32.0f);

            if (isEquipped) {
                context.drawText(this.textRenderer, Text.literal("✓"), sx + SLOT_W - 8, sy + 2, COLOR_SUCCESS, true);
            }
        }

        // Right Preview Inspector Card
        int cardX = contentX + 150;
        int cardY = contentY + 19;
        int cardW = 98;
        int cardH = 128;
        drawRoundedBox(context, cardX, cardY, cardW, cardH, COLOR_SLOT_BG, COLOR_WIN_BORDER);
        context.fill(cardX + 1, cardY + 1, cardX + cardW - 1, cardY + 14, COLOR_WIN_HEADER);
        context.fill(cardX + 1, cardY + 14, cardX + cardW - 1, cardY + 15, COLOR_WIN_BORDER);
        context.drawText(this.textRenderer, Text.literal("CAPE PREVIEW"), cardX + 8, cardY + 4, COLOR_ACCENT, true);

        if (selectedCape != null) {
            int bigW = 32;
            int bigH = 50;
            int bigX = cardX + (cardW - bigW) / 2;
            int bigY = cardY + 20;

            drawRoundedBox(context, bigX - 2, bigY - 2, bigW + 4, bigH + 4, 0xFF050810, COLOR_WIN_BORDER);
            context.drawTexturedQuad(selectedCape.getTexture(), bigX, bigY, bigX + bigW, bigY + bigH, 1.0f / 64.0f, 11.0f / 64.0f, 1.0f / 32.0f, 17.0f / 32.0f);

            String name = truncate(selectedCape.getName(), cardW - 14);
            context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(name), cardX + cardW / 2, bigY + 54, COLOR_TEXT_WHITE);

            String badge = "[" + selectedCape.getCategory().getDisplayName() + "]";
            context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(badge), cardX + cardW / 2, bigY + 66, COLOR_CYAN);
        }
    }

    private void renderPhysicsContent(DrawContext context, int contentX, int contentY) {
        OfficialCapesConfig config = OfficialCapesConfig.getInstance();

        context.drawText(this.textRenderer, Text.literal("Cape-Physik & Tuchsimulation"), contentX, contentY + 2, COLOR_TEXT_WHITE, true);

        // Labels for steppers
        context.drawText(this.textRenderer, Text.literal(String.format(Locale.ROOT, "Windstärke: %.1fx", config.windStrength)), contentX, contentY + 41, COLOR_TEXT_WHITE, false);
        context.drawText(this.textRenderer, Text.literal(String.format(Locale.ROOT, "Schwerkraft: %.1fx", config.gravity)), contentX, contentY + 59, COLOR_TEXT_WHITE, false);
        context.drawText(this.textRenderer, Text.literal(String.format(Locale.ROOT, "Wellen-Dynamik: %.1fx", config.waveIntensity)), contentX, contentY + 77, COLOR_TEXT_WHITE, false);

        // Right info card
        int cardX = contentX + 168;
        int cardY = contentY + 16;
        int cardW = 80;
        int cardH = 116;
        drawRoundedBox(context, cardX, cardY, cardW, cardH, COLOR_SLOT_BG, COLOR_WIN_BORDER);
        context.fill(cardX + 1, cardY + 1, cardX + cardW - 1, cardY + 14, COLOR_WIN_HEADER);
        context.fill(cardX + 1, cardY + 14, cardX + cardW - 1, cardY + 15, COLOR_WIN_BORDER);
        context.drawText(this.textRenderer, Text.literal("SIMULATION"), cardX + 6, cardY + 4, COLOR_ACCENT, true);

        int iy = cardY + 20;
        context.drawText(this.textRenderer, Text.literal("§a✓ §716 Bounded"), cardX + 6, iy, COLOR_TEXT_WHITE, false);
        context.drawText(this.textRenderer, Text.literal("§a✓ §7Smooth Wind"), cardX + 6, iy + 14, COLOR_TEXT_WHITE, false);
        context.drawText(this.textRenderer, Text.literal("§a✓ §7Cloth Waves"), cardX + 6, iy + 28, COLOR_TEXT_WHITE, false);
        context.drawText(this.textRenderer, Text.literal("§a✓ §7No Clipping"), cardX + 6, iy + 42, COLOR_TEXT_WHITE, false);
        context.drawText(this.textRenderer, Text.literal("§a✓ §7Client-Side"), cardX + 6, iy + 56, COLOR_TEXT_WHITE, false);
    }

    private String truncate(String text, int maxWidth) {
        if (this.textRenderer.getWidth(text) <= maxWidth) return text;
        while (!text.isEmpty() && this.textRenderer.getWidth(text + "..") > maxWidth) {
            text = text.substring(0, text.length() - 1);
        }
        return text + "..";
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (activeTab != Tab.PHYSICS) {
            int totalPages = Math.max(1, (filteredCapes.size() + SLOTS_PER_PAGE - 1) / SLOTS_PER_PAGE);
            if (verticalAmount > 0 && currentPage > 0) {
                currentPage--;
                rebuildUI();
                return true;
            } else if (verticalAmount < 0 && currentPage < totalPages - 1) {
                currentPage++;
                rebuildUI();
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean mouseClicked(Click click, boolean bl) {
        double mouseX = click.x();
        double mouseY = click.y();

        int winX = (this.width - WIN_WIDTH) / 2;
        int winY = (this.height - WIN_HEIGHT) / 2;

        // Sidebar click
        int sidebarY = winY + HEADER_HEIGHT + 1;
        if (mouseX >= winX + 4 && mouseX < winX + SIDEBAR_WIDTH - 4) {
            Tab[] tabs = Tab.values();
            int tabH = 20;
            int tabSpacing = 4;
            for (int i = 0; i < tabs.length; i++) {
                int ty = sidebarY + 4 + i * (tabH + tabSpacing);
                if (mouseY >= ty && mouseY < ty + tabH) {
                    if (activeTab != tabs[i]) {
                        activeTab = tabs[i];
                        currentPage = 0;
                        if (activeTab.category != null) {
                            updateFilteredCapes();
                        }
                        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.ui(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                        rebuildUI();
                    }
                    return true;
                }
            }
        }

        // Cape grid slot click
        if (activeTab != Tab.PHYSICS) {
            int contentX = winX + SIDEBAR_WIDTH + 10;
            int contentY = winY + HEADER_HEIGHT + 4;
            int gridStartY = contentY + 19;
            int startIndex = currentPage * SLOTS_PER_PAGE;
            int endIndex = Math.min(filteredCapes.size(), startIndex + SLOTS_PER_PAGE);

            for (int i = startIndex; i < endIndex; i++) {
                int slotIdx = i - startIndex;
                int col = slotIdx % GRID_COLS;
                int row = slotIdx / GRID_COLS;

                int sx = contentX + col * (SLOT_W + SLOT_SPACING);
                int sy = gridStartY + row * (SLOT_H + SLOT_SPACING);

                if (mouseX >= sx && mouseX < sx + SLOT_W && mouseY >= sy && mouseY < sy + SLOT_H) {
                    CapeDefinition cape = filteredCapes.get(i);
                    selectedCape = cape;
                    OfficialCapesConfig.getInstance().selectCape(cape.getId());
                    OfficialCapesConfig.getInstance().enabled = true;
                    statusMessage = "Gespeichert. " + cape.getName() + " ausgerüstet!";
                    MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.ui(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                    rebuildUI();
                    return true;
                }
            }
        }

        return super.mouseClicked(click, bl);
    }

    private void drawRoundedBox(DrawContext context, int x, int y, int w, int h, int bg, int border) {
        context.fill(x + 1, y, x + w - 1, y + h, bg);
        context.fill(x, y + 1, x + 1, y + h - 1, bg);
        context.fill(x + w - 1, y + 1, x + w, y + h - 1, bg);

        context.fill(x + 1, y, x + w - 1, y + 1, border);
        context.fill(x + 1, y + h - 1, x + w - 1, y + h, border);
        context.fill(x, y + 1, x + 1, y + h - 1, border);
        context.fill(x + w - 1, y + 1, x + w, y + h - 1, border);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(this.parent);
    }
}
