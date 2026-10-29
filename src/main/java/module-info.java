module org.example.casosdeestudioproyecto {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.example.casosdeestudioproyecto to javafx.fxml;
    opens org.example.casosdeestudioproyecto.controller to javafx.fxml;

    exports org.example.casosdeestudioproyecto;
    exports org.example.casosdeestudioproyecto.controllers;
    exports org.example.casosdeestudioproyecto.model;
    exports org.example.casosdeestudioproyecto.services;
    exports org.example.casosdeestudioproyecto.util;
}
