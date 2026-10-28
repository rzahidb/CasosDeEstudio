package org.example.casosdeestudioproyecto.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import org.example.casosdeestudioproyecto.model.ResultadoReservas;
import org.example.casosdeestudioproyecto.services.ReservasService;
import org.example.casosdeestudioproyecto.util.ViewNavigator;

public class ReservasController {

    @FXML
    private Label lblEstado;

    @FXML
    private Label lblEntradasIniciales;

    @FXML
    private Label lblEntradasDisponibles;

    @FXML
    private Label lblReservasConfirmadas;

    @FXML
    private Label lblSolicitudesRechazadas;

    @FXML
    private Label lblTotalSolicitudes;

    @FXML
    private Label lblResumenAgentes;

    @FXML
    private Label lblContadorPruebas;

    @FXML
    private ProgressBar barraProgreso;

    @FXML
    private TextArea txtRegistro;

    @FXML
    private Button btnIniciar;

    @FXML
    private Button btnLimpiar;

    @FXML
    private Button btnVolverMenu;

    private ReservasService reservasService;
    private int simulacionesCompletadas = 0;

    @FXML
    private void initialize() {
        reservasService = new ReservasService();
        ViewNavigator.registrarManejadorDetencion(alTerminar ->
                reservasService.detenerYEsperarAsincrono(alTerminar)
        );
        lblContadorPruebas.setText("Simulaciones completadas en esta sesión: 0");
        limpiarCampos();
    }

    private void limpiarCampos() {
        if (reservasService != null && reservasService.isEnEjecucion()) {
            agregarRegistro("[AVISO] No se pueden limpiar los campos mientras una simulación está en ejecución.");
            return;
        }

        lblEstado.setText("Listo");
        lblEntradasIniciales.setText(String.valueOf(ReservasService.ENTRADAS_INICIALES));
        lblEntradasDisponibles.setText(String.valueOf(ReservasService.ENTRADAS_INICIALES));
        lblReservasConfirmadas.setText("0");
        lblSolicitudesRechazadas.setText("0");
        lblTotalSolicitudes.setText("0 / " + ReservasService.SOLICITUDES_TOTALES);
        lblResumenAgentes.setText("Agente 1: 0 conf. / 0 rech.   |   Agente 2: 0 conf. / 0 rech.   |   Agente 3: 0 conf. / 0 rech.");
        barraProgreso.setProgress(0.0);
        txtRegistro.clear();
        txtRegistro.appendText("=== CASO 1: RESERVA CONCURRENTE DE ENTRADAS ===\n");
        txtRegistro.appendText("Configuración: 20 entradas iniciales | 3 agentes concurrentes | 10 intentos por agente (30 solicitudes).\n");
        txtRegistro.appendText("Presione 'Iniciar Simulación' para ejecutar los hilos trabajadores.\n\n");

        btnIniciar.setDisable(false);
        btnLimpiar.setDisable(false);
    }

    @FXML
    private void iniciarSimulacion() {
        if (reservasService.isEnEjecucion()) {
            agregarRegistro("[ADVERTENCIA] Intento de inicio duplicado bloqueado: la simulación ya está activa.");
            return;
        }

        limpiarCampos();
        lblEstado.setText("En ejecución (3 agentes concurrentes)");
        btnLimpiar.setDisable(true);
        agregarRegistro("--- Iniciando 3 hilos (Agente 1, Agente 2, Agente 3) ---");

        reservasService.iniciarSimulacion(
                this::actualizarVistaParcial,
                resumenAgentes -> lblResumenAgentes.setText(resumenAgentes),
                this::finalizarSimulacionVista,
                this::agregarRegistro
        );
    }

    @FXML
    private void accionLimpiarCampos() {
        limpiarCampos();
    }

    @FXML
    private void volverAlMenu() {
        btnIniciar.setDisable(true);
        btnLimpiar.setDisable(true);
        btnVolverMenu.setDisable(true);
        lblEstado.setText("Cerrando hilos activos antes de volver al menú...");
        ViewNavigator.cargarMenuPrincipal();
    }

    private void actualizarVistaParcial(ResultadoReservas resultado) {
        lblEntradasDisponibles.setText(String.valueOf(resultado.getEntradasDisponibles()));
        lblReservasConfirmadas.setText(String.valueOf(resultado.getReservasConfirmadas()));
        lblSolicitudesRechazadas.setText(String.valueOf(resultado.getSolicitudesRechazadas()));

        int procesadas = resultado.getTotalProcesadas();
        lblTotalSolicitudes.setText(procesadas + " / " + ReservasService.SOLICITUDES_TOTALES);
        barraProgreso.setProgress((double) procesadas / ReservasService.SOLICITUDES_TOTALES);

        agregarRegistro(resultado.getDetalleOperacion());
    }

    private void finalizarSimulacionVista(ResultadoReservas resultadoFinal) {
        simulacionesCompletadas++;
        lblEntradasDisponibles.setText(String.valueOf(resultadoFinal.getEntradasDisponibles()));
        lblReservasConfirmadas.setText(String.valueOf(resultadoFinal.getReservasConfirmadas()));
        lblSolicitudesRechazadas.setText(String.valueOf(resultadoFinal.getSolicitudesRechazadas()));
        lblTotalSolicitudes.setText(resultadoFinal.getTotalProcesadas() + " / " + ReservasService.SOLICITUDES_TOTALES);
        barraProgreso.setProgress(1.0);

        boolean invarianteCumplida =
                (resultadoFinal.getEntradasDisponibles() + resultadoFinal.getReservasConfirmadas() == ReservasService.ENTRADAS_INICIALES)
                        && resultadoFinal.getEntradasDisponibles() >= 0;

        lblEstado.setText("Completado");
        lblContadorPruebas.setText("Simulaciones completadas en esta sesión: " + simulacionesCompletadas);
        btnIniciar.setDisable(false);
        btnLimpiar.setDisable(false);

        agregarRegistro("\n=== SIMULACIÓN #" + simulacionesCompletadas + " FINALIZADA ===");
        agregarRegistro("Entradas iniciales   : " + resultadoFinal.getEntradasIniciales());
        agregarRegistro("Reservas confirmadas : " + resultadoFinal.getReservasConfirmadas());
        agregarRegistro("Solicitudes rechazadas: " + resultadoFinal.getSolicitudesRechazadas());
        agregarRegistro("Entradas disponibles : " + resultadoFinal.getEntradasDisponibles());
        agregarRegistro("Verificación atómica (disponibles + confirmadas == 20): " + (invarianteCumplida ? "CUMPLIDA" : "ERROR"));
    }

    private void agregarRegistro(String linea) {
        txtRegistro.appendText(linea + "\n");
    }
}