package com.libreriasmart.model;

/**
 * INTERFAZ: contrato de presentación en consola.
 * Todo lo que puede dibujarse como fila de tabla lo implementa
 * (Persona, Producto, DetalleVenta, Venta).
 * La UI (ConsoleUI.tabla) pide Tabulable, no clases concretas:
 * si mañana aparece una clase nueva que sea Tabulable, se dibuja sin tocar la UI.
 */
public interface Tabulable {
    String[] toRow();
}
