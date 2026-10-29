package org.example.casosdeestudioproyecto.services;

import javafx.application.Platform;
import org.example.casosdeestudioproyecto.model.AgenteReserva;
import org.example.casosdeestudioproyecto.model.InventarioEntradas;
import org.example.casosdeestudioproyecto.model.ResultadoReservas;

import java.util.function.Consumer;

/** Administra los tres agentes y espera sus hilos desde un coordinador secundario. */
public class ReservasService {
    public static final int ENTRADAS_INICIALES = 20;
    public static final int CANTIDAD_AGENTES = 3;
    public static final int INTENTOS_POR_AGENTE = 10;
    public static final int SOLICITUDES_TOTALES = CANTIDAD_AGENTES * INTENTOS_POR_AGENTE;

    private final InventarioEntradas inventario = new InventarioEntradas(ENTRADAS_INICIALES);
    private Thread[] hilosAgentes;
    private AgenteReserva[] agentes;
    private boolean enEjecucion;
    private int idSimulacionActual;

    public synchronized boolean isEnEjecucion() {
        return enEjecucion;
    }

    public synchronized boolean iniciarSimulacion(
            Consumer<ResultadoReservas> alActualizarIntento,
            Consumer<String> alActualizarResumenAgentes,
            Consumer<ResultadoReservas> alFinalizarSimulacion,
            Consumer<String> alRechazarInicioDuplicado) {
        if (enEjecucion) {
            if (alRechazarInicioDuplicado != null) {
                Platform.runLater(() -> alRechazarInicioDuplicado.accept(
                        "[ADVERTENCIA] Ya hay una simulación de reservas en curso."));
            }
            return false;
        }

        enEjecucion = true;
        int idSimulacion = ++idSimulacionActual;
        inventario.reiniciarInventario();
        agentes = new AgenteReserva[CANTIDAD_AGENTES];
        hilosAgentes = new Thread[CANTIDAD_AGENTES];

        for (int i = 0; i < CANTIDAD_AGENTES; i++) {
            int indice = i;
            String nombre = "Agente " + (i + 1);
            agentes[i] = new AgenteReserva(indice, nombre, INTENTOS_POR_AGENTE,
                    inventario, (idx, resultado) -> {
                String resumen = construirResumenAgentes();
                Platform.runLater(() -> {
                    if (idSimulacion == obtenerIdSimulacionActual()) {
                        if (alActualizarIntento != null) {
                            alActualizarIntento.accept(resultado);
                        }
                        if (alActualizarResumenAgentes != null) {
                            alActualizarResumenAgentes.accept(resumen);
                        }
                    }
                });
            });
            hilosAgentes[i] = new Thread(agentes[i], "Hilo-" + nombre + "-Sim" + idSimulacion);
        }

        Thread[] hilosEstaEjecucion = hilosAgentes.clone();
        AgenteReserva[] agentesEstaEjecucion = agentes.clone();
        for (Thread hilo : hilosEstaEjecucion) {
            hilo.start();
        }

        Thread coordinador = new Thread(() -> {
            esperarHilos(hilosEstaEjecucion);
            ResultadoReservas resultado = inventario.obtenerEstadoActual();
            String resumen = construirResumenAgentes(agentesEstaEjecucion);
            synchronized (ReservasService.this) {
                if (idSimulacion == idSimulacionActual) {
                    enEjecucion = false;
                }
            }
            Platform.runLater(() -> {
                if (idSimulacion == obtenerIdSimulacionActual()) {
                    if (alActualizarResumenAgentes != null) {
                        alActualizarResumenAgentes.accept(resumen);
                    }
                    if (alFinalizarSimulacion != null) {
                        alFinalizarSimulacion.accept(resultado);
                    }
                }
            });
        }, "Coordinador-Reservas-" + idSimulacion);
        coordinador.start();
        return true;
    }

    /** Cancela y espera sin bloquear el hilo JavaFX. */
    public void detenerYEsperarAsincrono(Runnable alTerminar) {
        Thread[] activos;
        synchronized (this) {
            idSimulacionActual++; // invalida actualizaciones pendientes de la vista anterior
            if (!enEjecucion || hilosAgentes == null) {
                if (alTerminar != null) {
                    alTerminar.run();
                }
                return;
            }
            activos = hilosAgentes.clone();
        }
        for (Thread hilo : activos) {
            if (hilo != null) {
                hilo.interrupt();
            }
        }
        Thread cierre = new Thread(() -> {
            esperarHilos(activos);
            synchronized (ReservasService.this) {
                enEjecucion = false;
            }
            if (alTerminar != null) {
                alTerminar.run();
            }
        }, "Cierre-Reservas");
        cierre.start();
    }

    private static void esperarHilos(Thread[] hilos) {
        boolean interrumpido = false;
        for (Thread hilo : hilos) {
            if (hilo == null) {
                continue;
            }
            boolean termino = false;
            while (!termino) {
                try {
                    hilo.join();
                    termino = true;
                } catch (InterruptedException e) {
                    interrumpido = true;
                }
            }
        }
        if (interrumpido) {
            Thread.currentThread().interrupt();
        }
    }

    private synchronized int obtenerIdSimulacionActual() {
        return idSimulacionActual;
    }

    private String construirResumenAgentes() {
        return construirResumenAgentes(agentes);
    }

    private String construirResumenAgentes(AgenteReserva[] lista) {
        if (lista == null) {
            return "Agente 1: 0 conf. / 0 rech. | Agente 2: 0 conf. / 0 rech. | Agente 3: 0 conf. / 0 rech.";
        }
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < lista.length; i++) {
            AgenteReserva agente = lista[i];
            if (agente != null) {
                if (!texto.isEmpty()) {
                    texto.append("   |   ");
                }
                texto.append(agente.getNombreAgente())
                        .append(": ").append(agente.getConfirmadasPropias())
                        .append(" conf. / ").append(agente.getRechazadasPropias())
                        .append(" rech.");
            }
        }
        return texto.toString();
    }
}
