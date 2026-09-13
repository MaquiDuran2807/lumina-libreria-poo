package com.libreriasmart.ui;

import com.libreriasmart.model.*;
import com.libreriasmart.repository.Database;
import com.libreriasmart.service.*;
import com.libreriasmart.service.VentaService.ItemCarrito;
import java.util.*;
import java.util.stream.Collectors;

/** MENÚ principal y subflujos de la app. Controla toda la interacción. */
public class AppMenu {
    private final Scanner sc = new Scanner(System.in);
    private final Database db = Database.getInstancia();
    private final ProductoService productos = new ProductoService();
    private final VentaService ventas = new VentaService();
    private final RecomendacionService reco = new RecomendacionService();
    private final ReporteService reportes = new ReporteService();

    public void iniciar() {
        boolean salir = false;
        while (!salir) {
            ConsoleUI.limpiar();
            ConsoleUI.banner();
            ConsoleUI.titulo("MENÚ PRINCIPAL");
            ConsoleUI.menu(new String[]{
                "Salir",
                "📦  Ver inventario completo",
                "🔍  Búsqueda",
                "🛒  Vender (POS)",
                "👥  Ver clientes y fidelización",
                "✨  Recomendaciones",
                "📊  Reportes y dashboard",
                "⚠  Alertas de stock bajo",
                "➕  Agregar producto",
                "📥  Reponer stock"
            });
            int op = leerEntero(0, 9);
            switch (op) {
                case 0 -> salir = true;
                case 1 -> verInventario();
                case 2 -> buscar();
                case 3 -> vender();
                case 4 -> verClientes();
                case 5 -> recomendar();
                case 6 -> dashboard();
                case 7 -> alertas();
                case 8 -> agregarProducto();
                case 9 -> reponer();
            }
        }
        System.out.println(ConsoleUI.VERDE + ConsoleUI.NEGRITA
            + "\n  ¡Gracias por usar Lumina Librería! 📚✨" + ConsoleUI.RESET);
    }

    // ---------------- 1. Inventario ----------------
    private void verInventario() {
        ConsoleUI.titulo("INVENTARIO COMPLETO");
        List<String[]> filas = productos.listarTodos().stream()
            .map(Producto::toRow).collect(Collectors.toList());
        ConsoleUI.tabla(Producto.headers(), filas);
        ConsoleUI.pausar(sc);
    }

    // ---------------- 2. Búsqueda ----------------
    private void buscar() {
        ConsoleUI.titulo("BÚSQUEDA");
        System.out.print("  🔎 Escribe nombre, autor, marca, categoría o ID (vacío = todo): ");
        String q = sc.nextLine();
        List<Producto> res = productos.buscar(q);
        if (res.isEmpty()) {
            ConsoleUI.error("Sin coincidencias para '" + q + "'. Prueba con otra palabra.");
        } else {
            ConsoleUI.exito(res.size() + " coincidencia(s) para '" + q + "':");
            ConsoleUI.tabla(Producto.headers(),
                res.stream().map(Producto::toRow).collect(Collectors.toList()));
            // Detalle del primero (muestra polimorfismo)
            Producto p = res.get(0);
            ConsoleUI.tarjeta("Detalle destacado",
                ConsoleUI.NEGRITA + p.getNombre() + ConsoleUI.RESET,
                "ID: " + p.getId() + "  ·  " + p.getCategoria().getEtiqueta(),
                "Info: " + p.getDetalleEspecifico(),
                "Precio: " + ConsoleUI.dinero(p.getPrecioVenta()) + "  ·  Margen: "
                    + String.format("%.1f%%", p.calcularMargen()));
        }
        ConsoleUI.pausar(sc);
    }

