package com.libreriasmart.model;

/**
 * ABSTRACCIÓN + ENCAPSULAMIENTO: base común de personas del sistema.
 * Demuestra herencia: Cliente la extiende.
 */
public abstract class Persona {
    protected String id;
    protected String nombre;
    protected String email;
    protected String telefono;

    protected Persona(String id, String nombre, String email, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    /** POLIMORFISMO: cada subclase se identifica a su manera. */
    public abstract String getTipo();

    /** Fila para tablas de consola. */
    public abstract String[] toRow();

    @Override
    public String toString() {
        return "[" + getTipo() + "] " + id + " · " + nombre;
    }
}
