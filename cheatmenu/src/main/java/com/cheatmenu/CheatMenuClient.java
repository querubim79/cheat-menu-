package com.cheatmenu;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * Mod 100% client-side: não registra pacotes, itens ou blocos e não precisa estar no servidor.
 * Tudo começa desligado a cada vez que você entra num mundo ou servidor.
 */
public class CheatMenuClient implements ClientModInitializer {
    public static KeyBinding OPEN_KEY;

    @Override
    public void onInitializeClient() {
        OPEN_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.cheatmenu.open",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.cheatmenu"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(CheatMenuClient::onTick);
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> CheatState.reset());
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> CheatState.reset());
    }

    private static void onTick(MinecraftClient mc) {
        while (OPEN_KEY.wasPressed()) {
            if (mc.player != null && mc.currentScreen == null) {
                mc.setScreen(new CheatMenuScreen());
            }
        }

        // a flag do criativo só vale enquanto a tela de criativo está aberta
        if (!(mc.currentScreen instanceof CreativeInventoryScreen)) {
            CheatState.creativeOpen = false;
        }

        KillAura.tick(mc);
    }
}
