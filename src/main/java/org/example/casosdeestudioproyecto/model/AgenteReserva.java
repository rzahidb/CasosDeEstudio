package org.example.casosdeestudioproyecto.model;

import org.example.casosdeestudioproyecto.model.InventarioEntradas;
import org.example.casosdeestudioproyecto.model.ResultadoReservas;

import java.util.function.BiConsumer;

/** Cada agente realiza sus solicitudes en un Thread diferente. */
public class AgenteReserva implements Runnable {
    private final int indiceAgente;
    private final String nombreAgente;
    private final int cantidadIntentos;
    private final InventarioEntradas inventario;
    private final BiConsumer<Integer, ResultadoReservas> notificadorProgreso;
    private volatile int confirmadasPropias;
    private volatile int rechazadasPropias;

    public AgenteReserva(int indiceAgente, String nombreAgente, int cantidadIntentos,
                         InventarioEntradas inventario,
                         BiConsumer<Integer, ResultadoReservas> notificadorProgreso) {
        this.indiceAgente = indiceAgente;
        this.nombreAgente = nombreAgente;
        this.cantidadIntentos = cantidadIntentos;
        this.inventario = inventario;
        this.notificadorProgreso = notificadorProgreso;
    }

    @Override
    public void run() {
        for (int intento = 1; intento <= cantidadIntentos; intento++) {
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
            try {
                Thread.sleep(160);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
            ResultadoReservas resultado = inventario.reservarEntrada(nombreAgente, intento);
            if (resultado.isUltimaReservaConfirmada()) {
                confirmadasPropias++;
            } else {
                rechazadasPropias++;
            }
            if (notificadorProgreso != null) {
                notificadorProgreso.accept(indiceAgente, resultado);
            }
        }
    }

    public int getConfirmadasPropias() { return confirmadasPropias; }
    public int getRechazadasPropias() { return rechazadasPropias; }
    public String getNombreAgente() { return nombreAgente; }
}