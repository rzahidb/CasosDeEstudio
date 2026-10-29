package org.example.casosdeestudioproyecto.model;

import org.example.casosdeestudioproyecto.model.Caja;
import org.example.casosdeestudioproyecto.model.ResultadoCaja;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/** Vendedor que registra hasta cinco ventas en una caja compartida. */
public class Vendedor implements Runnable {
    private final int indiceVendedor;
    private final String nombreVendedor;
    private final int metaVentas;
    private final int montoPorVenta;
    private final Caja cajaCompartida;
    private final BiConsumer<Integer, ResultadoCaja> alRegistrarVenta;
    private final Consumer<String> alInterrumpirVendedor;
    private volatile int ventasRealizadas;

    public Vendedor(int indiceVendedor, String nombreVendedor, int metaVentas,
                    int montoPorVenta, Caja cajaCompartida,
                    BiConsumer<Integer, ResultadoCaja> alRegistrarVenta,
                    Consumer<String> alInterrumpirVendedor) {
        this.indiceVendedor = indiceVendedor;
        this.nombreVendedor = nombreVendedor;
        this.metaVentas = metaVentas;
        this.montoPorVenta = montoPorVenta;
        this.cajaCompartida = cajaCompartida;
        this.alRegistrarVenta = alRegistrarVenta;
        this.alInterrumpirVendedor = alInterrumpirVendedor;
    }

    @Override
    public void run() {
        for (int numero = 1; numero <= metaVentas; numero++) {
            if (Thread.currentThread().isInterrupted()) {
                notificarInterrupcion(nombreVendedor + " fue interrumpido antes de la venta #" + numero);
                return;
            }
            try {
                Thread.sleep(450);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                notificarInterrupcion(nombreVendedor + " fue interrumpido durante la venta #" + numero);
                return;
            }
            if (Thread.currentThread().isInterrupted()) {
                notificarInterrupcion(nombreVendedor + " se detuvo antes de registrar la venta #" + numero);
                return;
            }
            ResultadoCaja resultado = cajaCompartida.registrarVentas(nombreVendedor, montoPorVenta);
            ventasRealizadas++;
            if (alRegistrarVenta != null) {
                alRegistrarVenta.accept(indiceVendedor, resultado);
            }
        }
    }

    private void notificarInterrupcion(String mensaje) {
        System.out.println(mensaje);
        if (alInterrumpirVendedor != null) {
            alInterrumpirVendedor.accept(mensaje);
        }
    }

    public int getVentasRealizadas() { return ventasRealizadas; }
    public int getIndiceVendedor() { return indiceVendedor; }
    public String getNombreVendedor() { return nombreVendedor; }
}