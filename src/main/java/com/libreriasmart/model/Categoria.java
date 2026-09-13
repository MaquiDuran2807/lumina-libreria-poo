package com.libreriasmart.model;

/** Categorías de productos de la librería. */
public enum Categoria {
    LIBRO_FICCION("Libro · Ficción"),
    LIBRO_INFANTIL("Libro · Infantil"),
    LIBRO_ACADEMICO("Libro · Académico"),
    UTILES("Útiles Escolares"),
    ARTE("Arte y Manualidades"),
    REVISTA("Revistas");

    private final String etiqueta;

    Categoria(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
