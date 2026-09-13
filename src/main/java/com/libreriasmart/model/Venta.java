package com.libreriasmart.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * COMPOSICIÓN + AGREGACIÓN: Venta contiene DetalleVentas y referencia un Cliente.
 * Encapsula cálculo de totales, descuento e IVA.
 */
public class Venta {
    private static int consecutivo = 1000;

    private final String id;
    private final Cliente cliente;
    private final LocalDateTime fecha;
    private final List<DetalleVenta> detalles = new ArrayList<>();
    private double tasaDescuento; // 0.0 - 0.30
    private static final double IVA = 0.19;

    public Venta(Cliente cliente) {
        this.id = "V-" + (++consecutivo);
        this.cliente = cliente;
        this.fecha = LocalDateTime.now();
    }

    /** Constructor para datos históricos (seed). */
    public Venta(String id, Cliente cliente, LocalDateTime fecha, double tasaDescuento) {
        this.id = id;
        this.cliente = cliente;
        this.fecha = fecha;
        this.tasaDescuento = tasaDescuento;
    }

    public String getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public LocalDateTime getFecha() { return fecha; }
    public List<DetalleVenta> getDetalles() { return detalles; }
    public double getTasaDescuento() { return tasaDescuento; }
    public void setTasaDescuento(double tasaDescuento) {
        this.tasaDescuento = Math.max(0, Math.min(0.30, tasaDescuento));
    }

    public void agregarDetalle(Producto producto, int cantidad) {
        boolean ok = producto.vender(cantidad);
        if (!ok) throw new IllegalArgumentException(
            "Stock insuficiente de '" + producto.getNombre() + "' (disponible: " + producto.getStock() + ")");
        detalles.add(new DetalleVenta(producto, cantidad));
    }

    /** Solo para seed histórico: no descuenta stock de nuevo. */
    public void agregarDetalleHistorico(Producto producto, int cantidad) {
        detalles.add(new DetalleVenta(producto, cantidad));
    }

    public double getSubtotal() {
        return detalles.stream().mapToDouble(DetalleVenta::getSubtotal).sum();
    }

    public double getValorDescuento() {
        return getSubtotal() * tasaDescuento;
    }

    public double getBaseConDescuento() {
        return getSubtotal() - getValorDescuento();
    }

    public double getIva() {
        return getBaseConDescuento() * IVA;
    }

    public double getTotalFinal() {
        return getBaseConDescuento() + getIva();
    }

    public double getGananciaTotal() {
        return detalles.stream().mapToDouble(DetalleVenta::getGanancia).sum() - getValorDescuento();
    }

    public int getTotalUnidades() {
        return detalles.stream().mapToInt(DetalleVenta::getCantidad).sum();
    }

    public String getFechaCorta() {
        return fecha.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public String[] toRow() {
        return new String[]{
            id, cliente.getNombre(), getFechaCorta(),
            String.valueOf(getTotalUnidades()),
            String.format("$%,.0f", getTotalFinal())
        };
    }

    public static String[] headers() {
        return new String[]{"ID", "CLIENTE", "FECHA", "UNDS", "TOTAL"};
    }
}
