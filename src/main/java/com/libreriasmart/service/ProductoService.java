package com.libreriasmart.service;

import com.libreriasmart.model.*;
import com.libreriasmart.repository.Database;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/** Lógica de inventario y búsqueda. */
public class ProductoService {
    private final Database db = Database.getInstancia();

    public List<Producto> listarTodos() {
        return db.getProductos();
    }

    /**
     * BÚSQUEDA tolerante: ignora mayúsculas/tildes parciales y busca en
     * nombre + detalle específico (autor/marca) + categoría + id.
     */
    public List<Producto> buscar(String query) {
        if (query == null || query.isBlank()) return listarTodos();
        String q = normalizar(query);
        return db.getProductos().stream()
            .filter(p -> normalizar(p.getNombre()).contains(q)
                || normalizar(p.getDetalleEspecifico()).contains(q)
                || normalizar(p.getCategoria().getEtiqueta()).contains(q)
                || normalizar(p.getId()).contains(q))
            .collect(Collectors.toList());
    }

    public List<Producto> porCategoria(Categoria cat) {
        return db.getProductos().stream()
            .filter(p -> p.getCategoria() == cat)
            .collect(Collectors.toList());
    }

    public List<Producto> stockBajo() {
        return db.getProductos().stream()
            .filter(Producto::necesitaReposicion)
            .sorted(Comparator.comparingInt(Producto::getStock))
            .collect(Collectors.toList());
    }

    public List<Producto> masRentables(int top) {
        return db.getProductos().stream()
            .sorted(Comparator.comparingDouble(Producto::calcularMargen).reversed())
            .limit(top)
            .collect(Collectors.toList());
    }

    public void agregar(Producto p) {
        db.getProductos().add(p);
    }

    public boolean eliminar(String id) {
        return db.buscarProductoPorId(id)
            .map(p -> db.getProductos().remove(p))
            .orElse(false);
    }

    private String normalizar(String s) {
        return java.text.Normalizer.normalize(s.toLowerCase(), java.text.Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "");
    }
}