    // ---------------- 3. POS / Vender ----------------
    private void vender() {
        ConsoleUI.titulo("PUNTO DE VENTA 🛒");
        Cliente cliente = elegirCliente();
        if (cliente == null) return;

        List<ItemCarrito> carrito = new ArrayList<>();
        while (true) {
            mostrarProductosNumerados(db.getProductos());
            System.out.print("\n  Número o ID (ENTER para cobrar, 'x' para cancelar, 0 nuevo producto): ");
            String entrada = sc.nextLine().trim();
            if (entrada.isEmpty()) break;
            if (entrada.equalsIgnoreCase("x")) {
                ConsoleUI.info("Venta cancelada.");
                ConsoleUI.pausar(sc);
                return;
            }
            if (entrada.equals("0")) {
                Producto nuevo = crearProductoInteractivo();
                if (nuevo != null) {
                    ConsoleUI.exito("Ahora selecciona '" + nuevo.getNombre() + "' con su número.");
                }
                continue;
            }
            Optional<Producto> opt = resolverProducto(entrada, db.getProductos());
            if (opt.isEmpty()) {
                ConsoleUI.error("Selección '" + entrada + "' no válida. Usa el número de la lista o el ID.");
                continue;
            }
            Producto p = opt.get();
            System.out.println("  → " + p + " | Stock: " + p.getStock()
                + " | " + p.getDetalleEspecifico());
            if (p.getStock() == 0) {
                ConsoleUI.error("Producto agotado, elige otro.");
                continue;
            }
            System.out.print("  Cantidad: ");
            int cant = leerEntero(1, Math.max(1, p.getStock()));
            if (cant > p.getStock()) {
                ConsoleUI.error("Stock insuficiente (tienes " + p.getStock() + ").");
                continue;
            }
            carrito.add(new ItemCarrito(p, cant));
            ConsoleUI.exito("Agregado: " + p.getNombre() + " x" + cant);
        }

        if (carrito.isEmpty()) {
            ConsoleUI.info("Carrito vacío, no se cobró nada.");
            ConsoleUI.pausar(sc);
            return;
        }

        try {
            Venta v = ventas.crearVenta(cliente, carrito);
            imprimirFactura(v);
            // Recomendación post-compra basada en el primer ítem
            List<Producto> sug = reco.recomendar(carrito.get(0).producto, 3);
            if (!sug.isEmpty()) {
                System.out.println(ConsoleUI.AMARILLO + ConsoleUI.NEGRITA
                    + "\n  ✨ También podría interesarte:" + ConsoleUI.RESET);
                sug.forEach(s -> System.out.println("     · " + s + " [" + s.getId() + "]"));
            }
        } catch (IllegalArgumentException e) {
            ConsoleUI.error(e.getMessage());
        }
        ConsoleUI.pausar(sc);
    }

    /** Elige cliente por número de lista, por ID, o 0 para crear uno nuevo en memoria. */
    private Cliente elegirCliente() {
        mostrarClientesNumerados();
        System.out.print("\n  Número, ID o ENTER (mostrador), 0 = nuevo cliente: ");
        String entrada = sc.nextLine().trim();
        if (entrada.isEmpty()) entrada = "C-005";
        if (entrada.equals("0")) {
            return agregarClienteInteractivo();
        }
        Optional<Cliente> opt = resolverCliente(entrada);
        if (opt.isEmpty()) {
            ConsoleUI.error("Cliente '" + entrada + "' no encontrado. Usa el número de la lista.");
            return null;
        }
        Cliente c = opt.get();
        ConsoleUI.info("Cliente: " + c.getNombre() + " · Nivel " + c.getNivel()
            + " (descuento base " + (int) (c.getDescuentoNivel() * 100) + "%)");
        return c;
    }

    private void imprimirFactura(Venta v) {
        System.out.println(ConsoleUI.VERDE + ConsoleUI.NEGRITA);
        System.out.println("  ╔═══════════════ 🧾 FACTURA " + v.getId() + " ═══════════════╗");
        System.out.println("   Cliente : " + v.getCliente().getNombre()
            + " (" + v.getCliente().getNivel() + ")");
        System.out.println("   Fecha   : " + v.getFechaCorta());
        System.out.println("  ─────────────────────────────────────────────" + ConsoleUI.RESET);
        ConsoleUI.tabla(DetalleVenta.headers(),
            v.getDetalles().stream().map(DetalleVenta::toRow).collect(Collectors.toList()));
        System.out.printf("   Subtotal : %s%n", ConsoleUI.dinero(v.getSubtotal()));
        System.out.printf("   Descuento aplicado (%.0f%%): -%s%n",
            v.getTasaDescuento() * 100, ConsoleUI.dinero(v.getValorDescuento()));
        System.out.printf("   IVA 19%%  : %s%n", ConsoleUI.dinero(v.getIva()));
        System.out.println(ConsoleUI.VERDE + ConsoleUI.NEGRITA
            + "   TOTAL    : " + ConsoleUI.dinero(v.getTotalFinal()) + ConsoleUI.RESET);
        ConsoleUI.exito("Puntos de " + v.getCliente().getNombre()
            + " ahora: " + v.getCliente().getPuntosFidelidad()
            + " (" + v.getCliente().getNivel() + ")");
    }

