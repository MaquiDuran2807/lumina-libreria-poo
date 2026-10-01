"""
Genera docs/diagrama-preliminar.png a partir de docs/diagrama-preliminar.svg
Uso:
    python docs/generar_png.py
    python docs/generar_png.py --ancho 2400
Si hay cairosvg+libcairo hace conversion directa, si no redibuja con Pillow.
"""
from __future__ import annotations
import argparse
import sys
from pathlib import Path

# Canvas del SVG: 1200x1320
SVG_W, SVG_H = 1200, 1320


def main() -> int:
    parser = argparse.ArgumentParser(description="SVG -> PNG para el diagrama preliminar")
    parser.add_argument("--ancho", type=int, default=2400, help="Ancho del PNG en px (default 2400)")
    args = parser.parse_args()

    base = Path(__file__).resolve().parent
    svg = base / "diagrama-preliminar.svg"
    png = base / "diagrama-preliminar.png"

    if not svg.exists():
        print(f"No se encontro {svg}", file=sys.stderr)
        return 1

    if convertir_con_cairosvg(svg, png, args.ancho):
        print(f"OK: {png} ({args.ancho}px de ancho)")
        return 0

    generar_con_pillow(png, args.ancho)
    return 0


def convertir_con_cairosvg(svg: Path, png: Path, ancho: int) -> bool:
    """Intenta conversión directa SVG->PNG. Retorna True si funcionó."""
    try:
        import cairosvg
        cairosvg.svg2png(url=str(svg), write_to=str(png), output_width=ancho)
        return True
    except Exception as e:
        print(f"cairosvg no disponible, usando plan B con Pillow ({e})", file=sys.stderr)
        return False


