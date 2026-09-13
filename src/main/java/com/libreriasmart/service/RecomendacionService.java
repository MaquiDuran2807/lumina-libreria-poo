package com.libreriasmart.service;

import com.libreriasmart.model.*;
import com.libreriasmart.repository.Database;
import java.util.*;
import java.util.stream.Collectors;

/**
 * MOTOR DE RECOMENDACIÓN simple pero efectivo:
 *  1) Misma categoría (peso 3)  2) Mismo autor/marca (peso 5)
 *  3) Popularidad histórica en ventas (peso 2 por unidad vendida)
 *  Excluye el producto origen y los sin stock.
 */
public class RecomendacionService {
    private final Database db = Database.getInstancia();

    public List<Producto> recomendar(Producto base, int max) {
        Map<String, Integer> popularidad = ventasPorProducto();

        return db.getProductos().stream()
            .filter(p -> !p.getId().equals(base.getId()) && p.getStock() > 0)
            .sorted(Comparator.comparingInt((Producto p) -> puntaje(p, base, popularidad)).reversed())
            .limit(max)
            .collect(Collectors.toList());
    }

    private int puntaje(Producto cand, Producto base, Map<String, Integer> pop) {
        int s = 0;
        if (cand.getCategoria() == base.getCategoria()) s += 30;
        if (cand.getDetalleEspecifico() != null && base.getDetalleEspecifico() != null) {
            String a = primeraPalabra(cand.getDetalleEspecifico());
            String b = primeraPalabra(base.getDetalleEspecifico());
            if (!a.isEmpty() && a.equalsIgnoreCase(b)) s += 50;
        }
        s += pop.getOrDefault(cand.getId(), 0) * 2;
        // Desempate: mejor margen (le conviene a la librería)
        s += (int) (cand.calcularMargen() / 10);
        return s;
    }

    private String primeraPalabra(String s) {
        String[] parts = s.split("[ ·,]+");
        return parts.length > 0 ? parts[0] : "";
    }

    /** Conteo histórico de unidades vendidas por producto. */
    public Map<String, Integer> ventasPorProducto() {
        Map<String, Integer> mapa = new HashMap<>();
        for (Venta v : db.getVentas()) {
            for (DetalleVenta d : v.getDetalles()) {
                mapa.merge(d.getProducto().getId(), d.getCantidad(), Integer::sum);
            }
        }
        return mapa;
    }

    public List<Producto> topVentas(int top) {
        Map<String, Integer> pop = ventasPorProducto();
        return db.getProductos().stream()
            .sorted(Comparator.comparingInt((Producto p) -> pop.getOrDefault(p.getId(), 0)).reversed())
            .limit(top)
            .collect(Collectors.toList());
    }
}
