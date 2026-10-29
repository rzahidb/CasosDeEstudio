package org.example.casosdeestudioproyecto.model;

/** Instantanea inmutable de una caja sincronizada. */
public class ResultadoCaja {
    private final int cantidadVentas;
    private final int totalRecaudado;
    private final String detalleUltimaVenta;

    public ResultadoCaja(int cantidadVentas, int totalRecaudado, String detalleUltimaVenta) {
        this.cantidadVentas = cantidadVentas;
        this.totalRecaudado = totalRecaudado;
        this.detalleUltimaVenta = detalleUltimaVenta;
    }

    public int getCantidadVentas() { return cantidadVentas; }
    public int getTotalRecaudado() { return totalRecaudado; }
    public String getDetalleUltimaVenta() { return detalleUltimaVenta; }
}