def generar_con_pillow(png: Path, ancho: int) -> None:
    """Plan B sin dependencias nativas: redibuja el modelo completo con Pillow."""
    from PIL import Image, ImageDraw, ImageFont

    alto = int(ancho * SVG_H / SVG_W)
    esc = ancho / SVG_W
    img = Image.new("RGB", (ancho, alto), "white")
    d = ImageDraw.Draw(img)

    def fuente(size: int, bold: bool = False):
        size_px = max(8, int(size * esc))
        candidatos = [
            r"C:\Windows\Fonts\arialbd.ttf" if bold else r"C:\Windows\Fonts\arial.ttf",
            "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf" if bold else "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",
        ]
        for p in candidatos:
            try:
                from pathlib import Path as _P
                if _P(p).exists():
                    return ImageFont.truetype(p, size_px)
            except Exception:
                pass
        return ImageFont.load_default()

    F_TIT = fuente(20, True)
    F_SUB = fuente(12)
    F_HEAD = fuente(15, True)
    F_TXT = fuente(12)
    F_SMALL = fuente(10)

    def R(x, y, w, h, fill=None, outline="black", width=2):
        d.rectangle([x * esc, y * esc, (x + w) * esc, (y + h) * esc],
                    fill=fill, outline=outline, width=max(1, int(width * esc / 2)))

    def T(x, y, texto, fill="black", anchor="lm", font=None):
        d.text((x * esc, y * esc), texto, fill=fill, anchor=anchor, font=font or F_TXT)

    def caja(x, y, w, h, titulo, color_head, attrs, metodos, extra=None, sub=""):
        R(x, y, w, h, fill="white", width=2)
        R(x, y, w, 44, fill=color_head, width=2)
        T(x + w / 2, y + 24, titulo, anchor="mm", font=F_HEAD)
        if sub:
            T(x + w - 8, y + 24, sub, anchor="rm", font=F_SMALL)
        d.line([x * esc, (y + 44) * esc, (x + w) * esc, (y + 44) * esc],
               fill="black", width=max(2, int(2 * esc)))
        yy = y + 66
        for a in attrs:
            T(x + 12, yy, a, font=F_TXT)
            yy += 21
        d.line([x * esc, yy * esc, (x + w) * esc, yy * esc], fill="black", width=max(2, int(2 * esc)))
        yy += 22
        for m in metodos:
            T(x + 12, yy, m, font=F_TXT)
            yy += 21
        if extra:
            for e in extra:
                T(x + 12, yy, e, font=F_SMALL)
                yy += 18

    T(600, 34, "Lumina Libreria - Preliminar (modelo + interfaces)", anchor="mm", font=F_TIT)
    T(600, 56, "10 clases + 2 interfaces - triangulo=herencia - -triang=realizacion - rombo b.=agreg. - rombo n.=comp.", anchor="mm", font=F_SUB)
    T(600, 1295, "Valery Morales  -  Camilo Romero  -  Miguel Quiroga   |   Elementos de Programacion Orientada a Objetos   |   Universidad del Tolima",
      anchor="mm", font=F_SUB, fill="#333333")

    caja(40, 90, 270, 250, "Persona", "#EDE7F6",
         ["# id : String", "# nombre : String", "# email : String", "# telefono : String"],
         ["+ getTipo() : String", "+ toRow() : String[]"], ["Ej: Sofia, Diego"], sub="abs")
    caja(40, 390, 270, 280, "Cliente", "#E3F2FD",
         ["- puntosFidelidad : int", "- totalCompras : int", "(hereda id, nombre)"],
         ["+ getNivel() : NivelCliente", "+ agregarCompra(v) : void", "+ getDescuentoNivel()"],
         ["Ej: C-001 Sofia (Diamante)"])
    caja(40, 720, 270, 200, "NivelCliente", "#E0F7FA",
         ["BRONCE 0%  PLATA 5%", "ORO 10%  DIAMANTE 15%"],
         ["+ getDescuento() : double", "+ porPuntos(p) : Nivel"], ["Ej: 1200pts->DIAMANTE"], sub="enum")
    caja(380, 90, 300, 300, "Producto", "#FFF3E0",
         ["# id, # nombre", "# precioVenta : double", "# stock, # stockMinimo"],
         ["+ vender(cant) : boolean", "+ reponer(cant) : void", "+ calcularMargen() : double"],
         ["Ej: L-001, U-001, R-001"], sub="abs")
    caja(730, 90, 200, 210, "Categoria", "#F3E5F5",
         ["FICCION, INFANTIL", "ACADEMICO", "UTILES, ARTE, REVISTA"],
         ["(enum sin metodos)"], ["Ej: L-001->FICCION"], sub="enum")
    caja(380, 440, 200, 190, "Libro", "#FFF8E1",
         ["- autor : String", "- editorial : String", "- isbn : String"],
         ["(hereda vender, margen)"], ["Ej: Garcia Marquez"])
    caja(600, 440, 200, 190, "UtilEscolar", "#FFF8E1",
         ["- marca : String", "- tipoUtil : String", "(hereda id, precio)"],
         ["(hereda vender, margen)"], ["Ej: Faber-Castell"])
    caja(820, 440, 200, 190, "Revista", "#FFF8E1",
         ["- edicion : String", "- periodicidad : String", "(hereda id, precio)"],
         ["(hereda vender, margen)"], ["Ej: N412 Mensual"])
    caja(380, 950, 280, 270, "Venta", "#E8F5E9",
         ["- id : String", "- fecha : DateTime", "- tasaDescuento : double"],
         ["+ agregarDetalle(p,c) : void", "+ getTotalFinal() : double", "+ getIva() : double"],
         ["Ej: V-1001 Sofia $142800"])
    caja(700, 950, 280, 270, "DetalleVenta", "#E0F1FB",
         ["- cantidad : int", "- precioUnitario : double"],
         ["+ getSubtotal() : double", "+ getGanancia() : double"],
         ["Ej: L-001 x2 -> $90000"])

    # Fila intermedia: interfaces
    caja(380, 700, 280, 200, "ITabulable", "#E8EAF6",
         ["(contrato, sin codigo)"],
         ["+ toRow() : String[]"],
         ["la implementan: Persona,", "Producto, Venta, Detalle"], sub="«interface»")
    caja(700, 700, 280, 200, "IInventariable", "#E8EAF6",
         ["(contrato, sin codigo)"],
         ["+ vender(c) : boolean", "+ reponer(c) : void", "+ necesitaReposicion()"],
         ["la implementa: Producto"], sub="«interface»")

    R(1000, 700, 160, 200, fill="#FFFDE7", outline="#999999", width=1)
    T(1012, 722, "INTERFACES", font=F_HEAD)
    T(1012, 744, "contrato: solo", font=F_SMALL)
    T(1012, 760, "firma, sin codigo", font=F_SMALL)
    T(1012, 780, "- -triang = realiza", font=F_SMALL)
    T(1012, 800, "programa contra", font=F_SMALL)
    T(1012, 818, "el contrato", font=F_SMALL)

    R(1000, 950, 160, 270, fill="#FAFAFA", outline="#999999", width=1)
    T(1012, 972, "Leyenda", font=F_HEAD)
    T(1012, 996, "triangulo=herencia", font=F_SMALL)
    T(1012, 1014, "rombo blanco=agreg.", font=F_SMALL)
    T(1012, 1032, "rombo negro=comp.", font=F_SMALL)
    T(1012, 1052, "- privado + publico", font=F_SMALL)
    T(1012, 1070, "# protegido", font=F_SMALL)
    T(1012, 1090, "- -triang=realiz.", font=F_SMALL)

    W = max(3, int(3 * esc))
    # 1. Persona->Cliente
    d.line([(175*esc, 345*esc), (175*esc, 385*esc)], fill="black", width=W)
    d.polygon([(175*esc, 340*esc), (167*esc, 354*esc), (183*esc, 354*esc)], fill="white", outline="black")
    T(232, 368, "es-un", anchor="mm", font=F_SMALL)
    # 2. Cliente->Nivel
    d.line([(175*esc, 675*esc), (175*esc, 715*esc)], fill="black", width=W)
    d.polygon([(175*esc, 670*esc), (167*esc, 682*esc), (175*esc, 694*esc), (183*esc, 682*esc)], fill="black")
    R(190, 686, 90, 20, fill="white", outline="black", width=1)
    T(235, 696, "tiene 1-1", anchor="mm", font=F_SMALL)
    # 3. Producto->Categoria (rombo negro horizontal)
    d.line([(692*esc, 180*esc), (725*esc, 180*esc)], fill="black", width=W)
    d.polygon([(668*esc, 180*esc), (680*esc, 168*esc), (692*esc, 180*esc), (680*esc, 192*esc)], fill="black")
    # tapa el subtitulo para que no se encimen (fondo blanco)
    # NOTE: se dibuja después, así que repintamos subtitulo limpio abajo
    R(320, 44, 560, 22, fill="white", outline="white", width=1)
    T(600, 56, "10 clases + 2 interfaces - triangulo=herencia - -triang=realizacion - rombo b.=agreg. - rombo n.=comp.", anchor="mm", font=F_SUB)
    R(655, 68, 110, 18, fill="white", outline="black", width=1)
    T(710, 77, "clasificado en", anchor="mm", font=F_SMALL)
    # 4. bus herencia
    d.polygon([(530*esc, 390*esc), (522*esc, 404*esc), (538*esc, 404*esc)], fill="white", outline="black")
    d.line([(530*esc, 404*esc), (530*esc, 415*esc), (480*esc, 415*esc), (480*esc, 440*esc)], fill="black", width=W)
    d.line([(530*esc, 415*esc), (700*esc, 415*esc), (700*esc, 440*esc)], fill="black", width=W)
    d.line([(530*esc, 415*esc), (920*esc, 415*esc), (920*esc, 440*esc)], fill="black", width=W)
    # 5. Venta->Cliente canal x=340 (rombo blanco horizontal en Venta)
    d.line([(368*esc, 1085*esc), (340*esc, 1085*esc), (340*esc, 525*esc), (310*esc, 525*esc)], fill="black", width=W)
    d.polygon([(368*esc, 1085*esc), (380*esc, 1073*esc), (392*esc, 1085*esc), (380*esc, 1097*esc)],
              fill="white", outline="black")
    R(60, 688, 120, 20, fill="white", outline="black", width=1)
    T(120, 702, "atiende a 1-1", anchor="mm", font=F_SMALL)
    # 6. Venta->Detalle (rombo negro)
    d.line([(672*esc, 1085*esc), (695*esc, 1085*esc)], fill="black", width=W)
    d.polygon([(648*esc, 1085*esc), (660*esc, 1073*esc), (672*esc, 1085*esc), (660*esc, 1097*esc)], fill="black")
    T(645, 1068, "contiene", anchor="mm", font=F_SMALL)
    # 7. Detalle->Producto: sube por el callejon x=680 (entre Tabulable e Inventariable)
    d.line([(750*esc, 962*esc), (750*esc, 925*esc), (680*esc, 925*esc), (680*esc, 655*esc),
            (360*esc, 655*esc), (360*esc, 250*esc), (380*esc, 250*esc)],
           fill="black", width=W)
    d.polygon([(750*esc, 938*esc), (762*esc, 950*esc), (750*esc, 962*esc), (738*esc, 950*esc)],
              fill="white", outline="black")
    T(485, 644, "refiere a *", anchor="mm", font=F_SMALL)

    # 8. Realizaciones (contratos): punteadas hacia la interfaz
    import math as _math

    def DL(x1, y1, x2, y2, fill="#283593"):
        dx, dy = x2 - x1, y2 - y1
        dist = _math.hypot(dx, dy)
        t, dash, gap = 0.0, 7.0, 5.0
        while t < dist:
            t2 = min(t + dash, dist)
            d.line([(x1 + dx * t / dist) * esc, (y1 + dy * t / dist) * esc,
                    (x1 + dx * t2 / dist) * esc, (y1 + dy * t2 / dist) * esc],
                   fill=fill, width=max(1, int(2 * esc / 2)))
            t += dash + gap

    def TR(pts, fill="white"):
        d.polygon([(x * esc, y * esc) for x, y in pts], fill=fill, outline="#283593")

    # R1. Producto -> Tabulable (callejon x=590, entre Libro y UtilEscolar)
    DL(590, 390, 590, 700)
    TR([(590, 700), (582, 714), (598, 714)])
    # R2. Producto -> Inventariable (borde derecho, baja por x=810)
    DL(680, 320, 810, 320)
    DL(810, 320, 810, 700)
    TR([(810, 700), (802, 714), (818, 714)])
    # R3. Persona -> Tabulable (diagonal por el callejon x=310-380)
    DL(310, 250, 380, 750)
    TR([(380, 750), (370, 735), (386, 733)])

    img.save(png)
    print(f"OK (Pillow): {png} ({ancho}x{alto})")


if __name__ == "__main__":
    raise SystemExit(main())
