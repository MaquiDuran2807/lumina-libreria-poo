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
    private final IPoliticaDescuento politica;

    public VentaService() {
        this(new DescuentoEstandar());
    }

    /** INYECCIÓN: la política se pasa como interfaz, se cambia sin tocar el servicio. */
    public VentaService(IPoliticaDescuento politica) {
        this.politica = politica;
    }

    public double calcularDescuento(Cliente c, double subtotal, int unidades) {
        return politica.calcular(c, subtotal, unidades);
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
