package com.libreriasmart.ui;

import com.libreriasmart.model.ITabulable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Utilidad de consola ESTILIZADA: colores ANSI, tablas Unicode,
 * banners, tarjetas y mensajes. Toda la UI pasa por aquí.
 */
public class ConsoleUI {
    public static final String RESET = "\u001B[0m";
    public static final String NEGRITA = "\u001B[1m";
    public static final String ROJO = "\u001B[31m";
    public static final String VERDE = "\u001B[32m";
    public static final String AMARILLO = "\u001B[33m";
    public static final String AZUL = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String GRIS = "\u001B[90m";

    private ConsoleUI() {}

    public static void limpiar() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void banner() {
        System.out.println(CYAN + NEGRITA);
        System.out.println("  ╔══════════════════════════════════════════════════════════╗");
        System.out.println("  ║  📚  L U M I N A · L I B R E R Í A                       ║");
        System.out.println("  ║      Inventario · Ventas · Clientes · Recomendaciones     ║");
        System.out.println("  ╚══════════════════════════════════════════════════════════╝" + RESET);
    }

    public static void titulo(String texto) {
        System.out.println("\n" + MAGENTA + NEGRITA + "━ " + texto + " " + "━".repeat(Math.max(0, 52 - texto.length())) + RESET);
    }

    public static void exito(String msg) {
        System.out.println(VERDE + NEGRITA + "  ✔ " + msg + RESET);
    }

    public static void error(String msg) {
        System.out.println(ROJO + NEGRITA + "  ✘ " + msg + RESET);
    }

    public static void info(String msg) {
        System.out.println(CYAN + "  ℹ " + msg + RESET);
    }

    public static void alerta(String msg) {
        System.out.println(AMARILLO + NEGRITA + "  ⚠ " + msg + RESET);
    }

    public static void tarjeta(String titulo, String... lineas) {
        System.out.println(AZUL + "  ┌─ " + NEGRITA + titulo + RESET + AZUL + " " + "─".repeat(50) + RESET);
        for (String l : lineas) {
            System.out.println("  │  " + l);
        }
        System.out.println(AZUL + "  └" + "─".repeat(55) + RESET);
    }

    /** Tabla Unicode auto-ajustada. */
    public static void tabla(String[] headers, List<String[]> filas) {
        int cols = headers.length;
        int[] ancho = new int[cols];
        for (int i = 0; i < cols; i++) ancho[i] = visible(headers[i]);
        for (String[] f : filas) {
            for (int i = 0; i < cols && i < f.length; i++) {
                ancho[i] = Math.max(ancho[i], visible(f[i]));
            }
        }
        for (int i = 0; i < cols; i++) ancho[i] = Math.min(ancho[i] + 2, 32);

        StringBuilder borde = new StringBuilder("  ┌");
        for (int i = 0; i < cols; i++) {
            borde.append("─".repeat(ancho[i]));
            borde.append(i == cols - 1 ? "┐" : "┬");
        }
        System.out.println(GRIS + borde + RESET);

        // Encabezado
        StringBuilder h = new StringBuilder("  │");
        for (int i = 0; i < cols; i++) {
            h.append(NEGRITA).append(CYAN).append(pad(headers[i], ancho[i])).append(RESET).append("│");
        }
        System.out.println(h);

        StringBuilder sep = new StringBuilder("  ├");
        for (int i = 0; i < cols; i++) {
            sep.append("─".repeat(ancho[i]));
            sep.append(i == cols - 1 ? "┤" : "┼");
        }
        System.out.println(GRIS + sep + RESET);

        if (filas.isEmpty()) {
            System.out.println("  │" + GRIS + "  (sin resultados)" + " ".repeat(40) + RESET + "│");
        }
        for (String[] f : filas) {
            StringBuilder row = new StringBuilder("  │");
            for (int i = 0; i < cols; i++) {
                String val = i < f.length ? f[i] : "";
                // Resalta alertas de stock
                if (val.contains("BAJO")) {
                    row.append(AMARILLO).append(NEGRITA).append(pad(val, ancho[i])).append(RESET).append("│");
                } else {
                    row.append(pad(val, ancho[i])).append("│");
                }
            }
            System.out.println(row);
        }

        StringBuilder pie = new StringBuilder("  └");
        for (int i = 0; i < cols; i++) {
            pie.append("─".repeat(ancho[i]));
            pie.append(i == cols - 1 ? "┘" : "┴");
        }
        System.out.println(GRIS + pie + RESET);
        System.out.println(GRIS + "  ↳ " + filas.size() + " registro(s)" + RESET);
    }

    /**
     * Versión POO: acepta cualquier lista de ITabulable
     * (Producto, Cliente, Venta, DetalleVenta...) sin mapeos manuales.
     * La UI depende de la INTERFAZ, no de las clases concretas.
     * (Se llama tablaDe porque Java no permite sobrecargar solo por
     *  genérico: List&lt;String[]&gt; y List&lt;ITabulable&gt; se borran igual.)
     */
    public static void tablaDe(String[] headers, List<? extends ITabulable> items) {
        List<String[]> filas = new ArrayList<>();
        for (ITabulable t : items) filas.add(t.toRow());
        tabla(headers, filas);
    }

    public static void menu(String[] opciones) {
        System.out.println();
        for (int i = 0; i < opciones.length; i++) {
            System.out.println("   " + CYAN + NEGRITA + "[" + i + "]" + RESET + " " + opciones[i]);
        }
        System.out.print("\n  " + NEGRITA + "➤ Elige una opción: " + RESET);
    }

    public static void pausar(Scanner sc) {
        System.out.print(GRIS + "\n  Pulsa ENTER para continuar..." + RESET);
        sc.nextLine();
    }

    // ---------- helpers ----------
    private static String pad(String s, int n) {
        int v = visible(s);
        String base = " " + s + " ";
        int faltan = n - v - 2;
        if (faltan < 0) faltan = 0;
        return base + " ".repeat(faltan);
    }

    /** Longitud visible sin códigos ANSI ni emojis anchos (aprox). */
    private static int visible(String s) {
        String limpio = s.replaceAll("\u001B\\[[;\\d]*m", "");
        return limpio.length();
    }

    public static String dinero(double v) {
        return String.format("$%,.0f", v);
    }
}
