package com.cheatmenu;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Locale;

public class CheatMenuScreen extends Screen {
    private ButtonWidget auraBtn;
    private ButtonWidget rangeBtn;
    private ButtonWidget targetBtn;
    private ButtonWidget eKeyBtn;

    public CheatMenuScreen() {
        super(Text.literal("Cheat Menu"));
    }

    @Override
    protected void init() {
        final int w = 200, h = 20, gap = 24;
        final int x = this.width / 2 - w / 2;
        int y = this.height / 2 - 80;

        auraBtn = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> {
            CheatState.killAura = !CheatState.killAura;
            refresh();
        }).dimensions(x, y, w, h).build());
        y += gap;

        addDrawableChild(ButtonWidget.builder(Text.literal("-"), b -> {
            CheatState.range = Math.max(CheatState.MIN_RANGE, CheatState.range - 0.5);
            refresh();
        }).dimensions(x, y, 20, h).build());

        rangeBtn = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> {})
                .dimensions(x + 22, y, w - 44, h).build());
        rangeBtn.active = false;

        addDrawableChild(ButtonWidget.builder(Text.literal("+"), b -> {
            CheatState.range = Math.min(CheatState.MAX_RANGE, CheatState.range + 0.5);
            refresh();
        }).dimensions(x + w - 20, y, 20, h).build());
        y += gap;

        targetBtn = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> {
            CheatState.targets = CheatState.targets.next();
            refresh();
        }).dimensions(x, y, w, h).build());
        y += gap + 8;

        addDrawableChild(ButtonWidget.builder(Text.literal("Abrir inventário criativo"), b -> openCreative())
                .dimensions(x, y, w, h).build());
        y += gap;

        eKeyBtn = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> {
            CheatState.creativeOnInventoryKey = !CheatState.creativeOnInventoryKey;
            refresh();
        }).dimensions(x, y, w, h).build());
        y += gap + 8;

        addDrawableChild(ButtonWidget.builder(Text.literal("Fechar"), b -> close())
                .dimensions(x, y, w, h).build());

        refresh();
    }

    private void refresh() {
        boolean on = CheatState.killAura;
        auraBtn.setMessage(Text.literal("Kill Aura: " + (on ? "LIGADO" : "DESLIGADO"))
                .formatted(on ? Formatting.GREEN : Formatting.RED));
        rangeBtn.setMessage(Text.literal(String.format(Locale.ROOT, "Alcance: %.1f", CheatState.range)));
        targetBtn.setMessage(Text.literal("Alvos: " + CheatState.targets.label));

        boolean e = CheatState.creativeOnInventoryKey;
        eKeyBtn.setMessage(Text.literal("Criativo na tecla E: " + (e ? "SIM" : "NÃO"))
                .formatted(e ? Formatting.GREEN : Formatting.WHITE));
    }

    private void openCreative() {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity p = mc.player;
        if (p == null) return;
        CheatState.creativeOpen = true;
        mc.setScreen(new CreativeInventoryScreen(p, p.networkHandler.getEnabledFeatures(), false));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (CheatMenuClient.OPEN_KEY != null && CheatMenuClient.OPEN_KEY.matchesKey(keyCode, scanCode)) {
            close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 105, 0xFFFFFF);
    }
}
