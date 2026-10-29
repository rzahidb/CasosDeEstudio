package org.example.casosdeestudioproyecto.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import org.example.casosdeestudioproyecto.model.ResultadoCaja;
import org.example.casosdeestudioproyecto.services.CajaService;
import org.example.casosdeestudioproyecto.util.ViewNavigator;

import java.text.NumberFormat;
import java.util.Locale;

public class CajaController {

    @FXML
    private Label lblEstado;

    @FXML
    private Label lblTotalVentas;

    @FXML
    private Label lblTotalRecaudado;

    @FXML
    private Label lblVendedor1;

    @FXML
    private Label lblVendedor2;

    @FXML
    private Label lblVendedor3;

    @FXML
    private ProgressBar barraVendedor1;

    @FXML
    private ProgressBar barraVendedor2;

    @FXML
    private ProgressBar barraVendedor3;

    @FXML
    private ProgressBar barraGeneral;

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

    private CajaService cajaService;

    @FXML
    private void initialize() {
        cajaService = new CajaService();
        ViewNavigator.registrarManejadorDetencion(alTerminar ->
                cajaService.detenerYEsperarAsincrono(alTerminar)
        );
        limpiarCampos();
    }

    private void limpiarCampos() {
        if (cajaService != null && cajaService.isEnEjecucion()) {
            agregarRegistro("[AVISO] No se pueden restablecer los campos mientras los vendedores están trabajando.");
            return;
        }

        lblEstado.setText("Listo");
        lblTotalVentas.setText("0 / " + CajaService.TOTAL_VENTAS_ESPERADAS);
        lblTotalRecaudado.setText("C$0");

        lblVendedor1.setText("0 / 5 ventas (C$0)");
        lblVendedor2.setText("0 / 5 ventas (C$0)");
        lblVendedor3.setText("0 / 5 ventas (C$0)");

        barraVendedor1.setProgress(0.0);
        barraVendedor2.setProgress(0.0);
        barraVendedor3.setProgress(0.0);
        barraGeneral.setProgress(0.0);

        txtRegistro.clear();
        txtRegistro.appendText("=== CASO 3: CAJA DE VENTAS CON VENDEDORES CONCURRENTES ===\n");
        txtRegistro.appendText("Configuración: 3 vendedores concurrentes | 5 ventas por vendedor | C$100 por venta.\n");
        txtRegistro.appendText("Meta completa: 15 ventas | Recaudación total: C$1,500.\n\n");

        btnIniciar.setDisable(false);
        btnCancelar.setDisable(true);
        btnLimpiar.setDisable(false);
    }

    @FXML
    private void iniciarSimulacion() {
        if (cajaService.isEnEjecucion()) {
            agregarRegistro("[ADVERTENCIA] Inicio duplicado bloqueado: la simulación ya está en ejecución.");
            return;
        }

        limpiarCampos();
        btnCancelar.setDisable(false);
        btnLimpiar.setDisable(true);
        agregarRegistro("---------- Inicio del proceso de ventas ----------");

        cajaService.iniciarSimulacionVentas(
                estado -> lblEstado.setText(estado),
                this::actualizarAvanceVentas,
                this::finalizarSimulacionVista,
                this::agregarRegistro
        );
    }

    @FXML
    private void cancelarSimulacion() {
        btnCancelar.setDisable(true);
        cajaService.cancelarSimulacionVentas(
                estado -> lblEstado.setText(estado),
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
        lblEstado.setText("Coordinando finalización de vendedores antes de salir...");
        ViewNavigator.cargarMenuPrincipal();
    }

    private void actualizarAvanceVentas(int[] ventasPorVendedor, ResultadoCaja resultadoCaja) {
        if (ventasPorVendedor != null && ventasPorVendedor.length >= 3) {
            actualizarControlVendedor(1, ventasPorVendedor[0], lblVendedor1, barraVendedor1);
            actualizarControlVendedor(2, ventasPorVendedor[1], lblVendedor2, barraVendedor2);
            actualizarControlVendedor(3, ventasPorVendedor[2], lblVendedor3, barraVendedor3);
        }

        int ventasTotales = resultadoCaja.getCantidadVentas();
        int recaudadoTotal = resultadoCaja.getTotalRecaudado();

        lblTotalVentas.setText(ventasTotales + " / " + CajaService.TOTAL_VENTAS_ESPERADAS);
        lblTotalRecaudado.setText(formatearCordobas(recaudadoTotal));
        barraGeneral.setProgress((double) ventasTotales / CajaService.TOTAL_VENTAS_ESPERADAS);

        if (resultadoCaja.getDetalleUltimaVenta() != null && !resultadoCaja.getDetalleUltimaVenta().isEmpty()) {
            agregarRegistro(resultadoCaja.getDetalleUltimaVenta());
        }
    }

    private void actualizarControlVendedor(int numero, int ventas, Label etiqueta, ProgressBar barra) {
        int monto = ventas * CajaService.MONTO_POR_VENTA;
        etiqueta.setText(ventas + " / " + CajaService.VENTAS_POR_VENDEDOR + " ventas (" + formatearCordobas(monto) + ")");
        barra.setProgress((double) ventas / CajaService.VENTAS_POR_VENDEDOR);
    }

    private void finalizarSimulacionVista(String estadoFinal, ResultadoCaja resultadoFinal) {
        btnIniciar.setDisable(false);
        btnCancelar.setDisable(true);
        btnLimpiar.setDisable(false);

        boolean invarianteCumplida =
                resultadoFinal.getTotalRecaudado() == resultadoFinal.getCantidadVentas() * CajaService.MONTO_POR_VENTA;

        agregarRegistro("\n------- Termina proceso de ventas (" + estadoFinal + ") -------");
        agregarRegistro("Cantidad de ventas : " + resultadoFinal.getCantidadVentas());
        agregarRegistro("Total recaudado    : " + formatearCordobas(resultadoFinal.getTotalRecaudado()));
        agregarRegistro("Consistencia atómica (totalRecaudado == cantidadVentas * 100): "
                + (invarianteCumplida ? "CUMPLIDA" : "ERROR"));
    }

    private String formatearCordobas(int monto) {
        NumberFormat formato = NumberFormat.getIntegerInstance(Locale.US);
        return "C$" + formato.format(monto);
    }

    private void agregarRegistro(String linea) {
        txtRegistro.appendText(linea + "\n");
    }
}