package org.example.casosdeestudioproyecto.services;

import javafx.application.Platform;
import org.example.casosdeestudioproyecto.model.Caja;
import org.example.casosdeestudioproyecto.model.ResultadoCaja;
import org.example.casosdeestudioproyecto.model.Vendedor;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/** Crea tres vendedores que comparten una sola Caja sincronizada. */
public class CajaService {
    public static final int CANTIDAD_VENDEDORES = 3;
    public static final int VENTAS_POR_VENDEDOR = 5;
    public static final int MONTO_POR_VENTA = 100;
    public static final int TOTAL_VENTAS_ESPERADAS = CANTIDAD_VENDEDORES * VENTAS_POR_VENDEDOR;

    private final Caja caja = new Caja();
    private Vendedor[] vendedores;
    private Thread[] hilosVendedores;
    private boolean enEjecucion;
    private boolean cancelacionSolicitada;
    private int idSimulacionActual;

    public synchronized boolean isEnEjecucion() { return enEjecucion; }

    public synchronized boolean iniciarSimulacionVentas(
            Consumer<String> alCambiarEstado,
            BiConsumer<int[], ResultadoCaja> alActualizarVenta,
            BiConsumer<String, ResultadoCaja> alFinalizarSimulacion,
            Consumer<String> alRegistrarMensaje) {
        if (enEjecucion) {
            if (alRegistrarMensaje != null) {
                Platform.runLater(() -> alRegistrarMensaje.accept(
                        "[ADVERTENCIA] Inicio duplicado bloqueado: los vendedores siguen activos."));
            }
            return false;
        }

        enEjecucion = true;
        cancelacionSolicitada = false;
        int id = ++idSimulacionActual;
        caja.reiniciarCaja();
        vendedores = new Vendedor[CANTIDAD_VENDEDORES];
        hilosVendedores = new Thread[CANTIDAD_VENDEDORES];

        Platform.runLater(() -> {
            if (id == obtenerIdSimulacionActual() && alCambiarEstado != null) {
                alCambiarEstado.accept("En ejecución");
            }
        });

        for (int i = 0; i < CANTIDAD_VENDEDORES; i++) {
            int indice = i;
            String nombre = "Vendedor " + (i + 1);
            vendedores[i] = new Vendedor(indice, nombre, VENTAS_POR_VENDEDOR,
                    MONTO_POR_VENTA, caja,
                    (idx, resultado) -> {
                        int[] totalesIndividuales = obtenerVentasPorVendedor();
                        Platform.runLater(() -> {
                            if (id == obtenerIdSimulacionActual() && alActualizarVenta != null) {
                                alActualizarVenta.accept(totalesIndividuales, resultado);
                            }
                        });
                    },
                    mensaje -> Platform.runLater(() -> {
                        if (id == obtenerIdSimulacionActual() && alRegistrarMensaje != null) {
                            alRegistrarMensaje.accept("[INTERRUPCIÓN] " + mensaje);
                        }
                    }));
            hilosVendedores[i] = new Thread(vendedores[i], "Hilo-" + nombre + "-Sim" + id);
        }

        Thread[] hilos = hilosVendedores.clone();
        Vendedor[] lista = vendedores.clone();
        for (Thread hilo : hilos) {
            hilo.start();
        }

        Thread coordinador = new Thread(() -> {
            esperarHilos(hilos);
            ResultadoCaja resultado = caja.obtenerInstantanea();
            int[] resultadosIndividuales = obtenerVentasPorVendedor(lista);
            String estadoFinal;
            synchronized (CajaService.this) {
                if (resultado.getCantidadVentas() == TOTAL_VENTAS_ESPERADAS) {
                    estadoFinal = "Completado";
                } else if (cancelacionSolicitada) {
                    estadoFinal = "Cancelado";
                } else {
                    estadoFinal = "Error";
                }
                if (id == idSimulacionActual) {
                    enEjecucion = false;
                    cancelacionSolicitada = false;
                }
            }
            Platform.runLater(() -> {
                if (id == obtenerIdSimulacionActual()) {
                    if (alActualizarVenta != null) {
                        alActualizarVenta.accept(resultadosIndividuales,
                                new ResultadoCaja(resultado.getCantidadVentas(),
                                        resultado.getTotalRecaudado(), ""));
                    }
                    if (alCambiarEstado != null) {
                        alCambiarEstado.accept(estadoFinal);
                    }
                    if (alFinalizarSimulacion != null) {
                        alFinalizarSimulacion.accept(estadoFinal, resultado);
                    }
                }
            });
        }, "Coordinador-Caja-" + id);
        coordinador.start();
        return true;
    }

