package com.libreriasmart.service;

import com.libreriasmart.model.Cliente;

/**
 * Implementación estándar de IPoliticaDescuento:
 * base por nivel del cliente (0–15%) + 5% si 5+ unidades
 * + 3% si subtotal > $150.000, con tope de 25%.
 */
public class DescuentoEstandar implements IPoliticaDescuento {
    @Override
    public double calcular(Cliente cliente, double subtotal, int unidades) {
        double d = cliente.getDescuentoNivel();
        if (unidades >= 5) d += 0.05;
        if (subtotal > 150000) d += 0.03;
        return Math.min(d, 0.25);
    }
}
