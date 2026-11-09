package net.officialcapes.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.officialcapes.cape.CapeCategory;
import net.officialcapes.cape.CapeDefinition;
import net.officialcapes.cape.CapeRegistry;
import net.officialcapes.config.OfficialCapesConfig;

import java.util.ArrayList;
import java.util.List;

public class OfficialCapesScreen extends Screen {
    private final Screen parent;
    private CapeCategory currentCategory = CapeCategory.ALL;
    private EditBox searchBox;
    private List<CapeDefinition> visibleCapes = new ArrayList<>();
    private int scrollOffset = 0;
    private CapeDefinition selectedCape;

    private Button toggleVisibilityButton;
    private Button togglePhysicsButton;

    public OfficialCapesScreen(Screen parent) {
        super(Component.literal("Offizielle Capes"));
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

        CapeCategory[] categories = CapeCategory.values();
        int tabWidth = Math.min(65, (this.width - 20) / categories.length);
        int tabStartX = (this.width - (tabWidth * categories.length)) / 2;
        int tabY = 28;

        for (int i = 0; i < categories.length; i++) {
            CapeCategory cat = categories[i];
            int x = tabStartX + i * tabWidth;
            Button btn = Button.builder(Component.literal(cat.getDisplayName()), b -> {
                this.currentCategory = cat;
                this.scrollOffset = 0;
                updateFilteredCapes();
            }).bounds(x, tabY, tabWidth - 2, 18).build();
            this.addRenderableWidget(btn);
        }

        int searchWidth = 140;
        int searchX = 16;
        int searchY = 52;
        searchBox = new EditBox(this.font, searchX, searchY, searchWidth, 16, Component.literal("Suche..."));
        searchBox.setResponder(s -> {
            this.scrollOffset = 0;
            updateFilteredCapes();
        });
        this.addRenderableWidget(searchBox);

        int bottomY = this.height - 28;
        int btnWidth = 100;
        int spacing = 6;
        int totalWidth = btnWidth * 4 + spacing * 3;
        int startX = (this.width - totalWidth) / 2;

        toggleVisibilityButton = Button.builder(getVisibilityText(), b -> {
            config.toggleCape();
            b.setMessage(getVisibilityText());
        }).bounds(startX, bottomY, btnWidth, 20).build();
        this.addRenderableWidget(toggleVisibilityButton);

        togglePhysicsButton = Button.builder(getPhysicsText(), b -> {
            config.physicsEnabled = !config.physicsEnabled;
            config.save();
            b.setMessage(getPhysicsText());
        }).bounds(startX + (btnWidth + spacing), bottomY, btnWidth, 20).build();
        this.addRenderableWidget(togglePhysicsButton);

        Button settingsBtn = Button.builder(Component.literal("Physik Setup..."), b -> {
            Minecraft.getInstance().setScreenAndShow(new CapePhysicsSettingsScreen(this));
        }).bounds(startX + (btnWidth + spacing) * 2, bottomY, btnWidth, 20).build();
        this.addRenderableWidget(settingsBtn);

        Button doneBtn = Button.builder(Component.literal("Fertig"), b -> {
            this.onClose();
        }).bounds(startX + (btnWidth + spacing) * 3, bottomY, btnWidth, 20).build();
        this.addRenderableWidget(doneBtn);

        updateFilteredCapes();
    }

    private Component getVisibilityText() {
        boolean enabled = OfficialCapesConfig.getInstance().enabled;
        return Component.literal(enabled ? "§aCape: AN" : "§cCape: AUS");
    }

    private Component getPhysicsText() {
        boolean phys = OfficialCapesConfig.getInstance().physicsEnabled;
        return Component.literal(phys ? "§aPhysik: AN" : "§7Physik: AUS");
    }

