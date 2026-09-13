package com.libreriasmart.repository;

import com.libreriasmart.model.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * "BASE DE DATOS" EN LISTAS hardcodeadas y temporales (en memoria).
 * PATRÓN SINGLETON: una sola instancia para toda la app.
 * Aquí se siembran (seed) productos, clientes y ventas de ejemplo.
 */
public class Database {
    private static Database instancia;

    private final List<Producto> productos = new ArrayList<>();
    private final List<Cliente> clientes = new ArrayList<>();
    private final List<Venta> ventas = new ArrayList<>();

    private Database() {
        sembrarProductos();
        sembrarClientes();
        sembrarVentasHistoricas();
    }

    public static synchronized Database getInstancia() {
        if (instancia == null) instancia = new Database();
        return instancia;
    }

    /** Solo para tests: reinicia el seed. */
    public static synchronized void reiniciar() {
        instancia = new Database();
    }

    public List<Producto> getProductos() { return productos; }
    public List<Cliente> getClientes() { return clientes; }
    public List<Venta> getVentas() { return ventas; }

    public Optional<Producto> buscarProductoPorId(String id) {
        return productos.stream()
            .filter(p -> p.getId().equalsIgnoreCase(id.trim()))
            .findFirst();
    }

    public Optional<Cliente> buscarClientePorId(String id) {
        return clientes.stream()
            .filter(c -> c.getId().equalsIgnoreCase(id.trim()))
            .findFirst();
    }

    // ---------------- SEED ----------------

    private void sembrarProductos() {
        productos.add(new Libro("L-001", "Cien años de soledad", Categoria.LIBRO_FICCION,
            28000, 45000, 15, 5, "Gabriel García Márquez", "Sudamericana", "978-0307474728", 432));
        productos.add(new Libro("L-002", "El Principito", Categoria.LIBRO_INFANTIL,
            15000, 28000, 20, 5, "Antoine de Saint-Exupéry", "Salamandra", "978-0156013987", 96));
        productos.add(new Libro("L-003", "Clean Code", Categoria.LIBRO_ACADEMICO,
            60000, 95000, 8, 3, "Robert C. Martin", "Prentice Hall", "978-0132350884", 464));
        productos.add(new Libro("L-004", "Harry Potter y la piedra filosofal", Categoria.LIBRO_FICCION,
            32000, 52000, 12, 4, "J.K. Rowling", "Salamandra", "978-8478884957", 320));
        productos.add(new Libro("L-005", "Hábitos Atómicos", Categoria.LIBRO_ACADEMICO,
            35000, 59000, 4, 5, "James Clear", "Avery", "978-0735211292", 320));
        productos.add(new Libro("L-006", "La casa en Mango Street", Categoria.LIBRO_FICCION,
            18000, 32000, 2, 4, "Sandra Cisneros", "Vintage", "978-0679734772", 110));
        productos.add(new UtilEscolar("U-001", "Mochila Escolar Pro 25L", Categoria.UTILES,
            55000, 89900, 10, 3, "Totto", "Mochila"));
        productos.add(new UtilEscolar("U-002", "Set 24 Colores Profesionales", Categoria.ARTE,
            22000, 39900, 25, 6, "Faber-Castell", "Colores"));
        productos.add(new UtilEscolar("U-003", "Cuaderno Cosido 100h Cuadriculado", Categoria.UTILES,
            8000, 14900, 40, 10, "Norma", "Cuaderno"));
        productos.add(new UtilEscolar("U-004", "Kit Acuarelas 36 + Pinceles", Categoria.ARTE,
            30000, 54900, 3, 5, "Pelikan", "Acuarela"));
        productos.add(new Revista("R-001", "National Geographic Kids", Categoria.REVISTA,
            12000, 19900, 18, 5, "Marzo 2026", "Mensual"));
        productos.add(new Revista("R-002", "Muy Interesante Ciencia", Categoria.REVISTA,
            10000, 17900, 14, 5, "N° 412", "Mensual"));
    }

    private void sembrarClientes() {
        clientes.add(new Cliente("C-001", "Sofía Ramírez", "sofia@mail.com", "3001112233", 1200, 14));
        clientes.add(new Cliente("C-002", "Diego Torres", "diego@mail.com", "3114445566", 650, 8));
        clientes.add(new Cliente("C-003", "Lucía Fernández", "lucia@mail.com", "3207778899", 180, 3));
        clientes.add(new Cliente("C-004", "Mateo García", "mateo@mail.com", "3012223344", 40, 1));
        clientes.add(new Cliente("C-005", "Valentina Ruiz", "vale@mail.com", "3159990011", 0, 0));
    }

    private void sembrarVentasHistoricas() {
        // Ventas de ejemplo para que los reportes/recomendaciones tengan datos.
        crearHistorica("V-900", "C-001", 6, new String[][]{{"L-001", "2"}, {"U-002", "1"}});
        crearHistorica("V-901", "C-002", 4, new String[][]{{"L-003", "1"}, {"U-001", "1"}});
        crearHistorica("V-902", "C-001", 2, new String[][]{{"L-004", "1"}, {"U-003", "3"}});
        crearHistorica("V-903", "C-003", 1, new String[][]{{"L-002", "2"}, {"R-001", "1"}});
    }

    private void crearHistorica(String idVenta, String idCliente, int diasAtras, String[][] items) {
        Cliente c = buscarClientePorId(idCliente).orElse(null);
        if (c == null) return;
        Venta v = new Venta(idVenta, c, LocalDateTime.now().minusDays(diasAtras), 0.05);
        for (String[] it : items) {
            buscarProductoPorId(it[0]).ifPresent(p -> {
                int cant = Integer.parseInt(it[1]);
                // Descontamos stock para que el seed sea coherente:
                if (p.getStock() >= cant) {
                    p.vender(cant);
                    v.agregarDetalleHistorico(p, cant);
                }
            });
        }
        ventas.add(v);
    }
}
