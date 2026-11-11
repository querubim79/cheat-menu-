package com.cheatmenu;

/** Estado global do menu (tudo client-side, tudo desligado ao entrar num mundo/servidor). */
public final class CheatState {
    private CheatState() {}

    public enum Targets {
        HOSTILE("Monstros"), PLAYERS("Jogadores"), ALL("Todos");

        public final String label;
        Targets(String label) { this.label = label; }

        public Targets next() {
            Targets[] v = values();
            return v[(ordinal() + 1) % v.length];
        }
    }

    public static final double MIN_RANGE = 1.0;
    public static final double MAX_RANGE = 4.0; // servidores vanilla ignoram acima disso (3.0 + 1.0 de folga)

    public static boolean killAura = false;
    public static double range = 3.0;
    public static Targets targets = Targets.HOSTILE;

    /** true enquanto a tela de criativo foi aberta pelo menu. */
    public static boolean creativeOpen = false;
    /** true = a tecla E abre o inventário criativo mesmo no survival. */
    public static boolean creativeOnInventoryKey = false;

    public static boolean creativeActive() {
        return creativeOpen || creativeOnInventoryKey;
    }

    public static void reset() {
        killAura = false;
        creativeOpen = false;
        creativeOnInventoryKey = false;
    }
}
