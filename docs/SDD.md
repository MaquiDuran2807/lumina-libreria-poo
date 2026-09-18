# SDD — Documento de Diseño de Software
## Lumina Librería 📚 (App de consola en Java + POO)

| Campo | Detalle |
|---|---|
| **Proyecto** | Sistema de gestión para librería (estilo NexusMart) |
| **Tipo** | Aplicación de consola / escritorio, Java 17+, sin dependencias externas |
| **BD** | Listas en memoria hardcodeadas (`Database` singleton) — almacenamiento temporal |
| **Paquete base** | `com.libreriasmart` |
| **Autor** | Proyecto POO — exposición universitaria |

---

## 1. Visión y alcance

### 1.1 Problema
Una librería pequeña lleva inventario, ventas y clientes en cuadernos/Excel: no sabe qué se vende más, qué reponer, ni cómo premiar clientes fieles.

### 1.2 Solución
App de consola **bonita y práctica** que centraliza:
- 📦 Inventario (libros, útiles/arte, revistas)
- 🛒 Punto de venta con factura estilizada, IVA y descuentos automáticos
- 👥 Fidelización por niveles (Bronce → Diamante)
- 🔍 Búsqueda tolerante (ignora tildes/mayúsculas)
- ✨ Recomendador ("si te gusta X, te gustará Y")
- 📊 Dashboard con ingresos, ticket promedio, ranking y gráficas de barras ASCII
- ⚠ Alertas de stock bajo

### 1.3 Fuera de alcance (v1)
Sin archivos/BD real, sin GUI, sin login multiusuario. Todo vive en listas mientras el programa corre.

---

## 2. Requisitos

### Funcionales
| ID | Requisito |
|---|---|
| RF-01 | CRUD simplificado de productos (agregar, listar, eliminar, reponer) |
| RF-02 | Búsqueda por nombre/autor/marca/categoría/ID (tolera tildes y mayúsculas) |
| RF-03 | Venta con carrito, validación de stock, IVA 19% y descuento por nivel/volumen |
| RF-04 | Fidelización: 1 pto por cada $10.000; niveles con descuento 0/5/10/15% |
| RF-05 | Recomendaciones por categoría + autor/marca + popularidad |
| RF-06 | Dashboard: ingresos, ganancia, ticket, top categorías, últimas ventas |
| RF-07 | Alertas de stock (`stock <= stockMinimo`) |
| RF-08 | UI de consola estilizada (colores ANSI, tablas Unicode, banners) |

### No funcionales
- RNF-01: Solo JDK (sin Maven/Gradle obligatorios), corre en Windows/Linux/Mac.
- RNF-02: Compila con `javac`, corre con `java`.
- RNF-03: Código POO ejemplar: herencia, polimorfismo, encapsulamiento, abstracción, composición.
- RNF-04: Datos seed coherentes para demo inmediata (12 productos, 5 clientes, 4 ventas).

---

## 3. Arquitectura (3 capas + UI)

```
┌─────────────┐     usa      ┌──────────────┐     lee/escribe   ┌─────────────┐
│     ui      │ ───────────► │   service    │ ────────────────► │ repository  │
│ AppMenu     │              │ Producto/    │                   │ Database    │
│ ConsoleUI   │              │ Venta/Reco/  │                   │ (Listas)    │
│ Main        │              │ Reporte      │                   └──────┬──────┘
└─────────────┘              └──────────────┘                          │ contiene
       ▲                            ▲                          ┌──────▼──────┐
       └────────── muestra ─────────┴──────────────────────────│    model    │
              Producto.toRow(), Venta.toRow()...               │ POO pura    │
                                                               └─────────────┘
```

- **model**: solo datos + reglas (sin `Scanner`, sin `System.out`).
- **repository.Database**: singleton con 3 `ArrayList` + métodos `seed`.
- **service**: lógica de negocio (búsqueda, descuentos, recomendaciones, reportes).
- **ui**: todo lo visual (`ConsoleUI`) + flujo (`AppMenu`).

