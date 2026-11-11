package com.cheatmenu.mixin;

import com.cheatmenu.CheatState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.ItemStack;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    /** Faz o cliente achar que tem inventário criativo (só quando o menu pediu). */
    @Inject(method = "hasCreativeInventory", at = @At("HEAD"), cancellable = true)
    private void cheatmenu$hasCreativeInventory(CallbackInfoReturnable<Boolean> cir) {
        if (CheatState.creativeActive()) {
            cir.setReturnValue(true);
        }
    }

    /**
     * No singleplayer / mundo LAN próprio, entrega o item direto pelo servidor integrado.
     * Em servidores remotos não faz nada (o servidor é quem decide o inventário).
     */
    @Inject(method = "clickCreativeStack", at = @At("HEAD"), cancellable = true)
    private void cheatmenu$clickCreativeStack(ItemStack stack, int slotId, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;
        if (player == null || player.isCreative()) return;
        if (!(mc.currentScreen instanceof CreativeInventoryScreen)) return;
        if (!CheatState.creativeActive()) return;

        IntegratedServer server = mc.getServer();
        if (server == null) return; // servidor remoto: comportamento vanilla (nenhum pacote enviado)

        ServerPlayerEntity sp = server.getPlayerManager().getPlayer(player.getUuid());
        if (sp == null) return;

        ItemStack copy = stack.copy();
        server.execute(() -> {
            if (slotId < 0) {
                if (!copy.isEmpty()) sp.dropItem(copy, true);
            } else if (slotId >= 1 && slotId <= 45) {
                sp.playerScreenHandler.getSlot(slotId).setStack(copy);
                sp.playerScreenHandler.sendContentUpdates();
            }
        });
        ci.cancel();
    }
}