    // ---------------- 4. Clientes ----------------
    private void verClientes() {
        ConsoleUI.titulo("CLIENTES Y FIDELIZACIÓN 👥");
        ConsoleUI.tabla(Cliente.headers(),
            db.getClientes().stream().map(Cliente::toRow).collect(Collectors.toList()));
        reportes.mejorCliente().ifPresent(m ->
            ConsoleUI.tarjeta("🏆 Mejor cliente",
                m.getNombre() + " · " + m.getNivel() + " · " + m.getPuntosFidelidad() + " pts",
                "Descuento permanente: " + (int) (m.getDescuentoNivel() * 100) + "%"));
        ConsoleUI.pausar(sc);
    }

    // ---------------- 5. Recomendaciones ----------------
    private void recomendar() {
        ConsoleUI.titulo("RECOMENDACIONES ✨");
        mostrarProductosNumerados(db.getProductos());
        System.out.print("  Número o ID del producto base (0 = nuevo producto): ");
        String entrada = sc.nextLine().trim();
        if (entrada.equals("0")) {
            Producto nuevo = crearProductoInteractivo();
            if (nuevo == null) { ConsoleUI.pausar(sc); return; }
            entrada = nuevo.getId();
        }
        Optional<Producto> opt = resolverProducto(entrada, db.getProductos());
        if (opt.isEmpty()) {
            ConsoleUI.error("Producto no encontrado. Usa el número de la lista.");
            ConsoleUI.pausar(sc);
            return;
        }
        Producto base = opt.get();
        ConsoleUI.info("Si te gusta '" + base.getNombre() + "' (" + base.getDetalleEspecifico() + ")...");
        List<Producto> sug = reco.recomendar(base, 5);
        ConsoleUI.tabla(Producto.headers(),
            sug.stream().map(Producto::toRow).collect(Collectors.toList()));

        ConsoleUI.titulo("🔥 TOP VENTAS HISTÓRICAS");
        ConsoleUI.tabla(Producto.headers(),
            reco.topVentas(3).stream().map(Producto::toRow).collect(Collectors.toList()));
        ConsoleUI.pausar(sc);
    }

    // ---------------- 6. Dashboard ----------------
    private void dashboard() {
        ConsoleUI.titulo("REPORTES Y DASHBOARD 📊");
        ConsoleUI.tarjeta("💰 Resumen",
            "Ventas registradas : " + db.getVentas().size(),
            "Unidades vendidas  : " + reportes.totalUnidadesVendidas(),
            "Ingresos totales   : " + ConsoleUI.dinero(reportes.ingresosTotales()),
            "Ganancia estimada  : " + ConsoleUI.dinero(reportes.gananciaTotal()),
            "Ticket promedio    : " + ConsoleUI.dinero(reportes.ticketPromedio()));

        System.out.println(ConsoleUI.MAGENTA + ConsoleUI.NEGRITA
            + "\n  Ventas por categoría:" + ConsoleUI.RESET);
        reportes.topCategorias(6).forEach((cat, val) -> {
            int barras = (int) Math.min(30, val / 10000);
            System.out.printf("   %-22s %10s  %s%n",
                cat.getEtiqueta(), ConsoleUI.dinero(val),
                ConsoleUI.CYAN + "█".repeat(Math.max(1, barras)) + ConsoleUI.RESET);
        });

        System.out.println(ConsoleUI.MAGENTA + ConsoleUI.NEGRITA
            + "\n  Últimas ventas:" + ConsoleUI.RESET);
        List<String[]> filas = db.getVentas().stream()
            .sorted(Comparator.comparing(Venta::getFecha).reversed())
            .limit(5).map(Venta::toRow).collect(Collectors.toList());
        ConsoleUI.tabla(Venta.headers(), filas);
        ConsoleUI.pausar(sc);
    }

