# Mapa mental — La Herencia en la POO

> Pega solo el bloque `mermaid` en [mermaid.live](https://mermaid.live), GitHub o Notion.
> Imagen bonita: `mapa-mental-herencia-poo.png` (generada con Python + Pillow).
> Ramas según rúbrica: definición y clasificación, abstractas, constructores,
> redefinición, final, protected — con ejemplos Java y relaciones entre ramas.

```mermaid
mindmap
  root((Herencia en POO))
    Definición y clasificación
      Subclase adquiere atributos y métodos de la base
      Reutilización, extensibilidad y modularidad
      Tipos de herencia
        Simple: una sola superclase
        Múltiple: varias bases; en Java solo con interfaces
        Multinivel: cadena A, B, C
        Jerárquica: varias hijas de una misma base
      Ej: class Libro extends Producto y hereda todo
    Clases abstractas
      No se pueden instanciar
      Mezclan métodos con cuerpo y abstractos
      Las hijas implementan lo abstracto
      Ej: abstract class Producto y no se instancia
    Constructores
      No se heredan
      super invoca al constructor de la base
      super va en la primera línea
      Ej: super con id y nombre y llama al constructor base
    Redefinición Override
      La hija cambia un método heredado
      Anotación Override
      Mismo nombre y misma firma
      Ej: Override en toRow y cambia su actuar
    Restringir con final
      final en clase: no se puede heredar
      final en método: no se puede redefinir
      Ej: final class Config y nadie la puede heredar
    Visibilidad protected
      Visible en la misma clase
      En subclases, aun de otro paquete
      En clases del mismo paquete
      Ej: protected int stock visible para las hijas
```

## Relaciones entre ramas (flechas del PNG)
- De la hoja *"Las hijas implementan lo abstracto"* a la hoja *"Anotación Override"*:
  lo abstracto *se implementa con @Override* (por eso se conectan).
- De *Restringir (final)* a *Redefinición*: *final impide redefinir*.

## Fuentes (del PDF)
- Deitel & Deitel (2016) · Barnes (2007) · Joyanes (1996) · López (2006) · Wu (2008)

## Equipo
Miguel Quiroga — Universidad del Tolima
