# 📚 Lumina Librería — App de consola POO en Java

![Java](https://img.shields.io/badge/Java-17%2B-007396?logo=java&logoColor=white)
![Consola](https://img.shields.io/badge/UI-Consola%20estilizada-6a1b9a)
![POO](https://img.shields.io/badge/Paradigma-POO-success)
![BD](https://img.shields.io/badge/BD-Listas%20en%20memoria-orange)
![Estado](https://img.shields.io/badge/Estado-Funcional-brightgreen)

Sistema de gestión para una **librería** (inventario, punto de venta, clientes con fidelización,
búsqueda, recomendaciones, reportes y alertas) con menú en consola **estilizada**
(colores ANSI + tablas Unicode), programado con **POO en Java puro** (sin dependencias)
y persistencia temporal en **listas hardcodeadas** (`Database` singleton).

> Proyecto académico de Programación Orientada a Objetos. Ideal para exponer
> herencia, polimorfismo, encapsulamiento, abstracción y composición con un caso real.

---

## ✨ Características

| Módulo | Qué hace |
|---|---|
| 📦 Inventario | Lista libros, útiles/arte y revistas con precio, stock, estado y margen |
| 🔍 Búsqueda | Tolera mayúsculas y tildes; busca en nombre, autor/marca, categoría e ID |
| 🛒 Punto de venta | Carrito, validación de stock, descuento por nivel/volumen, IVA 19%, factura y puntos |
| 👥 Clientes | Fidelización por niveles Bronce → Plata → Oro → Diamante (0/5/10/15% dto.) |
| ✨ Recomendaciones | Sugiere por misma categoría + autor/marca + historial de ventas |
| 📊 Reportes y dashboard | Ingresos, ganancia, ticket promedio, ventas por categoría (barras ASCII), últimas ventas |
| ⚠ Alertas de stock | Detecta `stock <= stockMinimo` para reponer a tiempo |
| ➕ Altas rápidas | Agregar producto/cliente en memoria con **ID automático** |

**Selección sin memorizar IDs:** en cada pantalla de elección se muestra la lista
numerada (`[1]`, `[2]`…) — escribe el **número** o el **ID** clásico (`L-001`, `C-001`).
Con **0** creas un cliente o producto nuevo sin salir del flujo.

---

## 🖥 Vista previa del menú

```
╔══════════════════════════════════════════════════════════╗
║  📚  L U M I N A · L I B R E R Í A                       ║
║      Inventario · Ventas · Clientes · Recomendaciones     ║
╚══════════════════════════════════════════════════════════╝

━ MENÚ PRINCIPAL ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
   [0] Salir
   [1] 📦  Ver inventario completo
   [2] 🔍  Búsqueda
   [3] 🛒  Vender (POS)
   [4] 👥  Ver clientes y fidelización
   [5] ✨  Recomendaciones
   [6] 📊  Reportes y dashboard
   [7] ⚠  Alertas de stock bajo
   [8] ➕  Agregar producto
   [9] 📥  Reponer stock
   [10] 📝  Generar reporte de ventas (CSV)
```

---

## ✅ Requisitos

- **JDK 17 o superior** (comprueba con `java -version`). Descárgalo en <https://adoptium.net>.
- Windows, Linux o macOS. Sin Maven/Gradle ni librerías externas.

## ▶ Instalación y ejecución (Windows)

```bat
compilar.bat
run.bat
```

Manual (cualquier SO, desde la carpeta del proyecto):

```bat
cd /d RUTA\DEL\PROYECTO
cmd /V:ON /c "(for /R src %f in (*.java) do @(set P=%f & echo !P:\=/%!)) > fuentes.txt"
javac -encoding UTF8 -d out @fuentes.txt
java -cp out com.libreriasmart.Main
```

> Si `javac` no se reconoce, instala el JDK 17+ desde <https://adoptium.net>.
> Los `.bat` ya detectan solos el JDK en sus rutas habituales.

---

## 🧪 Demo guiada (2 minutos)

1. **Inventario** (op. 1): mira las 12 filas con estado (`OK` / `⚠ BAJO`) y margen.
2. **Búsqueda** (op. 2): escribe `garcia` → encuentra a *García Márquez* aunque no pongas tilde.
3. **Vender** (op. 3): elige al cliente `1` (Diamante), agrega los productos `4 ×2` y `9 ×3`
   (5+ unidades ⇒ descuento por volumen) y pulsa ENTER para ver la factura con IVA.
4. **Recomendaciones** (op. 5): elige el producto `1` → sugiere otros de ficción primero.
5. **Dashboard** (op. 6) y **alertas** (op. 7): `L-006` y `U-004` salen en ⚠ a propósito.
6. **Cero mágico**: en cliente o producto escribe `0` y crea uno nuevo con ID automático.

## 🧠 Datos de prueba (seed en `Database.java`)

- **Productos:** `L-001` *Cien años de soledad*, `L-002` *El Principito*,
  `L-003` *Clean Code*, `U-001` Mochila, `U-002` Colores Faber-Castell… (12 en total).
- **Clientes:** `C-001` Sofía (Diamante), `C-002` Diego (Oro), `C-005` mostrador (nuevo).
- **Ventas históricas** `V-900…V-903` para que reportes y recomendaciones tengan datos desde el arranque.

---

## 🗂 Estructura del proyecto

```
proyecto-poo/
├── docs/
│   ├── SDD.md                              ← diseño (requisitos, arquitectura, algoritmos, pruebas)
│   ├── DIAGRAMA-CLASES.md                  ← diagrama Mermaid + guía de exposición
│   ├── diagrama-clases.puml                ← fuente PlantUML
│   ├── diagrama de clases.png              ← diagrama renderizado
│   └── Lumina_Libreria_Explicacion_POO*.docx ← explicación POO clase por clase (Word)
├── src/main/java/com/libreriasmart/
│   ├── Main.java                           ← entrada (carga seed y lanza el menú)
│   ├── model/                              ← POO pura: Persona, Cliente, NivelCliente,
│   │                                          Producto, Libro, UtilEscolar, Revista,
│   │                                          Categoria, DetalleVenta, Venta
│   ├── repository/Database.java            ← "BD" temporal: 3 ArrayList + seed (singleton)
│   ├── service/                            ← negocio: ProductoService, VentaService,
│   │                                          RecomendacionService, ReporteService
│   └── ui/ConsoleUI.java, AppMenu.java      ← consola estilizada + flujo del menú
├── compilar.bat / run.bat
└── README.md
```

---

## 🧱 POO en este proyecto (resumen)

| Pilar | Dónde | Idea en una frase |
|---|---|---|
| Herencia | `Persona→Cliente`, `Producto→Libro/UtilEscolar/Revista` | Reúso lo común, especializo lo distinto |
| Abstracción | `Producto`/`Persona` abstractas | Programo contra el contrato, no contra cada caso |
| Polimorfismo | `calcularPrecioFinal()`, `getDetalleEspecifico()`, `toRow()` | Mismo mensaje, cada tipo responde a su manera |
| Encapsulamiento | `vender()`/`reponer()`, nivel, descuento capado | Los datos se defienden solos con reglas |
| Composición | `Venta *-- DetalleVenta` | Sin venta no hay detalle |
| Agregación | `Venta→Cliente`, `Database→listas` | Lo uso, pero vive sin mí (BD temporal) |

➡ Explicación completa, clase por clase y con código, en
`docs/Lumina_Libreria_Explicacion_POO_v2.docx` y `docs/SDD.md`.

---

## 📐 Documentación

- `docs/SDD.md` — visión, requisitos, arquitectura en 3 capas, algoritmos, pruebas y roadmap.
- `docs/DIAGRAMA-CLASES.md` — diagrama en Mermaid + cómo exponerlo.
- `docs/diagrama-clases.puml` — pégalo en [plantuml.com](https://www.plantuml.com/plantuml/uml/) para ver el gráfico.

## 🛣 Roadmap

- [ ] Persistencia real (JSON/SQLite) reescribiendo solo `Database`
- [ ] Devoluciones y exportación de factura a TXT
- [ ] Tests JUnit de servicios
- [ ] Rol de empleado / login básico
