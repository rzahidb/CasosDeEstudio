package org.example.casosdeestudioproyecto.util;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;

public class ViewNavigator {

    public static final String VISTA_MENU = "/org/example/concurrenciaproyecto/views/menu-view.fxml";
    public static final String VISTA_RESERVAS = "/org/example/concurrenciaproyecto/views/reservas-view.fxml";
    public static final String VISTA_PEDIDOS = "/org/example/concurrenciaproyecto/views/pedidos-view.fxml";
    public static final String VISTA_CAJA = "/org/example/concurrenciaproyecto/views/caja-view.fxml";

    private static Stage stagePrincipal;
    private static Consumer<Runnable> manejadorDetencionActiva;
    private static boolean cerrandoVentana = false;

    private ViewNavigator() {
    }

    public static void configurarStagePrincipal(Stage stage) {
        stagePrincipal = stage;
        stagePrincipal.setTitle("UAM - Programación de Aplicaciones de Escritorio - Proyecto de Concurrencia");
        stagePrincipal.setMinWidth(860);
        stagePrincipal.setMinHeight(640);

        stagePrincipal.setOnCloseRequest(evento -> {
            if (cerrandoVentana) {
                evento.consume();
                return;
            }
            if (manejadorDetencionActiva != null) {
                evento.consume();
                cerrandoVentana = true;
                Consumer<Runnable> manejador = manejadorDetencionActiva;
                manejadorDetencionActiva = null;
                manejador.accept(() -> Platform.runLater(() -> {
                    stagePrincipal.close();
                    Platform.exit();
                }));
            } else {
                Platform.exit();
            }
        });
    }

    public static void registrarManejadorDetencion(Consumer<Runnable> manejador) {
        manejadorDetencionActiva = manejador;
    }

    public static void limpiarManejadorDetencion() {
        manejadorDetencionActiva = null;
    }

    public static void cargarMenuPrincipal() {
        cambiarVista(VISTA_MENU, "Menú Principal - Proyecto de Concurrencia");
    }

    public static void cargarVista(String rutaFxml, String tituloVentana) {
        if (manejadorDetencionActiva != null) {
            Consumer<Runnable> manejadorAnterior = manejadorDetencionActiva;
            manejadorDetencionActiva = null;
            manejadorAnterior.accept(() -> Platform.runLater(() -> cambiarVista(rutaFxml, tituloVentana)));
        } else {
            cambiarVista(rutaFxml, tituloVentana);
        }
    }

    public static void cerrarAplicacionSegura() {
        if (cerrandoVentana) {
            return;
        }
        cerrandoVentana = true;
        if (manejadorDetencionActiva != null) {
            Consumer<Runnable> manejador = manejadorDetencionActiva;
            manejadorDetencionActiva = null;
            manejador.accept(() -> Platform.runLater(() -> {
                if (stagePrincipal != null) {
                    stagePrincipal.close();
                }
                Platform.exit();
            }));
        } else {
            if (stagePrincipal != null) {
                stagePrincipal.close();
            }
            Platform.exit();
        }
    }

    private static void cambiarVista(String rutaFxml, String tituloVentana) {
        try {
            URL recurso = ViewNavigator.class.getResource(rutaFxml);
            if (recurso == null) {
                mostrarError("No se encontró el archivo FXML: " + rutaFxml);
                return;
            }

            FXMLLoader loader = new FXMLLoader(recurso);
            Parent raiz = loader.load();

            if (stagePrincipal.getScene() == null) {
                Scene escena = new Scene(raiz, 900, 660);
                stagePrincipal.setScene(escena);
            } else {
                stagePrincipal.getScene().setRoot(raiz);
            }

            stagePrincipal.setTitle("UAM | " + tituloVentana);
        } catch (IOException e) {
            mostrarError("Error al cargar la vista " + rutaFxml + ": " + e.getMessage());
        }
    }

    private static void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error de Navegación");
        alerta.setHeaderText("No se pudo abrir la pantalla solicitada");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}