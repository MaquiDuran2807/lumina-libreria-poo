package com.libreriasmart.service;

import com.libreriasmart.model.*;
import com.libreriasmart.repository.Database;
import java.util.List;

/**
 * Lógica de ventas: descuentos por nivel y volumen + registro + fidelización.
 * Reglas de descuento:
 *  - Nivel del cliente (0–15%)
 *  - +5% si compra 5+ unidades, +3% si subtotal > $150.000
 *  - Tope 25%
 */
public class VentaService {
    private final Database db = Database.getInstancia();

    public double calcularDescuento(Cliente c, double subtotal, int unidades) {
        double d = c.getDescuentoNivel();
        if (unidades >= 5) d += 0.05;
        if (subtotal > 150000) d += 0.03;
        return Math.min(d, 0.25);
    }

    public Venta crearVenta(Cliente cliente, List<ItemCarrito> carrito) {
        if (carrito.isEmpty()) throw new IllegalArgumentException("Carrito vacío");
        Venta venta = new Venta(cliente);
        double subtotal = carrito.stream()
            .mapToDouble(i -> i.producto.calcularPrecioFinal() * i.cantidad).sum();
        int unidades = carrito.stream().mapToInt(i -> i.cantidad).sum();
        venta.setTasaDescuento(calcularDescuento(cliente, subtotal, unidades));
        for (ItemCarrito i : carrito) {
            venta.agregarDetalle(i.producto, i.cantidad);
        }
        db.getVentas().add(venta);
        cliente.agregarCompra(venta.getTotalFinal());
        return venta;
    }

    /** Mini-DTO para el carrito del POS. */
    public static class ItemCarrito {
        public final Producto producto;
        public final int cantidad;
        public ItemCarrito(Producto producto, int cantidad) {
            this.producto = producto;
            this.cantidad = cantidad;
        }
    }
}
