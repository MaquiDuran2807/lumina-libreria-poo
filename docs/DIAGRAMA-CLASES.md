# 📚 Lumina Librería — Diagrama de Clases

![Diagrama principal renderizado](diagrama-clases.png)

> **Ojo, son dos formatos distintos, no se mezclan:**
> - El archivo `docs/diagrama-clases.puml` (PlantUML) es el que se pega en [plantuml.com/plantuml](https://www.plantuml.com/plantuml/uml/) o en VS Code con la extensión PlantUML.
> - El bloque `mermaid` de abajo (solo el código entre las triples comillas, sin el resto del .md) es el que se pega en [mermaid.live](https://mermaid.live), GitHub o Notion.

## Versión Mermaid (para GitHub / Notion)

![Diagrama completo generado con Python](diagrama-completo.png)

> Si el bloque `mermaid` te da error donde lo pegues, usa esta imagen
> (se genera con `python docs/generar_completo.py`, mismo contenido, sin cruces sobre cajas).

```mermaid
classDiagram
  class Persona {
    <<abstract>>
    #id: String
    #nombre: String
    +getTipo() String
    +toRow() List~String~
  }
  class Cliente {
    -puntosFidelidad: int
    +getNivel() NivelCliente
    +agregarCompra(valor) void
  }
  class NivelCliente {
    <<enum>>
    BRONCE
    PLATA
    ORO
    DIAMANTE
  }
  class Producto {
    <<abstract>>
    #id: String
    #nombre: String
    #categoria: Categoria
    #precioCompra: double
    #precioVenta: double
    #stock: int
    +calcularMargen() double
    +vender(cant) bool
    +calcularPrecioFinal() double
    +getDetalleEspecifico() String
  }
  class Tabulable {
    <<interface>>
    +toRow() List~String~
  }
  class Inventariable {
    <<interface>>
    +vender(cant) bool
    +reponer(cant) void
    +necesitaReposicion() bool
  }
  class PoliticaDescuento {
    <<interface>>
    +calcular(cliente,sub,unds) double
  }
  class DescuentoEstandar {
    +calcular(cliente,sub,unds) double
  }
  class Libro {
    -autor: String
    -editorial: String
    -isbn: String
  }
  class UtilEscolar {
    -marca: String
    -tipoUtil: String
  }
  class Revista {
    -edicion: String
    -periodicidad: String
  }
  class Categoria {
    <<enum>>
    LIBRO_FICCION
    LIBRO_INFANTIL
    LIBRO_ACADEMICO
    UTILES
    ARTE
    REVISTA
  }
  class Venta {
    -id: String
    -fecha: DateTime
    +getTotalFinal() double
    +agregarDetalle(prod,cant) void
  }
  class DetalleVenta {
    -cantidad: int
    +getSubtotal() double
  }
  class Database {
    -productos: List~Producto~
    -clientes: List~Cliente~
    -ventas: List~Venta~
    +getInstancia() Database
  }
  class ProductoService {
    +buscar(query) List
    +stockBajo() List
  }
  class VentaService {
    +crearVenta(cliente,carrito) Venta
  }
  class RecomendacionService {
    +recomendar(base,max) List
  }
  class ReporteService {
    +ingresosTotales() double
    +dashboard() void
  }
  class ConsoleUI {
    +banner() void
    +tabla(headers,filas) void
  }
  class AppMenu {
    +iniciar() void
  }

  Persona <|-- Cliente
  Producto <|-- Libro
  Producto <|-- UtilEscolar
  Producto <|-- Revista
  Tabulable <|.. Persona
  Tabulable <|.. Producto
  Tabulable <|.. Venta
  Tabulable <|.. DetalleVenta
  Inventariable <|.. Producto
  PoliticaDescuento <|.. DescuentoEstandar
  Venta *-- DetalleVenta
  Venta o-- Cliente
  DetalleVenta o-- Producto
  Database o-- Producto
  Database o-- Cliente
  Database o-- Venta
```

## Cómo leerlo (para tu exposición)

| Relación | Ejemplo | Significado |
|---|---|---|
| `Persona <|-- Cliente` | Herencia | Cliente **es-una** Persona |
| `Producto <|-- Libro/Util/Revista` | Herencia + polimorfismo | Cada tipo calcula su precio/detalle distinto |
| `<|..` (triángulo punteado) | Realización | La clase cumple el contrato de la interfaz (ej. `Producto` realiza `Inventariable`) |
| `Venta *-- DetalleVenta` | Composición | Sin venta no existen sus detalles |
| `Venta o-- Cliente` | Agregación | La venta referencia un cliente que vive fuera |
| `Database o-- Producto` | Agregación | La BD en listas **contiene** productos temporales |
| `AppMenu ..> Service` | Dependencia | El menú **usa** los servicios |
