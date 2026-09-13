package com.libreriasmart.model;

/** HERENCIA: Libro es-un Producto con autor/editorial/ISBN. */
public class Libro extends Producto {
    private String autor;
    private String editorial;
    private String isbn;
    private int numPaginas;

    public Libro(String id, String nombre, Categoria categoria,
                 double precioCompra, double precioVenta,
                 int stock, int stockMinimo,
                 String autor, String editorial, String isbn, int numPaginas) {
        super(id, nombre, categoria, precioCompra, precioVenta, stock, stockMinimo);
        this.autor = autor;
        this.editorial = editorial;
        this.isbn = isbn;
        this.numPaginas = numPaginas;
    }

    public String getAutor() { return autor; }
    public String getEditorial() { return editorial; }
    public String getIsbn() { return isbn; }
    public int getNumPaginas() { return numPaginas; }

    @Override
    public double calcularPrecioFinal() {
        // Libros: exentos de recargo extra en esta librería (precio de etiqueta).
        return precioVenta;
    }

    @Override
    public String getDetalleEspecifico() {
        return autor + " · " + editorial + " · " + numPaginas + " pág.";
    }
}
