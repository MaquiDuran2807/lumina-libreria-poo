package com.libreriasmart.service;

import com.libreriasmart.model.*;
import com.libreriasmart.repository.Database;
import java.util.*;
import java.util.stream.Collectors;

/** REPORTES: dashboard, rankings y alertas. */
public class ReporteService {
    private final Database db = Database.getInstancia();
    private final RecomendacionService rec = new RecomendacionService();

    public double ingresosTotales() {
        return db.getVentas().stream().mapToDouble(Venta::getTotalFinal).sum();
    }

    public double gananciaTotal() {
        return db.getVentas().stream().mapToDouble(Venta::getGananciaTotal).sum();
    }

    public double ticketPromedio() {
        if (db.getVentas().isEmpty()) return 0;
        return ingresosTotales() / db.getVentas().size();
    }

    public Optional<Cliente> mejorCliente() {
        return db.getClientes().stream()
            .max(Comparator.comparingInt(Cliente::getPuntosFidelidad));
    }

    public Map<Categoria, Double> ventasPorCategoria() {
        Map<Categoria, Double> mapa = new EnumMap<>(Categoria.class);
        for (Venta v : db.getVentas()) {
            for (DetalleVenta d : v.getDetalles()) {
                mapa.merge(d.getProducto().getCategoria(), d.getSubtotal(), Double::sum);
            }
        }
        return mapa;
    }

    public Map<Categoria, Double> topCategorias(int top) {
        return ventasPorCategoria().entrySet().stream()
            .sorted(Map.Entry.<Categoria, Double>comparingByValue().reversed())
            .limit(top)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (a, b) -> a, LinkedHashMap::new));
    }

    public List<Producto> productosTop(int top) {
        return rec.topVentas(top);
    }

    public long totalUnidadesVendidas() {
        return db.getVentas().stream().mapToLong(Venta::getTotalUnidades).sum();
    }
}
