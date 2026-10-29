package org.example.casosdeestudioproyecto.model;

/** Copia inmutable del estado del inventario despues de una solicitud. */
public class ResultadoReservas {
    private final int entradasIniciales;
    private final int entradasDisponibles;
    private final int reservasConfirmadas;
    private final int solicitudesRechazadas;
    private final boolean ultimaReservaConfirmada;
    private final String detalleOperacion;

    public ResultadoReservas(int entradasIniciales, int entradasDisponibles,
                             int reservasConfirmadas, int solicitudesRechazadas,
                             boolean ultimaReservaConfirmada, String detalleOperacion) {
        this.entradasIniciales = entradasIniciales;
        this.entradasDisponibles = entradasDisponibles;
        this.reservasConfirmadas = reservasConfirmadas;
        this.solicitudesRechazadas = solicitudesRechazadas;
        this.ultimaReservaConfirmada = ultimaReservaConfirmada;
        this.detalleOperacion = detalleOperacion;
    }

    public int getEntradasIniciales() { return entradasIniciales; }
    public int getEntradasDisponibles() { return entradasDisponibles; }
    public int getReservasConfirmadas() { return reservasConfirmadas; }
    public int getSolicitudesRechazadas() { return solicitudesRechazadas; }
    public int getTotalProcesadas() { return reservasConfirmadas + solicitudesRechazadas; }
    public boolean isUltimaReservaConfirmada() { return ultimaReservaConfirmada; }
    public String getDetalleOperacion() { return detalleOperacion; }
}