    // ---------------- 7. Alertas ----------------
    private void alertas() {
        ConsoleUI.titulo("ALERTAS DE STOCK ⚠");
        List<Producto> bajos = productos.stockBajo();
        if (bajos.isEmpty()) {
            ConsoleUI.exito("Todo el inventario está saludable. 🎉");
        } else {
            ConsoleUI.alerta(bajos.size() + " producto(s) necesitan reposición urgente:");
            ConsoleUI.tabla(Producto.headers(),
                bajos.stream().map(Producto::toRow).collect(Collectors.toList()));
        }
        ConsoleUI.pausar(sc);
    }

    // ---------------- 8 y 9. Agregar / Reponer ----------------
    /** Opción de menú: crea y pausa. El ID se asigna solo. */
    private void agregarProducto() {
        Producto nuevo = crearProductoInteractivo();
        if (nuevo != null) {
            ConsoleUI.info("Quedó guardado en memoria con ID " + nuevo.getId() + ".");
        }
        ConsoleUI.pausar(sc);
    }

    /** Crea un producto en memoria con ID automático. Retorna el creado. */
    private Producto crearProductoInteractivo() {
        ConsoleUI.titulo("AGREGAR PRODUCTO ➕");
        System.out.println("   [1] Libro   [2] Útil/Arte   [3] Revista");
        System.out.print("  Tipo: ");
        int tipo = leerEntero(1, 3);
        String id = generarIdProducto(tipo);
        System.out.println("  ID asignado: " + id);
        System.out.print("  Nombre: ");
        String nombre = sc.nextLine().trim();
        while (nombre.isEmpty()) {
            System.out.print("  (el nombre no puede estar vacío): ");
            nombre = sc.nextLine().trim();
        }
        System.out.print("  Precio compra: ");
        double pc = leerDouble(1, 10000000);
        System.out.print("  Precio venta: ");
        double pv = leerDouble(1, 10000000);
        System.out.print("  Stock inicial: ");
        int stock = leerEntero(0, 10000);

        Producto nuevo;
        if (tipo == 1) {
            System.out.print("  Autor: ");
            String autor = sc.nextLine();
            System.out.print("  Editorial: ");
            String ed = sc.nextLine();
            nuevo = new Libro(id, nombre, Categoria.LIBRO_FICCION, pc, pv,
                stock, 3, autor, ed, "ISBN-" + id, 200);
        } else if (tipo == 2) {
            System.out.print("  Marca: ");
            String marca = sc.nextLine();
            nuevo = new UtilEscolar(id, nombre, Categoria.UTILES, pc, pv,
                stock, 3, marca, "General");
        } else {
            System.out.print("  Edición: ");
            String edi = sc.nextLine();
            nuevo = new Revista(id, nombre, Categoria.REVISTA, pc, pv,
                stock, 3, edi, "Mensual");
        }
        productos.agregar(nuevo);
        ConsoleUI.exito("Producto '" + nombre + "' agregado con ID " + id + ".");
        return nuevo;
    }

    /** Crea un cliente en memoria con ID automático. Retorna el creado. */
    private Cliente agregarClienteInteractivo() {
        ConsoleUI.titulo("AGREGAR CLIENTE 👥");
        String id = generarIdCliente();
        System.out.println("  ID asignado: " + id);
        System.out.print("  Nombre: ");
        String nombre = sc.nextLine().trim();
        while (nombre.isEmpty()) {
            System.out.print("  (el nombre no puede estar vacío): ");
            nombre = sc.nextLine().trim();
        }
        System.out.print("  Email: ");
        String email = sc.nextLine().trim();
        System.out.print("  Teléfono: ");
        String tel = sc.nextLine().trim();
        Cliente nuevo = new Cliente(id, nombre,
            email.isEmpty() ? "sin@correo.com" : email,
            tel.isEmpty() ? "0000000000" : tel);
        db.getClientes().add(nuevo);
        ConsoleUI.exito("Cliente '" + nombre + "' agregado con ID " + id + ".");
        return nuevo;
    }

