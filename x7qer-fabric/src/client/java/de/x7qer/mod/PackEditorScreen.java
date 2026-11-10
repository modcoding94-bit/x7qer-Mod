package de.x7qer.mod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.util.List;

public final class PackEditorScreen extends Screen {
    private static final List<String> CATEGORIES = List.of("PvP", "Blöcke", "Gear", "HUD", "Misc", "Effekte");
    private int page = 0;
    private String status = "Wähle für jede Kategorie ein Resourcepack.";

    public PackEditorScreen() { this(0); }
    private PackEditorScreen(int page) {
        super(Component.literal("x7qer Style – Pack Editor"));
        this.page = page;
    }

    @Override protected void init() {
        clearWidgets();
        int w = 154, gap = 12, total = 3 * w + 2 * gap, left = (width - total) / 2;
        if (page == 0) {
            for (int i = 0; i < CATEGORIES.size(); i++) {
                final int index = i;
                int col = i % 3, row = i / 3;
                addRenderableWidget(Button.builder(Component.literal(CATEGORIES.get(i) + "  ›"), b -> minecraft.setScreen(new PackEditorScreen(index + 1)))
                    .bounds(left + col * (w + gap), height / 2 - 68 + row * 48, w, 38).build());
            }
            addRenderableWidget(Button.builder(Component.literal("Meine Packs"), b -> { status = "Packs-Ordner: " + Minecraft.getInstance().getResourcePackDirectory(); })
                .bounds(width / 2 - 80, height / 2 + 44, 160, 30).build());
            addRenderableWidget(Button.builder(Component.literal("Pack erstellen"), b -> exportPack())
                .bounds(width / 2 - 80, height / 2 + 82, 160, 32).build());
            addRenderableWidget(Button.builder(Component.literal("Fertig"), b -> onClose())
                .bounds(width / 2 - 80, height - 36, 160, 24).build());
        } else {
            String category = CATEGORIES.get(page - 1);
            List<String> packs = PackBuilder.findPackNames(Minecraft.getInstance().getResourcePackDirectory());
            for (int i = 0; i < Math.min(packs.size(), 8); i++) {
                String pack = packs.get(i);
                addRenderableWidget(Button.builder(Component.literal(pack), b -> {
                    PackBuilder.setChoice(category, pack);
                    status = category + " → " + pack;
                }).bounds(width / 2 - 150, 75 + i * 34, 300, 28).build());
            }
            if (packs.isEmpty()) status = "Keine Packs gefunden. Lege ZIPs oder Ordner in resourcepacks.";
            addRenderableWidget(Button.builder(Component.literal("Zurück"), b -> minecraft.setScreen(new PackEditorScreen()))
                    .bounds(width / 2 - 70, height - 42, 140, 28).build());
        }
    }

    private void exportPack() {
        try { status = "Erstellt: " + PackBuilder.build(Minecraft.getInstance().getResourcePackDirectory()).getFileName(); }
        catch (Exception e) { status = "Fehler beim Erstellen: " + e.getMessage(); }
    }

    @Override public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        renderBackground(g, mouseX, mouseY, delta);
        g.drawCenteredString(font, "x7qer Style", width / 2, 26, 0xFFFFFFFF);
        g.drawCenteredString(font, page == 0 ? "Pack Editor" : "Kategorie: " + CATEGORIES.get(page - 1), width / 2, 48, 0xFFDDDDDD);
        if (page == 0) {
            for (int i = 0; i < CATEGORIES.size(); i++) {
                String selected = PackBuilder.getChoice(CATEGORIES.get(i));
                g.drawCenteredString(font, selected == null ? "(Standard)" : selected, width / 2, height / 2 - 20 + (i / 3) * 48, 0xFFBDBDBD);
            }
        }
        g.drawCenteredString(font, status, width / 2, height - 62, 0xFFFFFFFF);
        super.render(g, mouseX, mouseY, delta);
    }
}
