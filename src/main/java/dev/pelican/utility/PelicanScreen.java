package dev.pelican.utility;

import net.minecraft.block.Block;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public final class PelicanScreen extends Screen {
    private static final int BLACK = 0xEE08080A;
    private static final int PANEL = 0xFF151519;
    private static final int WHITE = 0xFFF1F1F4;
    private static final int PURPLE = 0xFFD0A6FF;
    private int left, top, width = 310, height = 300;
    private int page = 0;
    private int scroll = 0;
    private final List<Block> filterBlocks = new ArrayList<>();

    public PelicanScreen() { super(Text.literal("PELICAN UTILITY")); filterBlocks.addAll(PelicanClient.STORAGE_BLOCKS); }
    @Override protected void init() { left = (this.width - width) / 2; top = (this.height - height) / 2; }

    @Override public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx, mouseX, mouseY, delta);
        ctx.fill(left, top, left + width, top + height, BLACK);
        ctx.fill(left, top, left + width, top + 28, PANEL);
        ctx.fill(left, top, left + 4, top + height, PURPLE);
        ctx.drawText(textRenderer, "PELICAN", left + 14, top + 10, WHITE, false);
        ctx.drawText(textRenderer, "UTILITY CLIENT  /  1.21.5", left + 112, top + 10, PURPLE, false);
        tab(ctx, left + 12, top + 40, "MODULES", page == 0);
        tab(ctx, left + 98, top + 40, "FILTERS", page == 1);
        tab(ctx, left + 184, top + 40, "RENDER", page == 2);
        if (page == 0) renderModules(ctx);
        else if (page == 1) renderFilters(ctx, mouseX, mouseY);
        else renderRenderSettings(ctx);
        ctx.drawText(textRenderer, "RMB: toggle panel   •   ESC: close", left + 12, top + height - 18, 0xFF9999A5, false);
    }

    private void tab(DrawContext ctx, int x, int y, String label, boolean active) {
        ctx.fill(x, y, x + 78, y + 22, active ? 0xFF3B2C4B : PANEL);
        ctx.drawCenteredTextWithShadow(textRenderer, label, x + 39, y + 7, active ? PURPLE : WHITE);
    }
    private void module(DrawContext ctx, int y, String name, String desc, boolean enabled) {
        ctx.fill(left + 12, y, left + width - 12, y + 52, PANEL);
        ctx.drawText(textRenderer, name, left + 22, y + 8, WHITE, false);
        ctx.drawText(textRenderer, desc, left + 22, y + 24, 0xFF9999A5, false);
        ctx.fill(left + width - 66, y + 14, left + width - 24, y + 38, enabled ? 0xFFB889EE : 0xFF050506);
        ctx.drawCenteredTextWithShadow(textRenderer, enabled ? "ON" : "OFF", left + width - 45, y + 22, enabled ? 0xFF17121D : WHITE);
    }
    private void renderModules(DrawContext ctx) {
        module(ctx, top + 72, "SpawnerESP", "Mob spawners through walls", PelicanClient.spawnerEsp);
        module(ctx, top + 132, "StorageESP", "Chests, barrels, shulkers...", PelicanClient.storageEsp);
        ctx.drawText(textRenderer, "Detected blocks are outlined in light purple.", left + 14, top + 205, 0xFFB6B6C0, false);
        ctx.drawText(textRenderer, "Filters and visibility are configurable in tabs.", left + 14, top + 222, 0xFFB6B6C0, false);
    }
    private void renderRenderSettings(DrawContext ctx) {
        ctx.drawText(textRenderer, "VISIBILITY", left + 16, top + 78, PURPLE, false);
        ctx.drawText(textRenderer, "Through walls", left + 22, top + 108, WHITE, false);
        ctx.fill(left + width - 66, top + 101, left + width - 24, top + 125, PelicanClient.throughWalls ? 0xFFB889EE : 0xFF050506);
        ctx.drawCenteredTextWithShadow(textRenderer, PelicanClient.throughWalls ? "ON" : "OFF", left + width - 45, top + 109, PelicanClient.throughWalls ? 0xFF17121D : WHITE);
        ctx.drawText(textRenderer, "Range: 96 blocks", left + 22, top + 150, 0xFFB6B6C0, false);
        ctx.drawText(textRenderer, "Color: light purple", left + 22, top + 170, 0xFFB6B6C0, false);
    }
    private void renderFilters(DrawContext ctx, int mouseX, int mouseY) {
        ctx.drawText(textRenderer, "STORAGE BLOCK FILTERS", left + 14, top + 72, PURPLE, false);
        int rowY = top + 94;
        for (int i = scroll; i < Math.min(filterBlocks.size(), scroll + 8); i++) {
            Block block = filterBlocks.get(i);
            String label = Registries.BLOCK.getId(block).getPath().replace('_', ' ');
            boolean selected = PelicanClient.STORAGE_BLOCKS.contains(block);
            ctx.fill(left + 12, rowY, left + width - 12, rowY + 21, PANEL);
            ctx.drawText(textRenderer, label, left + 20, rowY + 7, WHITE, false);
            ctx.fill(left + width - 43, rowY + 4, left + width - 23, rowY + 18, selected ? 0xFFB889EE : 0xFF050506);
            ctx.drawCenteredTextWithShadow(textRenderer, selected ? "✓" : "", left + width - 33, rowY + 7, 0xFF17121D);
            rowY += 23;
        }
        ctx.drawText(textRenderer, "Click a row to include/exclude it", left + 14, top + height - 38, 0xFFB6B6C0, false);
    }

    @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);
        if (mouseY >= top + 40 && mouseY <= top + 62) {
            if (mouseX >= left + 12 && mouseX < left + 90) page = 0;
            else if (mouseX >= left + 98 && mouseX < left + 176) page = 1;
            else if (mouseX >= left + 184 && mouseX < left + 262) page = 2;
            return true;
        }
        if (page == 0) {
            if (mouseY >= top + 72 && mouseY < top + 124) PelicanClient.spawnerEsp = !PelicanClient.spawnerEsp;
            else if (mouseY >= top + 132 && mouseY < top + 184) PelicanClient.storageEsp = !PelicanClient.storageEsp;
            return true;
        }
        if (page == 2 && mouseY >= top + 98 && mouseY <= top + 128) { PelicanClient.throughWalls = !PelicanClient.throughWalls; return true; }
        if (page == 1 && mouseY >= top + 94 && mouseY < top + 94 + 8 * 23) {
            int index = scroll + (int)((mouseY - (top + 94)) / 23);
            if (index >= 0 && index < filterBlocks.size()) {
                Block b = filterBlocks.get(index);
                if (PelicanClient.STORAGE_BLOCKS.contains(b)) PelicanClient.STORAGE_BLOCKS.remove(b);
                else PelicanClient.STORAGE_BLOCKS.add(b);
            }
            return true;
        }
        return true;
    }
    @Override public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (page == 1) scroll = Math.max(0, Math.min(filterBlocks.size() - 1, scroll + (verticalAmount < 0 ? 1 : -1)));
        return true;
    }
    @Override public boolean shouldPause() { return false; }
}
