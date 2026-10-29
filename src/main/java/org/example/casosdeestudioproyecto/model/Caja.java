package org.example.casosdeestudioproyecto.model;

import org.example.casosdeestudioproyecto.model.ResultadoCaja;

/** Recurso compartido entre los tres vendedores. */
public class Caja {
    private int cantidadVentas;
    private int totalRecaudado;

    /** Suma venta y recaudacion de forma atomica dentro del mismo bloqueo. */
    public synchronized ResultadoCaja registrarVentas(String vendedor, int monto) {
        cantidadVentas++;
        totalRecaudado += monto;
        String mensaje = vendedor + " registró una venta de C$" + monto
                + " | Ventas acumuladas: " + cantidadVentas
                + " | Total: C$" + totalRecaudado;
        System.out.println(mensaje);
        return new ResultadoCaja(cantidadVentas, totalRecaudado, mensaje);
    }

    public synchronized int getCantidadVentas() { return cantidadVentas; }
    public synchronized int getTotalRecaudado() { return totalRecaudado; }

    public synchronized ResultadoCaja obtenerInstantanea() {
        return new ResultadoCaja(cantidadVentas, totalRecaudado,
                "Instantánea actual de caja");
    }

    /** Llamar solo cuando todos los vendedores anteriores hayan terminado. */
    public synchronized void reiniciarCaja() {
        cantidadVentas = 0;
        totalRecaudado = 0;
    }
}