---

## 4. Diseño POO (pilares + interfaces)

### 4.1 Herencia
- `Persona (abstract)` → `Cliente`
- `Producto (abstract)` → `Libro` | `UtilEscolar` | `Revista`

### 4.2 Interfaces (contratos sin código, `- -▷` realización)
- `ITabulable.toRow()`: lo implementan `Persona`, `Producto`, `Venta`, `DetalleVenta`. `ConsoleUI.tablaDe()` pide la interfaz, así cualquier clase futura tabulable se dibuja sin tocar la UI.
- `IInventariable` (vender/reponer/stock): lo implementa `Producto`; los servicios piden el contrato cuando solo les importa el stock.
- `IPoliticaDescuento` (patrón Strategy) + `DescuentoEstandar`: `VentaService` recibe la política por constructor (inyección), así la regla se cambia o se prueba sin tocar el servicio.

### 4.2 Polimorfismo
- `Producto.calcularPrecioFinal()` y `getDetalleEspecifico()`: cada subclase responde distinto (libro → autor/editorial, útil → marca/tipo, revista → edición).
- `Persona.getTipo()` / `toRow()`: cada persona se presenta y tabula distinto.
- El POS cobra `List<Producto>` sin saber qué subtipo es (sustitución de Liskov).

### 4.3 Encapsulamiento
- Atributos `protected/private`; `stock` solo cambia vía `vender()`/`reponer()` con validación; `precioVenta` rechaza ≤ 0; `Venta.tasaDescuento` capada a 30%.

### 4.4 Abstracción
- `Producto` y `Persona` definen **qué** se hace, las hijas deciden **cómo**.
- `ConsoleUI.tabla(headers, filas)`: la app no sabe dibujar bordes, solo entrega datos.

### 4.5 Composición / Agregación
- `Venta *-- DetalleVenta` (composición: mueren juntas).
- `Venta o-- Cliente`, `DetalleVenta o-- Producto`, `Database o-- {Producto, Cliente, Venta}` (agregación).

---

## 5. Modelo de datos (listas como BD temporal)

`Database` (singleton, `repository/Database.java`):

```java
List<Producto> productos;  // 12 seed: L-001..L-006, U-001..U-004, R-001..R-002
List<Cliente>  clientes;   // 5 seed: C-001 (Diamante) ... C-005 (nuevo)
List<Venta>    ventas;     // 4 históricas V-900..V-903 (descuentan stock al sembrar)
```

- IDs legibles (`L-`, `U-`, `R-`, `C-`, `V-`) para demo en vivo.
- Sin persistencia: al cerrar se pierde (es el requisito: "temporal"). Migración futura: cambiar `Database` por JDBC/JSON sin tocar `service` ni `ui`.

---

## 6. Lógica de negocio (algoritmos)

### 6.1 Búsqueda (`ProductoService.buscar`)
Normaliza (minúsculas + sin tildes) y hace `contains` sobre nombre + detalle + categoría + ID.

### 6.2 Descuento (`VentaService.calcularDescuento`)
```
base = descuentoNivel(cliente)        // 0 / 5 / 10 / 15 %
+ 5% si unidades >= 5
+ 3% si subtotal > $150.000
tope 25%
total = (subtotal - dto) * 1.19 (IVA)
puntos += total / 10.000
```

### 6.3 Recomendador (`RecomendacionService.recomendar`)
Puntaje por candidato (excluye base y sin stock):
```
+30 misma categoría
+50 mismo autor/marca (primera palabra del detalle)
+2  por cada unidad histórica vendida
+ margen/10 (desempate rentable)
→ ordena desc, toma top-N
```

### 6.4 Reportes (`ReporteService`)
Ingresos = Σ `venta.totalFinal`; ganancia = Σ (`precio - costo`) − dto; ticket = ingresos / #ventas; top categorías por Σ subtotal; mejor cliente por puntos.

---

## 7. Diseño de UI en consola

