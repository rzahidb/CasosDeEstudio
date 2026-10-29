package org.example.casosdeestudioproyecto.model;

public enum EstadoProcesamiento {
    LISTO("Listo"),
    EN_EJECUCION("En ejecución"),
    CANCELANDO("Cancelando"),
    CANCELADO("Cancelado"),
    COMPLETADO("Completado"),
    ERROR("Error");

    private final String textoMostrar;

    EstadoProcesamiento(String textoMostrar) {
        this.textoMostrar = textoMostrar;
    }

    public String getTextoMostrar() { return textoMostrar; }

    @Override
    public String toString() { return textoMostrar; }
}