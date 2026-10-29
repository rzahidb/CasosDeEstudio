package org.example.casosdeestudioproyecto;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.casosdeestudioproyecto.util.ViewNavigator;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) {
        ViewNavigator.configurarStagePrincipal(stage);
        ViewNavigator.cargarMenuPrincipal();
        stage.show();
    }
}