    public synchronized boolean cancelarSimulacionVentas(
            Consumer<String> alCambiarEstado, Consumer<String> alRegistrarMensaje) {
        if (!enEjecucion || hilosVendedores == null) {
            mensaje(alRegistrarMensaje, "[AVISO] No hay una simulación activa para cancelar.");
            return false;
        }
        if (cancelacionSolicitada) {
            mensaje(alRegistrarMensaje, "[AVISO] La cancelación ya está en curso.");
            return false;
        }
        cancelacionSolicitada = true;
        int id = idSimulacionActual;
        for (Thread hilo : hilosVendedores) {
            if (hilo != null) {
                hilo.interrupt();
            }
        }
        Platform.runLater(() -> {
            if (id == obtenerIdSimulacionActual()) {
                if (alCambiarEstado != null) {
                    alCambiarEstado.accept("Cancelando");
                }
                if (alRegistrarMensaje != null) {
                    alRegistrarMensaje.accept("[SOLICITUD] Se envió interrupt() a los 3 vendedores.");
                }
            }
        });
        return true;
    }

    public void detenerYEsperarAsincrono(Runnable alTerminar) {
        Thread[] activos;
        synchronized (this) {
            idSimulacionActual++;
            if (!enEjecucion || hilosVendedores == null) {
                if (alTerminar != null) {
                    alTerminar.run();
                }
                return;
            }
            cancelacionSolicitada = true;
            activos = hilosVendedores.clone();
        }
        for (Thread hilo : activos) {
            if (hilo != null) {
                hilo.interrupt();
            }
        }
        Thread cierre = new Thread(() -> {
            esperarHilos(activos);
            synchronized (CajaService.this) {
                enEjecucion = false;
                cancelacionSolicitada = false;
            }
            if (alTerminar != null) {
                alTerminar.run();
            }
        }, "Cierre-Caja");
        cierre.start();
    }

    private static void esperarHilos(Thread[] hilos) {
        boolean interrumpido = false;
        for (Thread hilo : hilos) {
            if (hilo == null) {
                continue;
            }
            boolean terminado = false;
            while (!terminado) {
                try {
                    hilo.join();
                    terminado = true;
                } catch (InterruptedException e) {
                    interrumpido = true;
                }
            }
        }
        if (interrumpido) {
            Thread.currentThread().interrupt();
        }
    }

    private int[] obtenerVentasPorVendedor() {
        return obtenerVentasPorVendedor(vendedores);
    }

    private static int[] obtenerVentasPorVendedor(Vendedor[] lista) {
        int[] conteos = new int[CANTIDAD_VENDEDORES];
        if (lista != null) {
            for (int i = 0; i < Math.min(lista.length, conteos.length); i++) {
                if (lista[i] != null) {
                    conteos[i] = lista[i].getVentasRealizadas();
                }
            }
        }
        return conteos;
    }

    private static void mensaje(Consumer<String> destino, String texto) {
        if (destino != null) {
            Platform.runLater(() -> destino.accept(texto));
        }
    }

    private synchronized int obtenerIdSimulacionActual() { return idSimulacionActual; }
}
