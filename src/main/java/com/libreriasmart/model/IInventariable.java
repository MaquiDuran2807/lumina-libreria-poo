package com.libreriasmart.model;

/**
 * INTERFAZ: contrato de inventario con reglas.
 * Quien lo implementa (Producto) garantiza que el stock
 * solo cambia vía vender()/reponer() y que avisa si está bajo.
 * Los servicios piden IInventariable cuando solo les importa el stock.
 */
public interface IInventariable {
    int getStock();
    boolean vender(int cantidad);
    void reponer(int cantidad);
    boolean necesitaReposicion();
}
