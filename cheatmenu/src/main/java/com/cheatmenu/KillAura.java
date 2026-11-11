package com.cheatmenu;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public final class KillAura {
    private KillAura() {}

    public static void tick(MinecraftClient mc) {
        if (!CheatState.killAura) return;

        ClientPlayerEntity player = mc.player;
        if (player == null || mc.world == null || mc.interactionManager == null) return;
        if (!player.isAlive() || player.isSpectator()) return;

        // respeita o cooldown do ataque (dano total, menos chance de flag no anticheat)
        if (player.getAttackCooldownProgress(0.5f) < 1.0f) return;

        double range = CheatState.range;
        double rangeSq = range * range;
        Vec3d eye = player.getEyePos();
        Box search = player.getBoundingBox().expand(range + 1.0);

        LivingEntity best = null;
        double bestDist = Double.MAX_VALUE;

        for (LivingEntity target : mc.world.getEntitiesByClass(LivingEntity.class, search, t -> isValidTarget(player, t))) {
            double d = distanceSqToBox(eye, target.getBoundingBox());
            if (d > rangeSq) continue;
            if (!player.canSee(target)) continue;
            if (d < bestDist) {
                bestDist = d;
                best = target;
            }
        }

        if (best == null) return;

        mc.interactionManager.attackEntity(player, best);
        player.swingHand(Hand.MAIN_HAND);
    }

    private static boolean isValidTarget(ClientPlayerEntity self, LivingEntity e) {
        if (e == self || !e.isAlive() || e.isSpectator()) return false;
        if (e instanceof ArmorStandEntity) return false;
        if (e instanceof TameableEntity pet && pet.isTamed()) return false;

        return switch (CheatState.targets) {
            case HOSTILE -> e instanceof Monster;
            case PLAYERS -> e instanceof PlayerEntity;
            case ALL -> true;
        };
    }

    /** Distância ao quadrado do olho até o ponto mais próximo da hitbox. */
    private static double distanceSqToBox(Vec3d p, Box b) {
        double dx = Math.max(Math.max(b.minX - p.x, 0.0), p.x - b.maxX);
        double dy = Math.max(Math.max(b.minY - p.y, 0.0), p.y - b.maxY);
        double dz = Math.max(Math.max(b.minZ - p.z, 0.0), p.z - b.maxZ);
        return dx * dx + dy * dy + dz * dz;
    }
}
