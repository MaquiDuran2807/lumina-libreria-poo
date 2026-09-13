package com.libreriasmart.model;

/** HERENCIA: Revista con edición y periodicidad. */
public class Revista extends Producto {
    private String edicion;
    private String periodicidad;

    public Revista(String id, String nombre, Categoria categoria,
                   double precioCompra, double precioVenta,
                   int stock, int stockMinimo,
                   String edicion, String periodicidad) {
        super(id, nombre, categoria, precioCompra, precioVenta, stock, stockMinimo);
        this.edicion = edicion;
        this.periodicidad = periodicidad;
    }

    public String getEdicion() { return edicion; }
    public String getPeriodicidad() { return periodicidad; }

    @Override
    public double calcularPrecioFinal() {
        return precioVenta;
    }

    @Override
    public String getDetalleEspecifico() {
        return "Ed. " + edicion + " · " + periodicidad;
    }
}