    private void updateFilteredCapes() {
        visibleCapes.clear();
        String query = searchBox != null ? searchBox.getValue().toLowerCase().trim() : "";
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
    public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        if (super.mouseClicked(event, bl)) {
            return true;
        }

        double mouseX = event.x();
        double mouseY = event.y();
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
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        super.extractRenderState(extractor, mouseX, mouseY, delta);

        extractor.centeredText(this.font, Component.literal("§l§6Offizielle Capes & Physik"), this.width / 2, 10, 0xFFFFFF);

        int listX = 16;
        int listY = 74;
        int listWidth = this.width - 190;
        int listHeight = this.height - 112;

        extractor.fill(listX - 2, listY - 2, listX + listWidth + 2, listY + listHeight + 2, 0x90000000);
        extractor.outline(listX - 2, listY - 2, listWidth + 4, listHeight + 4, 0xFF555555);

        extractor.enableScissor(listX, listY, listX + listWidth, listY + listHeight);

        OfficialCapesConfig config = OfficialCapesConfig.getInstance();
        String activeCapeId = config.selectedCapeId;

        for (int i = 0; i < visibleCapes.size(); i++) {
            CapeDefinition cape = visibleCapes.get(i);
            int itemY = listY + i * 32 - scrollOffset;

            if (itemY + 30 < listY || itemY > listY + listHeight) {
                continue;
            }

            boolean isHovered = mouseX >= listX && mouseX <= listX + listWidth && mouseY >= itemY && mouseY <= itemY + 30;
            boolean isEquipped = cape.getId().equals(activeCapeId);
            boolean isSelected = cape == selectedCape;

            int rowBg = isSelected ? 0x604A6572 : (isHovered ? 0x40FFFFFF : 0x20000000);
            extractor.fill(listX, itemY, listX + listWidth, itemY + 30, rowBg);
            if (isSelected) {
                extractor.outline(listX, itemY, listWidth, 30, 0xFF00AAFF);
            }

            int iconX = listX + 4;
            int iconY = itemY + 3;
            extractor.fill(iconX - 1, iconY - 1, iconX + 16, iconY + 25, 0xFF000000);
            extractor.blit(cape.getTexture(), iconX, iconX + 15, iconY, iconY + 24, 1.0f/64.0f, 11.0f/64.0f, 1.0f/32.0f, 17.0f/32.0f);

            String badge = getBadgeColor(cape.getCategory()) + "[" + cape.getCategory().getDisplayName() + "]";
            extractor.text(this.font, Component.literal("§f§l" + cape.getName() + " " + badge), listX + 24, itemY + 5, 0xFFFFFF, true);

            String desc = cape.getDescription();
            if (desc.length() > 52) desc = desc.substring(0, 49) + "...";
            extractor.text(this.font, Component.literal("§7" + desc), listX + 24, itemY + 17, 0xAAAAAA, false);

            if (isEquipped) {
                extractor.text(this.font, Component.literal("§a§l✓ AUSGERÜSTET"), listX + listWidth - 96, itemY + 10, 0x55FF55, true);
            }
        }

        extractor.disableScissor();

        int detailX = this.width - 165;
        int detailY = 74;
        int detailWidth = 150;
        int detailHeight = listHeight;

        extractor.fill(detailX, detailY, detailX + detailWidth, detailY + detailHeight, 0x90000000);
        extractor.outline(detailX, detailY, detailWidth, detailHeight, 0xFF555555);

        if (selectedCape != null) {
            extractor.centeredText(this.font, Component.literal("§e§l" + selectedCape.getName()), detailX + detailWidth / 2, detailY + 10, 0xFFFFFF);

            int bigW = 48;
            int bigH = 76;
            int bigX = detailX + (detailWidth - bigW) / 2;
            int bigY = detailY + 26;

            extractor.fill(bigX - 2, bigY - 2, bigX + bigW + 2, bigY + bigH + 2, 0xFF111111);
            extractor.outline(bigX - 2, bigY - 2, bigW + 4, bigH + 4, 0xFF777777);
            extractor.blit(selectedCape.getTexture(), bigX, bigX + bigW, bigY, bigY + bigH, 1.0f/64.0f, 11.0f/64.0f, 1.0f/32.0f, 17.0f/32.0f);

            extractor.centeredText(this.font, Component.literal("§bKategorie: §f" + selectedCape.getCategory().getDisplayName()), detailX + detailWidth / 2, bigY + bigH + 8, 0xCCCCCC);

            int descY = bigY + bigH + 22;
            List<String> wrapped = wrapText(selectedCape.getDescription(), 22);
            for (String line : wrapped) {
                if (descY + 10 < detailY + detailHeight) {
                    extractor.centeredText(this.font, Component.literal("§7" + line), detailX + detailWidth / 2, descY, 0xAAAAAA);
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
    public void onClose() {
        Minecraft.getInstance().setScreenAndShow(this.parent);
    }
}
