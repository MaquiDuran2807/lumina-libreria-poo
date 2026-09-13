package com.libreriasmart.model;

/** HERENCIA: útil escolar / arte con marca y tipo. */
public class UtilEscolar extends Producto {
    private String marca;
    private String tipoUtil;

    public UtilEscolar(String id, String nombre, Categoria categoria,
                       double precioCompra, double precioVenta,
                       int stock, int stockMinimo,
                       String marca, String tipoUtil) {
        super(id, nombre, categoria, precioCompra, precioVenta, stock, stockMinimo);
        this.marca = marca;
        this.tipoUtil = tipoUtil;
    }

    public String getMarca() { return marca; }
    public String getTipoUtil() { return tipoUtil; }

    @Override
    public double calcularPrecioFinal() {
        return precioVenta;
    }

    @Override
    public String getDetalleEspecifico() {
        return marca + " · " + tipoUtil;
    }
}
