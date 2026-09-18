package com.libreriasmart.service;

import com.libreriasmart.model.Cliente;

/**
 * INTERFAZ (patrón Strategy): política de descuento.
 * VentaService depende de este contrato, no de una clase concreta,
 * así la regla se cambia (o se prueba) sin tocar el servicio.
 */
public interface IPoliticaDescuento {
    double calcular(Cliente cliente, double subtotal, int unidades);
}
