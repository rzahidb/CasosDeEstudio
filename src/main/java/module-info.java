module org.example.casosdeestudioproyecto {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.casosdeestudioproyecto to javafx.fxml;
    exports org.example.casosdeestudioproyecto;
}