package org.example.casosdeestudioproyecto.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import org.example.casosdeestudioproyecto.model.EstadoProcesamiento;
import org.example.casosdeestudioproyecto.services.PedidosService;
import org.example.casosdeestudioproyecto.util.ViewNavigator;

public class PedidosController {

    @FXML
    private Label lblEstado;

    @FXML
    private Label lblContadorPedidos;

    @FXML
    private Label lblPorcentaje;

    @FXML
    private ProgressBar barraProgreso;

    @FXML
    private TextArea txtRegistro;

    @FXML
    private Button btnIniciar;

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnLimpiar;

    @FXML
    private Button btnVolverMenu;

    private PedidosService pedidosService;

    @FXML
    private void initialize() {
        pedidosService = new PedidosService();
        ViewNavigator.registrarManejadorDetencion(alTerminar ->
                pedidosService.detenerYEsperarAsincrono(alTerminar)
        );
        limpiarCampos();
    }

    private void limpiarCampos() {
        if (pedidosService != null && pedidosService.isEnEjecucion()) {
            agregarRegistro("[AVISO] No se pueden restablecer los campos mientras el trabajador sigue activo.");
            return;
        }

        actualizarEtiquetaEstado(EstadoProcesamiento.LISTO);
        lblContadorPedidos.setText("0 / " + PedidosService.TOTAL_PEDIDOS);
        lblPorcentaje.setText("0 %");
        barraProgreso.setProgress(0.0);

        txtRegistro.clear();
        txtRegistro.appendText("=== CASO 2: PROCESAMIENTO DE PEDIDOS CON CANCELACIÓN COOPERATIVA ===\n");
        txtRegistro.appendText("Configuración: 1 trabajador en segundo plano | 30 pedidos secuenciales | 200 ms por pedido.\n");
        txtRegistro.appendText("Presione 'Iniciar' para comenzar o 'Cancelar' durante la espera para probar interrupt().\n\n");

        btnIniciar.setDisable(false);
        btnCancelar.setDisable(true);
        btnLimpiar.setDisable(false);
    }

    @FXML
    private void iniciarProcesamiento() {
        if (pedidosService.isEnEjecucion()) {
            agregarRegistro("[ADVERTENCIA] Inicio duplicado bloqueado: ya hay un trabajador ejecutándose.");
            return;
        }

        limpiarCampos();
        btnCancelar.setDisable(false);
        btnLimpiar.setDisable(true);
        agregarRegistro("--- Creando nueva instancia de ProcesadorPedidos y nuevo Thread ---");

        pedidosService.iniciarProcesamiento(
                this::actualizarEtiquetaEstado,
                this::actualizarProgresoPedido,
                this::finalizarEjecucionVista,
                this::agregarRegistro
        );
    }

    @FXML
    private void cancelarProcesamiento() {
        btnCancelar.setDisable(true);
        pedidosService.solicitarCancelacion(
                this::actualizarEtiquetaEstado,
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
        btnCancelar.setDisable(true);
        btnLimpiar.setDisable(true);
        btnVolverMenu.setDisable(true);
        lblEstado.setText("Cancelando y esperando al trabajador antes de salir...");
        ViewNavigator.cargarMenuPrincipal();
    }

    private void actualizarProgresoPedido(int pedidosCompletados, String mensaje) {
        lblContadorPedidos.setText(pedidosCompletados + " / " + PedidosService.TOTAL_PEDIDOS);
        double fraccion = (double) pedidosCompletados / PedidosService.TOTAL_PEDIDOS;
        int porcentaje = (int) Math.round(fraccion * 100);
        barraProgreso.setProgress(fraccion);
        lblPorcentaje.setText(porcentaje + " %");
        agregarRegistro(mensaje);
    }

    private void finalizarEjecucionVista(EstadoProcesamiento estadoFinal, int completadosFinales) {
        lblContadorPedidos.setText(completadosFinales + " / " + PedidosService.TOTAL_PEDIDOS);
        double fraccion = (double) completadosFinales / PedidosService.TOTAL_PEDIDOS;
        int porcentaje = (int) Math.round(fraccion * 100);
        barraProgreso.setProgress(fraccion);
        lblPorcentaje.setText(porcentaje + " %");

        btnIniciar.setDisable(false);
        btnCancelar.setDisable(true);
        btnLimpiar.setDisable(false);

        agregarRegistro("\n=== TRABAJADOR FINALIZADO EFECTIVAMENTE (Confirmado con join asíncrono) ===");
        agregarRegistro("Estado final        : " + estadoFinal.getTextoMostrar());
        agregarRegistro("Pedidos completados : " + completadosFinales + "/" + PedidosService.TOTAL_PEDIDOS);
        agregarRegistro("Progreso registrado : " + porcentaje + " %");
    }

    private void actualizarEtiquetaEstado(EstadoProcesamiento estado) {
        lblEstado.setText(estado.getTextoMostrar());
    }

    private void agregarRegistro(String mensaje) {
        txtRegistro.appendText(mensaje + "\n");
    }
}