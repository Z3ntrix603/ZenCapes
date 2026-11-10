package net.officialcapes.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.officialcapes.cape.CapeCategory;
import net.officialcapes.cape.CapeDefinition;
import net.officialcapes.cape.CapeRegistry;
import net.officialcapes.config.OfficialCapesConfig;

import java.util.ArrayList;
import java.util.List;

public class OfficialCapesScreen extends Screen {
    private final Screen parent;
    private CapeCategory currentCategory = CapeCategory.ALL;
    private TextFieldWidget searchBox;
    private List<CapeDefinition> visibleCapes = new ArrayList<>();
    private int scrollOffset = 0;
    private CapeDefinition selectedCape;

    private ButtonWidget toggleVisibilityButton;
    private ButtonWidget togglePhysicsButton;

    public OfficialCapesScreen(Screen parent) {
        super(Text.literal("Offizielle Capes"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        OfficialCapesConfig config = OfficialCapesConfig.getInstance();
        selectedCape = CapeRegistry.get(config.selectedCapeId);
        if (selectedCape == null) {
            selectedCape = CapeRegistry.getDefaultCape();
        }

        // Category tab buttons at top
        CapeCategory[] categories = CapeCategory.values();
        int tabWidth = Math.min(65, (this.width - 20) / categories.length);
        int tabStartX = (this.width - (tabWidth * categories.length)) / 2;
        int tabY = 28;

        for (int i = 0; i < categories.length; i++) {
            CapeCategory cat = categories[i];
            int x = tabStartX + i * tabWidth;
            ButtonWidget btn = ButtonWidget.builder(Text.literal(cat.getDisplayName()), b -> {
                this.currentCategory = cat;
                this.scrollOffset = 0;
                updateFilteredCapes();
            }).dimensions(x, tabY, tabWidth - 2, 18).build();
            this.addDrawableChild(btn);
        }

        // Search field
        int searchWidth = 140;
        int searchX = 16;
        int searchY = 52;
        searchBox = new TextFieldWidget(this.textRenderer, searchX, searchY, searchWidth, 16, Text.literal("Suche..."));
        searchBox.setPlaceholder(Text.literal("§7Cape suchen..."));
        searchBox.setChangedListener(s -> {
            this.scrollOffset = 0;
            updateFilteredCapes();
        });
        this.addDrawableChild(searchBox);

        // Bottom action buttons
        int bottomY = this.height - 28;
        int btnWidth = 100;
        int spacing = 6;
        int totalWidth = btnWidth * 4 + spacing * 3;
        int startX = (this.width - totalWidth) / 2;

        // 1. Cape Visibility Toggle Button
        toggleVisibilityButton = ButtonWidget.builder(getVisibilityText(), b -> {
            config.toggleCape();
            b.setMessage(getVisibilityText());
        }).dimensions(startX, bottomY, btnWidth, 20).build();
        this.addDrawableChild(toggleVisibilityButton);

        // 2. Physics Toggle Button
        togglePhysicsButton = ButtonWidget.builder(getPhysicsText(), b -> {
            config.physicsEnabled = !config.physicsEnabled;
            config.save();
            b.setMessage(getPhysicsText());
        }).dimensions(startX + (btnWidth + spacing), bottomY, btnWidth, 20).build();
        this.addDrawableChild(togglePhysicsButton);

        // 3. Physics Settings Screen
        ButtonWidget settingsBtn = ButtonWidget.builder(Text.literal("Physik Setup..."), b -> {
            MinecraftClient.getInstance().setScreen(new CapePhysicsSettingsScreen(this));
        }).dimensions(startX + (btnWidth + spacing) * 2, bottomY, btnWidth, 20).build();
        this.addDrawableChild(settingsBtn);

        // 4. Done Button
        ButtonWidget doneBtn = ButtonWidget.builder(Text.literal("Fertig"), b -> {
            this.close();
        }).dimensions(startX + (btnWidth + spacing) * 3, bottomY, btnWidth, 20).build();
        this.addDrawableChild(doneBtn);

        updateFilteredCapes();
    }

    private Text getVisibilityText() {
        boolean enabled = OfficialCapesConfig.getInstance().enabled;
        return Text.literal(enabled ? "§aCape: AN" : "§cCape: AUS");
    }

    private Text getPhysicsText() {
        boolean phys = OfficialCapesConfig.getInstance().physicsEnabled;
        return Text.literal(phys ? "§aPhysik: AN" : "§7Physik: AUS");
    }

    private void updateFilteredCapes() {
        visibleCapes.clear();
        String query = searchBox != null ? searchBox.getText().toLowerCase().trim() : "";
        List<CapeDefinition> source = CapeRegistry.getByCategory(currentCategory);

        for (CapeDefinition cape : source) {
            if (query.isEmpty() || cape.getName().toLowerCase().contains(query) || cape.getDescription().toLowerCase().contains(query)) {
                visibleCapes.add(cape);
            }
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int listHeight = visibleCapes.size() * 32;
        int viewHeight = this.height - 110;
        int maxScroll = Math.max(0, listHeight - viewHeight);

        scrollOffset = (int) Math.max(0, Math.min(maxScroll, scrollOffset - verticalAmount * 24));
        return true;
    }

    @Override
    public boolean mouseClicked(Click click, boolean bl) {
        if (super.mouseClicked(click, bl)) {
            return true;
        }

        double mouseX = click.x();
        double mouseY = click.y();
        int listX = 16;
        int listY = 74;
        int listWidth = this.width - 190;
        int listBottom = this.height - 36;

        if (mouseX >= listX && mouseX <= listX + listWidth && mouseY >= listY && mouseY <= listBottom) {
            int relativeY = (int) mouseY - listY + scrollOffset;
            int index = relativeY / 32;
            if (index >= 0 && index < visibleCapes.size()) {
                CapeDefinition clicked = visibleCapes.get(index);
                this.selectedCape = clicked;
                OfficialCapesConfig.getInstance().selectCape(clicked.getId());
                return true;
            }
        }
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        // Header Title
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("§l§6Offizielle Capes & Physik"), this.width / 2, 10, 0xFFFFFF);

        // List Bounds
        int listX = 16;
        int listY = 74;
        int listWidth = this.width - 190;
        int listHeight = this.height - 112;

        // Dark background panel for cape list
        context.fill(listX - 2, listY - 2, listX + listWidth + 2, listY + listHeight + 2, 0x90000000);
        context.drawStrokedRectangle(listX - 2, listY - 2, listWidth + 4, listHeight + 4, 0xFF555555);

        // Render Cape Entries
        context.enableScissor(listX, listY, listX + listWidth, listY + listHeight);

        OfficialCapesConfig config = OfficialCapesConfig.getInstance();
        String activeCapeId = config.selectedCapeId;

        for (int i = 0; i < visibleCapes.size(); i++) {
            CapeDefinition cape = visibleCapes.get(i);
            int itemY = listY + i * 32 - scrollOffset;

            if (itemY + 30 < listY || itemY > listY + listHeight) {
                continue; // culled
            }

            boolean isHovered = mouseX >= listX && mouseX <= listX + listWidth && mouseY >= itemY && mouseY <= itemY + 30;
            boolean isEquipped = cape.getId().equals(activeCapeId);
            boolean isSelected = cape == selectedCape;

            // Row background
            int rowBg = isSelected ? 0x604A6572 : (isHovered ? 0x40FFFFFF : 0x20000000);
            context.fill(listX, itemY, listX + listWidth, itemY + 30, rowBg);
            if (isSelected) {
                context.drawStrokedRectangle(listX, itemY, listWidth, 30, 0xFF00AAFF);
            }

            // Cape mini preview
            int iconX = listX + 4;
            int iconY = itemY + 3;
            context.fill(iconX - 1, iconY - 1, iconX + 16, iconY + 25, 0xFF000000);
            context.drawTexturedQuad(cape.getTexture(), iconX, iconY, iconX + 15, iconY + 24, 1.0f/64.0f, 11.0f/64.0f, 1.0f/32.0f, 17.0f/32.0f);

            // Title and Category Badge
            String badge = getBadgeColor(cape.getCategory()) + "[" + cape.getCategory().getDisplayName() + "]";
            context.drawText(this.textRenderer, Text.literal("§f§l" + cape.getName() + " " + badge), listX + 24, itemY + 5, 0xFFFFFF, true);

            // Lore / Subtitle
            String desc = cape.getDescription();
            if (desc.length() > 52) desc = desc.substring(0, 49) + "...";
            context.drawText(this.textRenderer, Text.literal("§7" + desc), listX + 24, itemY + 17, 0xAAAAAA, false);

            // Status Icon on the right
            if (isEquipped) {
                context.drawText(this.textRenderer, Text.literal("§a§l✓ AUSGERÜSTET"), listX + listWidth - 96, itemY + 10, 0x55FF55, true);
            }
        }

        context.disableScissor();

        // Right Detail Box
        int detailX = this.width - 165;
        int detailY = 74;
        int detailWidth = 150;
        int detailHeight = listHeight;

        context.fill(detailX, detailY, detailX + detailWidth, detailY + detailHeight, 0x90000000);
        context.drawStrokedRectangle(detailX, detailY, detailWidth, detailHeight, 0xFF555555);

        if (selectedCape != null) {
            context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("§e§l" + selectedCape.getName()), detailX + detailWidth / 2, detailY + 10, 0xFFFFFF);

            // Large cape preview
            int bigW = 48;
            int bigH = 76;
            int bigX = detailX + (detailWidth - bigW) / 2;
            int bigY = detailY + 26;

            context.fill(bigX - 2, bigY - 2, bigX + bigW + 2, bigY + bigH + 2, 0xFF111111);
            context.drawStrokedRectangle(bigX - 2, bigY - 2, bigW + 4, bigH + 4, 0xFF777777);
            context.drawTexturedQuad(selectedCape.getTexture(), bigX, bigY, bigX + bigW, bigY + bigH, 1.0f/64.0f, 11.0f/64.0f, 1.0f/32.0f, 17.0f/32.0f);

            // Category
            context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("§bKategorie: §f" + selectedCape.getCategory().getDisplayName()), detailX + detailWidth / 2, bigY + bigH + 8, 0xCCCCCC);

            // Detailed description wrapped
            int descY = bigY + bigH + 22;
            List<String> wrapped = wrapText(selectedCape.getDescription(), 22);
            for (String line : wrapped) {
                if (descY + 10 < detailY + detailHeight) {
                    context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("§7" + line), detailX + detailWidth / 2, descY, 0xAAAAAA);
                    descY += 10;
                }
            }
        }
    }

    private String getBadgeColor(CapeCategory cat) {
        return switch (cat) {
            case MINECON -> "§c";
            case STAFF -> "§4";
            case EVENTS -> "§d";
            case COMMUNITY -> "§b";
            case SPECIAL -> "§6";
            default -> "§7";
        };
    }

    private List<String> wrapText(String text, int maxChars) {
        List<String> result = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder current = new StringBuilder();

        for (String word : words) {
            if (current.length() + word.length() + 1 > maxChars) {
                result.add(current.toString());
                current = new StringBuilder(word);
            } else {
                if (!current.isEmpty()) current.append(" ");
                current.append(word);
            }
        }
        if (!current.isEmpty()) {
            result.add(current.toString());
        }
        return result;
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(this.parent);
    }
}
