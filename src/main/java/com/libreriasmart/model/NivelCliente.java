package com.libreriasmart.model;

/** Nivel de fidelización del cliente. Define descuento automático. */
public enum NivelCliente {
    BRONCE(0, 0.0),
    PLATA(200, 0.05),
    ORO(500, 0.10),
    DIAMANTE(1000, 0.15);

    private final int puntosMinimos;
    private final double descuento;

    NivelCliente(int puntosMinimos, double descuento) {
        this.puntosMinimos = puntosMinimos;
        this.descuento = descuento;
    }

    public double getDescuento() {
        return descuento;
    }

    public static NivelCliente porPuntos(int puntos) {
        if (puntos >= DIAMANTE.puntosMinimos) return DIAMANTE;
        if (puntos >= ORO.puntosMinimos) return ORO;
        if (puntos >= PLATA.puntosMinimos) return PLATA;
        return BRONCE;
    }
}
