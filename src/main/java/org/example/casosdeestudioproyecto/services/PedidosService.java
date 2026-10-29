package org.example.casosdeestudioproyecto.services;

import javafx.application.Platform;
import org.example.casosdeestudioproyecto.model.EstadoProcesamiento;
import org.example.casosdeestudioproyecto.model.ProcesadorPedidos;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/** Controla un trabajador secuencial y su cancelacion cooperativa. */
public class PedidosService {
    public static final int TOTAL_PEDIDOS = 30;
    public static final long DURACION_PEDIDO_MS = 200L;

    private Thread hiloTrabajador;
    private ProcesadorPedidos procesadorActual;
    private EstadoProcesamiento estadoActual = EstadoProcesamiento.LISTO;
    private boolean enEjecucion;
    private int idEjecucionActual;

    public synchronized boolean isEnEjecucion() { return enEjecucion; }
    public synchronized EstadoProcesamiento getEstadoActual() { return estadoActual; }

    public synchronized boolean iniciarProcesamiento(
            Consumer<EstadoProcesamiento> alCambiarEstado,
            BiConsumer<Integer, String> alCompletarPedido,
            BiConsumer<EstadoProcesamiento, Integer> alFinalizarTrabajador,
            Consumer<String> alRegistrarMensaje) {
        if (enEjecucion) {
            notificarMensaje(alRegistrarMensaje,
                    "[ADVERTENCIA] Inicio duplicado bloqueado: ya existe un trabajador activo.");
            return false;
        }

        enEjecucion = true;
        estadoActual = EstadoProcesamiento.EN_EJECUCION;
        int id = ++idEjecucionActual;
        Platform.runLater(() -> {
            if (id == obtenerIdEjecucionActual() && alCambiarEstado != null) {
                alCambiarEstado.accept(EstadoProcesamiento.EN_EJECUCION);
            }
        });

        ProcesadorPedidos procesador = new ProcesadorPedidos(TOTAL_PEDIDOS, DURACION_PEDIDO_MS,
                (completados, mensaje) -> Platform.runLater(() -> {
                    if (id == obtenerIdEjecucionActual() && alCompletarPedido != null) {
                        alCompletarPedido.accept(completados, mensaje);
                    }
                }),
                detalle -> Platform.runLater(() -> {
                    if (id == obtenerIdEjecucionActual() && alRegistrarMensaje != null) {
                        alRegistrarMensaje.accept("[CANCELACIÓN COOPERATIVA] " + detalle);
                    }
                }),
                detalle -> Platform.runLater(() -> {
                    if (id == obtenerIdEjecucionActual() && alRegistrarMensaje != null) {
                        alRegistrarMensaje.accept("[ERROR] " + detalle);
                    }
                }));

        procesadorActual = procesador;
        Thread trabajador = new Thread(procesador, "Hilo-ProcesadorPedidos-" + id);
        hiloTrabajador = trabajador;
        trabajador.start();

        Thread coordinador = new Thread(() -> {
            esperarHilo(trabajador);
            int completados = procesador.getPedidosCompletados();
            EstadoProcesamiento estadoFinal;
            synchronized (PedidosService.this) {
                if (procesador.isErrorOcurrido()) {
                    estadoFinal = EstadoProcesamiento.ERROR;
                } else if (completados == TOTAL_PEDIDOS) {
                    estadoFinal = EstadoProcesamiento.COMPLETADO;
                } else if (procesador.isCancelado() || estadoActual == EstadoProcesamiento.CANCELANDO) {
                    estadoFinal = EstadoProcesamiento.CANCELADO;
                } else {
                    estadoFinal = EstadoProcesamiento.ERROR;
                }
                if (id == idEjecucionActual) {
                    estadoActual = estadoFinal;
                    enEjecucion = false;
                }
            }
            Platform.runLater(() -> {
                if (id == obtenerIdEjecucionActual()) {
                    if (alCambiarEstado != null) {
                        alCambiarEstado.accept(estadoFinal);
                    }
                    if (alFinalizarTrabajador != null) {
                        alFinalizarTrabajador.accept(estadoFinal, completados);
                    }
                }
            });
        }, "Coordinador-Pedidos-" + id);
        coordinador.start();
        return true;
    }

    public synchronized boolean solicitarCancelacion(
            Consumer<EstadoProcesamiento> alCambiarEstado,
            Consumer<String> alRegistrarMensaje) {
        if (!enEjecucion || hiloTrabajador == null) {
            notificarMensaje(alRegistrarMensaje, "[AVISO] No hay un procesamiento activo para cancelar.");
            return false;
        }
        if (estadoActual == EstadoProcesamiento.CANCELANDO) {
            notificarMensaje(alRegistrarMensaje, "[AVISO] La cancelación ya está en curso.");
            return false;
        }
        if (procesadorActual != null && procesadorActual.getPedidosCompletados() >= TOTAL_PEDIDOS) {
            notificarMensaje(alRegistrarMensaje, "[AVISO] Los 30 pedidos ya terminaron.");
            return false;
        }
        int id = idEjecucionActual;
        estadoActual = EstadoProcesamiento.CANCELANDO;
        hiloTrabajador.interrupt();
        Platform.runLater(() -> {
            if (id == obtenerIdEjecucionActual()) {
                if (alCambiarEstado != null) {
                    alCambiarEstado.accept(EstadoProcesamiento.CANCELANDO);
                }
                if (alRegistrarMensaje != null) {
                    alRegistrarMensaje.accept("[SOLICITUD] Se envió interrupt(). Esperando terminación real...");
                }
            }
        });
        return true;
    }

    public void detenerYEsperarAsincrono(Runnable alTerminar) {
        Thread activo;
        synchronized (this) {
            idEjecucionActual++; // ignora callbacks anteriores si se cierra la vista
            if (!enEjecucion || hiloTrabajador == null) {
                if (alTerminar != null) {
                    alTerminar.run();
                }
                return;
            }
            estadoActual = EstadoProcesamiento.CANCELANDO;
            activo = hiloTrabajador;
        }
        activo.interrupt();
        Thread cierre = new Thread(() -> {
            esperarHilo(activo);
            synchronized (PedidosService.this) {
                enEjecucion = false;
                estadoActual = EstadoProcesamiento.CANCELADO;
            }
            if (alTerminar != null) {
                alTerminar.run();
            }
        }, "Cierre-Pedidos");
        cierre.start();
    }

    private static void esperarHilo(Thread hilo) {
        boolean interrumpido = false;
        boolean terminado = false;
        while (!terminado) {
            try {
                hilo.join();
                terminado = true;
            } catch (InterruptedException e) {
                interrumpido = true;
            }
        }
        if (interrumpido) {
            Thread.currentThread().interrupt();
        }
    }

    private static void notificarMensaje(Consumer<String> consumidor, String mensaje) {
        if (consumidor != null) {
            Platform.runLater(() -> consumidor.accept(mensaje));
        }
    }

    private synchronized int obtenerIdEjecucionActual() { return idEjecucionActual; }
}
