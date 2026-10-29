package org.example.casosdeestudioproyecto.model;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/** Procesa los pedidos uno por uno en un solo hilo secundario. */
public class ProcesadorPedidos implements Runnable {
    private final int totalPedidos;
    private final long duracionPorPedidoMs;
    private final BiConsumer<Integer, String> alCompletarPedido;
    private final Consumer<String> alCancelarEnPuntoSeguro;
    private final Consumer<String> alOcurrirError;
    private volatile int pedidosCompletados;
    private volatile boolean cancelado;
    private volatile boolean errorOcurrido;

    public ProcesadorPedidos(int totalPedidos, long duracionPorPedidoMs,
                             BiConsumer<Integer, String> alCompletarPedido,
                             Consumer<String> alCancelarEnPuntoSeguro,
                             Consumer<String> alOcurrirError) {
        this.totalPedidos = totalPedidos;
        this.duracionPorPedidoMs = duracionPorPedidoMs;
        this.alCompletarPedido = alCompletarPedido;
        this.alCancelarEnPuntoSeguro = alCancelarEnPuntoSeguro;
        this.alOcurrirError = alOcurrirError;
    }

    @Override
    public void run() {
        try {
            for (int numeroPedido = 1; numeroPedido <= totalPedidos; numeroPedido++) {
                if (Thread.currentThread().isInterrupted()) {
                    marcarCancelado("Interrupción detectada antes del pedido #" + numeroPedido);
                    return;
                }
                try {
                    Thread.sleep(duracionPorPedidoMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    marcarCancelado("Pedido #" + numeroPedido + " interrumpido; no se contabiliza.");
                    return;
                }
                if (Thread.currentThread().isInterrupted()) {
                    marcarCancelado("Interrupción antes de confirmar el pedido #" + numeroPedido);
                    return;
                }
                pedidosCompletados++;
                String mensaje = String.format(
                        "[COMPLETADO] Pedido #%02d procesado (%d ms) | Avance: %d/%d",
                        numeroPedido, duracionPorPedidoMs, pedidosCompletados, totalPedidos);
                System.out.println(mensaje);
                if (alCompletarPedido != null) {
                    alCompletarPedido.accept(pedidosCompletados, mensaje);
                }
            }
        } catch (Exception e) {
            errorOcurrido = true;
            if (alOcurrirError != null) {
                alOcurrirError.accept("Error inesperado en el procesador: " + e.getMessage());
            }
        }
    }

    private void marcarCancelado(String mensaje) {
        cancelado = true;
        System.out.println("[CANCELADO] " + mensaje);
        if (alCancelarEnPuntoSeguro != null) {
            alCancelarEnPuntoSeguro.accept(mensaje);
        }
    }

    public int getPedidosCompletados() { return pedidosCompletados; }
    public boolean isCancelado() { return cancelado; }
    public boolean isErrorOcurrido() { return errorOcurrido; }
}
