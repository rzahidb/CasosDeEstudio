package org.example.casosdeestudioproyecto.model;

import org.example.casosdeestudioproyecto.model.ResultadoReservas;


/** Recurso compartido: consultar y descontar forman una unica operacion atomica. */
public class InventarioEntradas {
    private final int entradasIniciales;
    private int entradasDisponibles;
    private int reservasConfirmadas;
    private int solicitudesRechazadas;

    public InventarioEntradas(int entradasIniciales) {
        if (entradasIniciales < 0) {
            throw new IllegalArgumentException("El numero de entradas no puede ser negativo");
        }
        this.entradasIniciales = entradasIniciales;
        this.entradasDisponibles = entradasIniciales;
    }

    public synchronized ResultadoReservas reservarEntrada(String nombreAgente, int numeroSolicitud) {
        boolean confirmada;
        String mensaje;
        if (entradasDisponibles > 0) {
            entradasDisponibles--;
            reservasConfirmadas++;
            confirmada = true;
            mensaje = String.format(
                    "[CONFIRMADA] %s (Intento #%02d) -> 1 entrada asignada | Disponibles: %d | Confirmadas: %d | Rechazadas: %d",
                    nombreAgente, numeroSolicitud, entradasDisponibles, reservasConfirmadas, solicitudesRechazadas);
        } else {
            solicitudesRechazadas++;
            confirmada = false;
            mensaje = String.format(
                    "[RECHAZADA] %s (Intento #%02d) -> Sin cupo | Disponibles: %d | Confirmadas: %d | Rechazadas: %d",
                    nombreAgente, numeroSolicitud, entradasDisponibles, reservasConfirmadas, solicitudesRechazadas);
        }
        System.out.println(mensaje);
        return new ResultadoReservas(entradasIniciales, entradasDisponibles,
                reservasConfirmadas, solicitudesRechazadas, confirmada, mensaje);
    }

    public synchronized ResultadoReservas obtenerEstadoActual() {
        return new ResultadoReservas(entradasIniciales, entradasDisponibles,
                reservasConfirmadas, solicitudesRechazadas, false,
                "Consulta del estado actual del inventario");
    }

    /** Solo debe llamarse cuando hayan terminado los hilos de la simulacion anterior. */
    public synchronized void reiniciarInventario() {
        entradasDisponibles = entradasIniciales;
        reservasConfirmadas = 0;
        solicitudesRechazadas = 0;
    }
}