    private void reponer() {
        ConsoleUI.titulo("REPONER STOCK 📥");
        mostrarProductosNumerados(db.getProductos());
        System.out.print("  Número o ID del producto (0 = nuevo producto): ");
        String entrada = sc.nextLine().trim();
        if (entrada.equals("0")) {
            crearProductoInteractivo();
            ConsoleUI.pausar(sc);
            return;
        }
        Optional<Producto> opt = resolverProducto(entrada, db.getProductos());
        if (opt.isEmpty()) {
            ConsoleUI.error("Producto no encontrado. Usa el número de la lista.");
            ConsoleUI.pausar(sc);
            return;
        }
        Producto p = opt.get();
        System.out.println("  Stock actual de '" + p.getNombre() + "': " + p.getStock());
        System.out.print("  Cantidad a ingresar: ");
        int cant = leerEntero(1, 10000);
        p.reponer(cant);
        ConsoleUI.exito("Nuevo stock: " + p.getStock());
        ConsoleUI.pausar(sc);
    }

    // ---------------- Listas numeradas + IDs automáticos ----------------
    private void mostrarClientesNumerados() {
        System.out.println("  Clientes (elige el número o el ID):");
        List<Cliente> lista = db.getClientes();
        for (int i = 0; i < lista.size(); i++) {
            Cliente c = lista.get(i);
            System.out.printf("   [%d] %s · %s (%s · %d pts)%n",
                i + 1, c.getId(), c.getNombre(), c.getNivel(), c.getPuntosFidelidad());
        }
        System.out.println("   [0] ➕ Nuevo cliente");
    }

    private void mostrarProductosNumerados(List<Producto> lista) {
        System.out.println("  Catálogo (elige el número o el ID):");
        for (int i = 0; i < lista.size(); i++) {
            Producto p = lista.get(i);
            System.out.printf("   [%d] %s · %s (%s, stock %d)%n",
                i + 1, p.getId(), p.getNombre(),
                ConsoleUI.dinero(p.getPrecioVenta()), p.getStock());
        }
        System.out.println("   [0] ➕ Agregar producto");
    }

    /** Acepta número de lista (1..N) o ID como "L-001"/"C-001". */
    private Optional<Producto> resolverProducto(String entrada, List<Producto> lista) {
        try {
            int n = Integer.parseInt(entrada);
            if (n >= 1 && n <= lista.size()) return Optional.of(lista.get(n - 1));
            return Optional.empty();
        } catch (NumberFormatException e) {
            return db.buscarProductoPorId(entrada);
        }
    }

    private Optional<Cliente> resolverCliente(String entrada) {
        try {
            int n = Integer.parseInt(entrada);
            if (n >= 1 && n <= db.getClientes().size()) {
                return Optional.of(db.getClientes().get(n - 1));
            }
            return Optional.empty();
        } catch (NumberFormatException e) {
            return db.buscarClientePorId(entrada);
        }
    }

    private String generarIdCliente() {
        int max = 0;
        for (Cliente c : db.getClientes()) {
            try {
                int n = Integer.parseInt(c.getId().split("-")[1]);
                max = Math.max(max, n);
            } catch (Exception ignored) {}
        }
        return String.format("C-%03d", max + 1);
    }

    private String generarIdProducto(int tipo) {
        String prefijo = tipo == 1 ? "L" : tipo == 2 ? "U" : "R";
        int max = 0;
        for (Producto p : db.getProductos()) {
            if (p.getId().startsWith(prefijo + "-")) {
                try {
                    int n = Integer.parseInt(p.getId().split("-")[1]);
                    max = Math.max(max, n);
                } catch (Exception ignored) {}
            }
        }
        return String.format("%s-%03d", prefijo, max + 1);
    }

    // ---------------- Lectura segura ----------------
    private int leerEntero(int min, int max) {
        while (true) {
            try {
                String line = sc.nextLine().trim();
                int v = Integer.parseInt(line);
                if (v < min || v > max) {
                    System.out.print("  (ingresa un número entre " + min + " y " + max + "): ");
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                System.out.print("  (número inválido, intenta de nuevo): ");
            } catch (NoSuchElementException e) {
                return min;
            }
        }
    }

    private double leerDouble(double min, double max) {
        while (true) {
            try {
                double v = Double.parseDouble(sc.nextLine().trim());
                if (v < min || v > max) {
                    System.out.print("  (valor entre " + (int) min + " y " + (int) max + "): ");
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                System.out.print("  (número inválido, intenta de nuevo): ");
            }
        }
    }
}
