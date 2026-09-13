package com.libreriasmart.model;

/**
 * HERENCIA de Persona. Gestiona fidelización por niveles y puntos.
 */
public class Cliente extends Persona {
    private int puntosFidelidad;
    private int totalCompras;

    public Cliente(String id, String nombre, String email, String telefono,
                   int puntosFidelidad, int totalCompras) {
        super(id, nombre, email, telefono);
        this.puntosFidelidad = puntosFidelidad;
        this.totalCompras = totalCompras;
    }

    public Cliente(String id, String nombre, String email, String telefono) {
        this(id, nombre, email, telefono, 0, 0);
    }

    public int getPuntosFidelidad() { return puntosFidelidad; }
    public int getTotalCompras() { return totalCompras; }

    public NivelCliente getNivel() {
        return NivelCliente.porPuntos(puntosFidelidad);
    }

    public double getDescuentoNivel() {
        return getNivel().getDescuento();
    }

    /** 1 punto por cada $10.000 de compra. Sube de nivel automáticamente. */
    public void agregarCompra(double valorCompra) {
        int nuevos = (int) (valorCompra / 10000);
        this.puntosFidelidad += nuevos;
        this.totalCompras++;
    }

    @Override
    public String getTipo() { return "CLIENTE"; }

    @Override
    public String[] toRow() {
        return new String[]{
            id, nombre, getNivel().name(),
            String.valueOf(puntosFidelidad),
            String.valueOf(totalCompras), telefono
        };
    }

    public static String[] headers() {
        return new String[]{"ID", "NOMBRE", "NIVEL", "PUNTOS", "COMPRAS", "TELÉFONO"};
    }
}
