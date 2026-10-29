package org.example.casosdeestudioproyecto.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.example.casosdeestudioproyecto.util.ViewNavigator;

public class MenuController {

    @FXML
    private Label lblSubtitulo;

    @FXML
    private Label lblEstadoMenu;

    @FXML
    private Button btnCaso1;

    @FXML
    private Button btnCaso2;

    @FXML
    private Button btnCaso3;

    @FXML
    private Button btnSalir;

    @FXML
    private void initialize() {
        ViewNavigator.limpiarManejadorDetencion();
        limpiarCampos();
    }

    private void limpiarCampos() {
        lblSubtitulo.setText("Evaluaciones Integradas de Concurrencia en JavaFX (Java 21)");
        lblEstadoMenu.setText("Estado del sistema: Listo. Seleccione un caso de estudio para iniciar.");
        btnCaso1.setDisable(false);
        btnCaso2.setDisable(false);
        btnCaso3.setDisable(false);
        btnSalir.setDisable(false);
    }

    @FXML
    private void abrirCaso1Reservas() {
        lblEstadoMenu.setText("Cargando Caso 1: Reserva Concurrente de Entradas...");
        ViewNavigator.cargarVista(ViewNavigator.VISTA_RESERVAS, "Caso 1 - Reserva Concurrente de Entradas");
    }

    @FXML
    private void abrirCaso2Pedidos() {
        lblEstadoMenu.setText("Cargando Caso 2: Procesamiento de Pedidos con Cancelación...");
        ViewNavigator.cargarVista(ViewNavigator.VISTA_PEDIDOS, "Caso 2 - Procesamiento de Pedidos con Cancelación");
    }

    @FXML
    private void abrirCaso3Caja() {
        lblEstadoMenu.setText("Cargando Caso 3: Caja de Ventas con Vendedores Concurrentes...");
        ViewNavigator.cargarVista(ViewNavigator.VISTA_CAJA, "Caso 3 - Caja de Ventas con Vendedores Concurrentes");
    }

    @FXML
    private void salirAplicacion() {
        lblEstadoMenu.setText("Cerrando aplicación de forma segura...");
        ViewNavigator.cerrarAplicacionSegura();
    }
}