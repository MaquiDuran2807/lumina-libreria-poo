package com.libreriasmart.model;

/**
 * ABSTRACCIÓN central del inventario.
 * ENCAPSULAMIENTO: stock y precios protegidos, solo se mutan con reglas.
 * POLIMORFISMO: calcularPrecioFinal() y getDetalleEspecifico() cambian por subclase.
 */
public abstract class Producto {
    protected String id;
    protected String nombre;
    protected Categoria categoria;
    protected double precioCompra;
    protected double precioVenta;
    protected int stock;
    protected int stockMinimo;

    protected Producto(String id, String nombre, Categoria categoria,
                       double precioCompra, double precioVenta,
                       int stock, int stockMinimo) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
    }

    // ---------- Getters / setters con reglas ----------
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Categoria getCategoria() { return categoria; }
    public double getPrecioCompra() { return precioCompra; }
    public double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(double precioVenta) {
        if (precioVenta <= 0) throw new IllegalArgumentException("El precio debe ser > 0");
        this.precioVenta = precioVenta;
    }
    public int getStock() { return stock; }
    public int getStockMinimo() { return stockMinimo; }

    /** Margen de ganancia en % */
    public double calcularMargen() {
        if (precioCompra <= 0) return 0;
        return ((precioVenta - precioCompra) / precioCompra) * 100.0;
    }

    public boolean necesitaReposicion() {
        return stock <= stockMinimo;
    }

    /** Descuenta stock con validación. Retorna false si no hay suficiente. */
    public boolean vender(int cantidad) {
        if (cantidad <= 0 || cantidad > stock) return false;
        stock -= cantidad;
        return true;
    }

    public void reponer(int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("Cantidad > 0");
        stock += cantidad;
    }

    // ---------- Polimorfismo ----------
    /** Precio final con impuestos/recargos propios de cada tipo. */
    public abstract double calcularPrecioFinal();

    /** Texto corto con el atributo distintivo (autor, marca, edición...). */
    public abstract String getDetalleEspecifico();

    public String[] toRow() {
        return new String[]{
            id,
            nombre.length() > 28 ? nombre.substring(0, 28) + "…" : nombre,
            categoria.getEtiqueta(),
            String.format("$%,.0f", precioVenta),
            String.valueOf(stock),
            necesitaReposicion() ? "⚠ BAJO" : "OK",
            String.format("%.1f%%", calcularMargen())
        };
    }

    public static String[] headers() {
        return new String[]{"ID", "NOMBRE", "CATEGORÍA", "PRECIO", "STOCK", "ESTADO", "MARGEN"};
    }

    @Override
    public String toString() {
        return id + " · " + nombre + " ($" + String.format("%,.0f", precioVenta) + ")";
    }
}
