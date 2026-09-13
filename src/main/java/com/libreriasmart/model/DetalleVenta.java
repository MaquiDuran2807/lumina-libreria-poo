package com.libreriasmart.model;

/** COMPOSICIÓN: línea de una venta (producto x cantidad). */
public class DetalleVenta {
    private final Producto producto;
    private final int cantidad;
    private final double precioUnitario;

    public DetalleVenta(Producto producto, int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("Cantidad > 0");
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = producto.calcularPrecioFinal();
    }

    public Producto getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }

    public double getSubtotal() {
        return precioUnitario * cantidad;
    }

    public double getGanancia() {
        return (precioUnitario - producto.getPrecioCompra()) * cantidad;
    }

    public String[] toRow() {
        return new String[]{
            producto.getId(),
            producto.getNombre(),
            String.valueOf(cantidad),
            String.format("$%,.0f", precioUnitario),
            String.format("$%,.0f", getSubtotal())
        };
    }

    public static String[] headers() {
        return new String[]{"ID", "PRODUCTO", "CANT", "P.UNIT", "SUBTOTAL"};
    }
}