`ui/ConsoleUI.java`: colores ANSI (CYAN/MAGENTA/VERDE/AMARILLO), banner ASCII, `titulo()`, `tarjeta()`, `tabla()` Unicode (`┌┬┐├┼┤└┴┘`), `menu()` numerado, `pausar()`.

`ui/AppMenu.java`: bucle 0–10 (inventario, búsqueda, POS, clientes, recomendaciones, dashboard, alertas, agregar, reponer, reporte CSV). Selección por número de lista o por ID; 0 crea cliente/producto nuevo en memoria con ID automático. Lectura segura (`leerEntero/leerDouble` con reintento). Factura con tabla de detalles + subtotal/dto/IVA/total + sugerencias post-compra.

Ejemplo de factura:
```
╔═══════════════ 🧾 FACTURA V-1001 ═══════════════╗
 Cliente : Sofía Ramírez (DIAMANTE)
 ─────────────────────────────────────────────
┌────┬──────────┬──────┬────────┬──────────┐
│ ID │ PRODUCTO │ CANT │ P.UNIT │ SUBTOTAL │
...
 Subtotal : $150.000 | Dto 20% | IVA 19% | TOTAL: $142.800
```

---

## 8. Estructura de archivos

```
proyecto poo/
├── docs/
│   ├── SDD.md                  ← este documento
│   ├── DIAGRAMA-CLASES.md      ← Mermaid + guía de exposición
│   ├── diagrama-clases.puml    ← fuente PlantUML
│   └── diagrama-clases.png     ← diagrama principal renderizado
├── src/main/java/com/libreriasmart/
│   ├── Main.java
│   ├── model/{Categoria,NivelCliente,Persona,Cliente,Producto,Libro,UtilEscolar,Revista,DetalleVenta,Venta,ITabulable,IInventariable}.java
│   ├── repository/Database.java
│   ├── service/{ProductoService,VentaService,IPoliticaDescuento,DescuentoEstandar,RecomendacionService,ReporteService}.java
│   └── ui/{ConsoleUI,AppMenu}.java
├── compilar.bat / run.bat      ← scripts Windows
└── README.md
```

---

## 9. Compilación, ejecución y demo (5 min)

```bat
compilar.bat   :: javac -d out (todos los .java, UTF-8)
run.bat        :: java -cp out com.libreriasmart.Main
```

Guion sugerido: ① inventario → ② búsqueda "garcia" (tolera "GARCÍA") → ③ vender al cliente N.º 1 con 5+ unds (verás el descuento por nivel + volumen) → ④ recomendaciones del producto N.º 1 → ⑤ dashboard + alertas (L-006 y U-004 en ⚠ BAJO a propósito). Para elegir, escribe el número de la lista; con 0 agregas un cliente/producto nuevo en memoria.

---

## 10. Pruebas manuales

| # | Caso | Esperado |
|---|---|---|
| 1 | Buscar "principito" / "PRINCIPITO" / "principíto" | Encuentra L-002 en los 3 casos |
| 2 | Vender más unidades que el stock | Rechaza con "Stock insuficiente" |
| 3 | Vender 5+ unds a cliente ORO | Dto ≥ 15% (10 nivel + 5 volumen) |
| 4 | Recomendar N.º 1 (ficción) | Sugiere otros de ficción primero |
| 5 | Dashboard tras ventas seed | Ingresos > 0, top categorías no vacío |
| 6 | Elegir 0 en cliente/producto | Crea uno nuevo en memoria con ID automático (C-006, L-007…) |

---

## 11. Decisiones y trabajo futuro

- Se eligieron listas y no archivos para cumplir el requisito "BD temporal hardcodeada" y simplificar la demo.
- Enums (`Categoria`, `NivelCliente`) en vez de tablas: menos clases, mismo poder expresivo.
- Futuro: persistencia JSON/CSV o SQLite (solo reescribir `Database`), login de empleado, devoluciones, exportación de factura a TXT, tests JUnit.
