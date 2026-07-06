package com.taczlevel.gui;

import com.taczlevel.TaczLevelMod;
import com.taczlevel.data.GunLevelManager;
import com.taczlevel.network.GunUpgradePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class GunUpgradeScreen extends AbstractContainerScreen<GunUpgradeMenu> {
    private static final int BG_MAIN       = 0xFF0a0a1a;
    private static final int BG_PANEL      = 0xFF0e0e24;
    private static final int CARD_INACTIVE = 0xFF12122e;
    private static final int CARD_ACTIVE   = 0xFF161638;
    private static final int CARD_HOVER    = 0xFF1c1c44;
    private static final int BORDER_DIM    = 0xFF1a1a3a;
    private static final int BORDER_LIGHT  = 0xFF2a2a5a;
    private static final int TEXT_MAIN     = 0xFFc8c8e8;
    private static final int TEXT_SUB      = 0xFF8888bb;
    private static final int TEXT_MUTED    = 0xFF555577;
    private static final int TEXT_GOLD     = 0xFFffaa44;
    private static final int TEXT_GREEN    = 0xFF44ff88;
    private static final int BAR_TRACK     = 0xFF2a2a44;

    private static final int[] ACCENT = {
        0xFF00d4aa, 0xFFff8800, 0xFFa855f7, 0xFFef4444,
    };
    private static final int[] ACCENT_DIM = {
        0xFF004d3f, 0xFF4d2a00, 0xFF3a1a55, 0xFF4d1515,
    };

    private static final int CARDS_X   = 74;
    private static final int CARDS_Y   = 16;
    private static final int CARD_W    = 120;
    private static final int CARD_H    = 22;
    private static final int CARD_GAP  = 2;
    private static final int BTN_W     = 50;
    private static final int BTN_H     = 12;
    private static final int OPTIONS   = 4;

    private static final int EXP_TEXT_Y = 128;
    private static final int EXP_BAR_Y  = 134;
    private static final int EXP_BAR_H  = 4;

    private final Button[] actionButtons = new Button[OPTIONS];

    public GunUpgradeScreen(GunUpgradeMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 196;
        this.imageHeight = 220;
        this.inventoryLabelY = 140;
        this.titleLabelX = 8;
    }

    @Override
    protected void init() {
        super.init();
        for (int i = 0; i < OPTIONS; i++) {
            final int opt = i;
            int bx = leftPos + CARDS_X + CARD_W - BTN_W - 6;
            int by = topPos + CARDS_Y + i * (CARD_H + CARD_GAP) + 2;
            actionButtons[i] = addRenderableWidget(new FlatButton(bx, by, BTN_W, BTN_H,
                    Component.empty(), btn -> sendUpgrade(opt), ACCENT[opt]));
        }
    }

    @Override
    public void containerTick() {
        super.containerTick();
        boolean hasGun = menu.hasGun();
        ItemStack gun = menu.getBlockEntity().getItemHandler().getStackInSlot(0);
        int activeSlot = hasGun ? GunLevelManager.getActiveSlot(gun) : -1;

        for (int i = 0; i < OPTIONS; i++) {
            int lvl = hasGun ? GunLevelManager.getLevel(gun, i) : 0;
            int maxLvl = GunLevelManager.getMaxLevelForOption(i);
            boolean maxed = hasGun && lvl >= maxLvl;
            boolean activated = hasGun && GunLevelManager.isActivated(gun, i);

            if (activeSlot == -1) {
                // No active upgrade — can activate any non-maxed option
                actionButtons[i].active = hasGun && !activated && !maxed;
                if (maxed) {
                    actionButtons[i].setMessage(Component.translatable("gui.taczlevel.status_maxed"));
                } else {
                    actionButtons[i].setMessage(Component.translatable("gui.taczlevel.activate"));
                }
            } else if (activeSlot == i) {
                // This is the active upgrade
                if (maxed) {
                    actionButtons[i].active = false;
                    actionButtons[i].setMessage(Component.translatable("gui.taczlevel.status_maxed"));
                } else if (hasGun && GunLevelManager.isAtGateLevel(gun, i) && !GunLevelManager.isGateUnlocked(gun, i)) {
                    actionButtons[i].active = true;
                    actionButtons[i].setMessage(Component.translatable("gui.taczlevel.unlock_gate"));
                } else {
                    actionButtons[i].active = false;
                    actionButtons[i].setMessage(Component.translatable("gui.taczlevel.status_active"));
                }
            } else {
                // Another upgrade is active
                actionButtons[i].active = false;
                if (maxed) {
                    actionButtons[i].setMessage(Component.translatable("gui.taczlevel.status_maxed"));
                } else {
                    actionButtons[i].setMessage(Component.translatable("gui.taczlevel.status_locked"));
                }
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mx, int my) {
        drawBackground(gui);
        drawCards(gui, mx, my);
        drawExpBar(gui);
    }

    private void drawBackground(GuiGraphics gui) {
        int x = leftPos, y = topPos, w = imageWidth, h = imageHeight;

        gui.fill(x, y, x + w, y + h, BG_MAIN);
        gui.fill(x, y, x + w, y + 1, BORDER_LIGHT);
        gui.fill(x, y + h - 1, x + w, y + h, BORDER_LIGHT);
        gui.fill(x, y, x + 1, y + h, BORDER_LIGHT);
        gui.fill(x + w - 1, y, x + w, y + h, BORDER_LIGHT);

        gui.fill(x + 68, y + 12, x + w - 4, y + 126, BG_PANEL);
        gui.fill(x + 68, y + 12, x + w - 4, y + 13, BORDER_DIM);
        gui.fill(x + 68, y + 125, x + w - 4, y + 126, BORDER_DIM);
    }

    private void drawCards(GuiGraphics gui, int mx, int my) {
        boolean hasGun = menu.hasGun();
        if (!hasGun) return;

        ItemStack gun = menu.getBlockEntity().getItemHandler().getStackInSlot(0);
        int activeSlot = GunLevelManager.getActiveSlot(gun);
        boolean showDetails = Screen.hasShiftDown();

        for (int i = 0; i < OPTIONS; i++) {
            int cx = leftPos + CARDS_X;
            int cy = topPos + CARDS_Y + i * (CARD_H + CARD_GAP);
            boolean hovered = mx >= cx && mx < cx + CARD_W && my >= cy && my < cy + CARD_H;
            boolean isActive = activeSlot == i;
            boolean activated = GunLevelManager.isActivated(gun, i);

            int bg = isActive ? CARD_ACTIVE : CARD_INACTIVE;
            if (isActive && hovered) bg = CARD_HOVER;
            int accent = isActive ? ACCENT[i] : ACCENT_DIM[i];

            gui.fill(cx, cy, cx + CARD_W, cy + CARD_H, bg);
            gui.fill(cx, cy, cx + 2, cy + CARD_H, accent);
            gui.fill(cx + 2, cy, cx + CARD_W, cy + 1, BORDER_DIM);
            gui.fill(cx + 2, cy + CARD_H - 1, cx + CARD_W, cy + CARD_H, BORDER_DIM);
            gui.fill(cx + CARD_W - 1, cy, cx + CARD_W, cy + CARD_H, BORDER_DIM);

            int lvl = GunLevelManager.getLevel(gun, i);
            int maxLvl = GunLevelManager.getMaxLevelForOption(i);
            double pct = GunLevelManager.getStatPercentage(i, lvl);
            boolean maxed = lvl >= maxLvl;

            gui.fill(cx + 7, cy + 4, cx + 11, cy + 8, accent);

            if (showDetails) {
                String statKey = GunLevelManager.getStatNameKey(i);
                Component statLine = Component.translatable(statKey, String.format("%.0f", pct));
                gui.drawString(font, statLine, cx + 16, cy + 2,
                        isActive ? TEXT_MAIN : (activated ? TEXT_MUTED : TEXT_SUB), false);

                Component lvlText = Component.translatable("gui.taczlevel.level_short",
                        String.valueOf(lvl), String.valueOf(maxLvl));
                int lvlW = font.width(lvlText);

                int barX = cx + 7 + lvlW + 6;
                int barY = cy + 15;
                int barW = CARD_W - 14 - lvlW - 6;
                if (barW < 10) barW = 10;

                int lvlColor = maxed ? TEXT_GOLD : (isActive ? TEXT_GREEN : TEXT_MUTED);
                gui.drawString(font, lvlText, cx + 7, cy + 13, lvlColor, false);

                gui.fill(barX, barY, barX + barW, barY + 3, BAR_TRACK);
                if (maxLvl > 0) {
                    int fill = (int) ((float) lvl / maxLvl * barW);
                    if (fill > 0) {
                        gui.fill(barX, barY, barX + Math.min(fill, barW), barY + 3, accent);
                    }
                }

                if (isActive && GunLevelManager.isAtGateLevel(gun, i) && !GunLevelManager.isGateUnlocked(gun, i)) {
                    gui.fill(cx + CARD_W - 14, cy + 3, cx + CARD_W - 4, cy + 10, 0xFF44aaff);
                }
            } else {
                String nameKey = GunLevelManager.getOptionNameKey(i);
                Component nameLine = Component.translatable(nameKey);
                gui.drawString(font, nameLine, cx + 16, cy + 3,
                        isActive ? TEXT_MAIN : (activated ? TEXT_MUTED : TEXT_SUB), false);

                Component lvlText = Component.translatable("gui.taczlevel.level_short",
                        String.valueOf(lvl), String.valueOf(maxLvl));
                int lvlColor = maxed ? TEXT_GOLD : (isActive ? TEXT_GREEN : TEXT_MUTED);
                gui.drawString(font, lvlText, cx + 16 + font.width(nameLine) + 6, cy + 3, lvlColor, false);

                int barX = cx + 7;
                int barY = cy + 14;
                int barW = CARD_W - 14;
                gui.fill(barX, barY, barX + barW, barY + 2, BAR_TRACK);
                if (maxLvl > 0) {
                    int fill = (int) ((float) lvl / maxLvl * barW);
                    if (fill > 0) {
                        gui.fill(barX, barY, barX + Math.min(fill, barW), barY + 2, accent);
                    }
                }
            }
        }

        if (!showDetails) {
            gui.drawString(font, Component.translatable("gui.taczlevel.shift_hint"),
                    leftPos + CARDS_X, topPos + CARDS_Y + OPTIONS * (CARD_H + CARD_GAP) + 4, TEXT_MUTED, false);
        }
    }

    private void drawExpBar(GuiGraphics gui) {
        if (!menu.hasGun()) return;
        ItemStack gun = menu.getBlockEntity().getItemHandler().getStackInSlot(0);
        int overall = GunLevelManager.getOverallLevel(gun);
        if (overall <= 0) return;

        int exp = menu.getExp();
        int needed = menu.getExpToNext();
        int barX = leftPos + 10;
        int barW = 156;

        if (needed <= 0) {
            int activeSlot = GunLevelManager.getActiveSlot(gun);
            boolean atGate = activeSlot >= 0
                    && GunLevelManager.isAtGateLevel(gun, activeSlot)
                    && !GunLevelManager.isGateUnlocked(gun, activeSlot);
            if (atGate) {
                gui.drawString(font, Component.translatable("gui.taczlevel.gate_locked"),
                        barX, topPos + EXP_TEXT_Y, TEXT_GOLD, false);
            } else {
                gui.drawString(font, Component.translatable("gui.taczlevel.exp_maxed"),
                        barX, topPos + EXP_TEXT_Y, TEXT_GOLD, false);
            }
            return;
        }

        gui.drawString(font, Component.translatable("gui.taczlevel.exp", exp, needed),
                barX, topPos + EXP_TEXT_Y, TEXT_SUB, false);

        int barY = topPos + EXP_BAR_Y;
        gui.fill(barX - 1, barY - 1, barX + barW + 1, barY + EXP_BAR_H + 1, 0xFF000000);
        gui.fill(barX, barY, barX + barW, barY + EXP_BAR_H, BAR_TRACK);

        int filled = (int) ((float) exp / needed * barW);
        if (filled > 0) {
            gui.fill(barX, barY, barX + Math.min(filled, barW), barY + EXP_BAR_H, 0xFF00d4aa);
            if (filled > 4) {
                gui.fill(barX, barY, barX + Math.min(filled, barW), barY + 1, 0xFF33ffcc);
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mx, int my) {
        gui.drawString(font, title, titleLabelX, titleLabelY, TEXT_MAIN, false);

        if (menu.hasGun()) {
            ItemStack gun = menu.getBlockEntity().getItemHandler().getStackInSlot(0);
            int overall = GunLevelManager.getOverallLevel(gun);
            if (overall > 0) {
                Component badge = Component.literal("\u2605 Lv." + overall);
                int bw = font.width(badge);
                int bx = imageWidth - 6 - bw;
                gui.fill(bx - 3, titleLabelY - 1, bx + bw + 3, titleLabelY + 9, 0xFF1a1a3a);
                gui.fill(bx - 3, titleLabelY - 1, bx + bw + 3, titleLabelY, 0xFF00d4aa);
                gui.drawString(font, badge, bx, titleLabelY, TEXT_GREEN, false);
            }
        }

        gui.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, TEXT_SUB, false);
    }

    @Override
    public void render(GuiGraphics gui, int mx, int my, float partialTick) {
        super.render(gui, mx, my, partialTick);
        renderTooltip(gui, mx, my);
    }

    private void sendUpgrade(int opt) {
        TaczLevelMod.CHANNEL.sendToServer(
                new GunUpgradePacket(menu.containerId, opt,
                        menu.getBlockEntity().getBlockPos().asLong()));
    }

    private static class FlatButton extends Button {
        private final int accentColor;

        public FlatButton(int x, int y, int w, int h, Component msg, OnPress onPress, int accent) {
            super(x, y, w, h, msg, onPress, DEFAULT_NARRATION);
            this.accentColor = accent;
        }

        @Override
        protected void renderWidget(GuiGraphics gui, int mx, int my, float partialTick) {
            int bg;
            int txt;
            if (!active) {
                bg = 0xFF181830;
                txt = 0xFF555577;
            } else if (isHoveredOrFocused()) {
                bg = 0xFF3a3a7a;
                txt = 0xFFe8e8ff;
            } else {
                bg = 0xFF2a2a5a;
                txt = 0xFFc8c8e8;
            }

            gui.fill(getX(), getY(), getX() + width, getY() + height, bg);
            gui.fill(getX(), getY(), getX() + width, getY() + 1, active ? accentColor : 0xFF2a2a4a);

            Component msg = getMessage();
            gui.drawString(Minecraft.getInstance().font, msg,
                    getX() + (width - Minecraft.getInstance().font.width(msg)) / 2,
                    getY() + (height - 8) / 2, txt, false);
        }
    }
}
