# -*- coding: utf-8 -*-
"""
Diagrama COMPLETO (las 21 clases del bloque mermaid) dibujado con Pillow.
Uso:  python docs/generar_completo.py [--ancho 2250]

Estrategia anti-cruces:
 - Bandas horizontales separadas: UI (y100-300) / servicios (y380-600) /
   Database (y680-880) / modelo pegado (y880-2080, recorte verificado de
   diagrama-preliminar.png).
 - Las relaciones entre bandas van por buses y callejones vacíos con
   coordenadas fijas revisadas; ninguna línea atraviesa una caja.
 - Dentro del modelo se reutiliza el recorte ya verificado (sin redibujar).
"""
from __future__ import annotations
import argparse
import math
import sys
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont

W, H = 1500, 2220  # lienzo en unidades


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("--ancho", type=int, default=2250)
    args = ap.parse_args()

    base = Path(__file__).resolve().parent
    prelim = base / "diagrama-preliminar.png"
    if not prelim.exists():
        print(f"Falta {prelim}: genera primero el preliminar", file=sys.stderr)
        return 1
    out = base / "diagrama-completo.png"

    esc = args.ancho / W
    img = Image.new("RGB", (args.ancho, int(args.ancho * H / W)), "white")
    d = ImageDraw.Draw(img)

    def fuente(size: int, bold: bool = False):
        px = max(8, int(size * esc))
        cands = [r"C:\Windows\Fonts\arialbd.ttf" if bold else r"C:\Windows\Fonts\arial.ttf"]
        for p in cands:
            try:
                if Path(p).exists():
                    return ImageFont.truetype(p, px)
            except Exception:
                pass
        return ImageFont.load_default()

    F_TIT, F_SUB = fuente(22, True), fuente(12)
    F_HEAD, F_TXT, F_SMALL = fuente(14, True), fuente(11), fuente(10)

    def R(x, y, w, h, fill=None, outline="black", width=2):
        d.rectangle([x * esc, y * esc, (x + w) * esc, (y + h) * esc],
                    fill=fill, outline=outline, width=max(1, int(width * esc / 2)))

    def T(x, y, s, fill="black", anchor="lm", font=None):
        d.text((x * esc, y * esc), s, fill=fill, anchor=anchor, font=font or F_TXT)

    def LN(pts, fill="black", width=2):
        d.line([(x * esc, y * esc) for x, y in pts], fill=fill, width=max(1, int(width * esc / 2)))

    def DL(x1, y1, x2, y2, fill="#283593"):
        dx, dy = x2 - x1, y2 - y1
        dist = math.hypot(dx, dy)
        t, dash, gap = 0.0, 7.0, 5.0
        while t < dist:
            t2 = min(t + dash, dist)
            d.line([(x1 + dx * t / dist) * esc, (y1 + dy * t / dist) * esc,
                    (x1 + dx * t2 / dist) * esc, (y1 + dy * t2 / dist) * esc],
                   fill=fill, width=max(1, int(2 * esc / 2)))
            t += dash + gap

    def TRH(pts, fill="white", outline="#283593"):
        d.polygon([(x * esc, y * esc) for x, y in pts], fill=fill, outline=outline)

    def FLA(pts, fill="black"):  # flecha de dependencia (rellena)
        d.polygon([(x * esc, y * esc) for x, y in pts], fill=fill, outline=fill)

    def ROMBO(cx, cy, r=12, fill="white"):
        d.polygon([(cx * esc, cy * esc), ((cx - r) * esc, cy * esc),
                   (cx * esc, (cy - r) * esc), ((cx + r) * esc, cy * esc)],
                  fill=fill, outline="black")

    def caja(x, y, w, h, titulo, color, attrs, metodos, extra=None, sub=""):
        R(x, y, w, h, fill="white", width=2)
        R(x, y, w, 40, fill=color, width=2)
        T(x + w / 2, y + 22, titulo, anchor="mm", font=F_HEAD)
        if sub:
            T(x + w - 8, y + 22, sub, anchor="rm", font=F_SMALL)
        d.line([x * esc, (y + 40) * esc, (x + w) * esc, (y + 40) * esc], fill="black", width=2)
        yy = y + 60
        for a in attrs:
            T(x + 10, yy, a, font=F_TXT)
            yy += 19
        d.line([x * esc, yy * esc, (x + w) * esc, yy * esc], fill="black", width=2)
        yy += 20
        for m in metodos:
            T(x + 10, yy, m, font=F_TXT)
            yy += 19
        if extra:
            for e in extra:
                T(x + 10, yy, e, font=F_SMALL)
                yy += 16

    # ---------- título ----------
    T(750, 34, "Lumina Libreria - Diagrama Completo (21 clases + 3 interfaces)", anchor="mm", font=F_TIT)
    T(750, 60, "ui / service / repository / model - punteada=dependencia(usa) - -triang=realizacion",
      anchor="mm", font=F_SUB)

    # ---------- banda UI ----------
    caja(80, 100, 300, 200, "AppMenu", "#D1C4E9",
         ["- sc : Scanner"],
         ["+ iniciar() : void", "+ vender() : void", "+ elegirCliente()"],
         ["+ dashboard(), + reponer()..."])
    caja(450, 100, 300, 200, "ConsoleUI", "#D1C4E9",
         [],
         ["+ banner() : void", "+ tabla() : void", "+ tablaDe() : void"],
         ["+ tarjeta(), + exito()..."])
    # AppMenu -> ConsoleUI (adyacentes)
    DL(380, 200, 450, 200, fill="black")
    FLA([(450, 200), (440, 195), (440, 205)])
    T(415, 188, "usa", anchor="mm", font=F_SMALL)

    # ---------- banda servicios ----------
    SXS = [40, 264, 488, 712, 936, 1160]
    SW, SH, SY = 200, 220, 380
    caja(SXS[0], SY, SW, SH, "ProductoService", "#C8E6C9", [],
         ["+ buscar() : List", "+ stockBajo() : List"], ["+ porCategoria()..."])
    caja(SXS[1], SY, SW, SH, "VentaService", "#C8E6C9",
         ["- politica : PoliticaDesc."],
         ["+ crearVenta() : Venta", "+ calcularDescuento()"])
    caja(SXS[2], SY, SW, SH, "IPoliticaDescuento", "#E8EAF6",
         ["«interface»", "(contrato, sin codigo)"],
         ["+ calcular() : double"])
    caja(SXS[3], SY, SW, SH, "DescuentoEstandar", "#C8E6C9", [],
         ["+ calcular() : double"], ["realiza PoliticaDesc."])
    caja(SXS[4], SY, SW, SH, "RecomendacionService", "#C8EAF6", [],
         ["+ recomendar() : List", "+ topVentas() : List"])
    caja(SXS[5], SY, SW, SH, "ReporteService", "#C8E6C9", [],
         ["+ ingresosTotales()", "+ dashboard() : void"])
    # bus AppMenu -> servicios (y340, callejón vacío entre bandas).
    # OJO: el bus cubre x140-1260 para alcanzar a ProductoService (izq. del todo).
    LN([(230, 300), (230, 340)])
    LN([(140, 340), (1260, 340)])
    for cx in (140, 364, 588, 812, 1036, 1260):
        LN([(cx, 340), (cx, 380)])
    T(250, 330, "usa", anchor="mm", font=F_SMALL)
    # VentaService -> PoliticaDescuento (adyacentes)
    DL(464, 490, 488, 490, fill="black")
    FLA([(488, 490), (478, 485), (478, 495)])
    T(476, 478, "usa", anchor="mm", font=F_SMALL)
    # DescuentoEstandar - -▷ PoliticaDescuento (adyacentes)
    DL(712, 490, 688, 490)
    TRH([(688, 490), (700, 484), (700, 496)])
    # ReporteService -> RecomendacionService (adyacentes)
    DL(1160, 490, 1136, 490, fill="black")
    FLA([(1136, 490), (1146, 485), (1146, 495)])
    T(1148, 478, "usa", anchor="mm", font=F_SMALL)

    # ---------- Database ----------
    caja(950, 680, 320, 200, "Database", "#FFE0B2",
         ["- productos / clientes", "- ventas : List"],
         ["+ getInstancia()", "+ buscarProductoPorId()"],
         ["singleton + listas (BD temporal)"])
    # bus servicios -> Database (y640, callejón vacío)
    LN([(140, 600), (140, 640), (1260, 640)])
    for cx in (140, 364, 588, 812, 1036, 1260):
        LN([(cx, 600), (cx, 640)])
    LN([(1110, 640), (1110, 680)])
    FLA([(1110, 680), (1105, 670), (1115, 670)])
    T(1130, 648, "usa", anchor="mm", font=F_SMALL)

    # ---------- modelo pegado (recorte verificado del preliminar) ----------
    pre = Image.open(prelim).convert("RGB")
    # recorte en px (el preliminar es esc=2): se excluye título y pie.
    # El recorte cubre local y90-1275 (1185 uds) y se pega 1:1 en (150,940):
    # punto global = (150 + lx, 850 + ly). Los "almacena" usan esa fórmula.
    crop = pre.crop((0, 90 * 2, 1200 * 2, 1275 * 2)).resize((1200, 1185), Image.LANCZOS)
    img.paste(crop.resize((int(1200 * esc), int(1185 * esc)), Image.LANCZOS),
              (int(150 * esc), int(940 * esc)))
    # marco del bloque modelo
    R(150, 940, 1200, 1185, outline="#777777", width=1)
    T(160, 932, "model (ver diagrama-preliminar.png)", font=F_SMALL, fill="#777777")

    # ---------- Database almacena -> modelo (3 rombos, sin pisar cajas) ----------
    # Cliente: entra por borde izquierdo (190,1400 global)
    ROMBO(950, 760)
    LN([(950, 760), (120, 760), (120, 1400), (190, 1400)])
    T(880, 748, "almacena", anchor="mm", font=F_SMALL)
    # Producto: sube a su borde superior global (680,940)
    LN([(680, 940), (680, 900), (1050, 900)])
    ROMBO(1050, 900)
    T(1105, 924, "almacena", anchor="mm", font=F_SMALL)
    # Venta: callejon izquierdo x=120 hasta la fila libre y=1900, entra por borde izq.
    ROMBO(950, 850)
    LN([(950, 850), (120, 850), (120, 1900), (530, 1900)])
    T(880, 838, "almacena", anchor="mm", font=F_SMALL)

    # ---------- mini leyenda ----------
    R(1150, 100, 300, 180, fill="#FAFAFA", outline="#999999", width=1)
    T(1162, 124, "Leyenda", font=F_HEAD)
    T(1162, 148, "punteada = usa (dependencia)", font=F_SMALL)
    T(1162, 166, "- -triang = realizacion", font=F_SMALL)
    T(1162, 184, "rombo blanco = agregacion", font=F_SMALL)
    T(1162, 202, "bus = reparte a varias cajas", font=F_SMALL)
    T(1162, 220, "sin cruces sobre cajas", font=F_SMALL)

    T(750, 2190, "Valery Morales  -  Camilo Romero  -  Miguel Quiroga   |   Elementos de Programacion Orientada a Objetos   |   Universidad del Tolima",
      anchor="mm", font=F_SUB, fill="#333333")

    img.save(out)
    print(f"OK: {out} ({img.size[0]}x{img.size[1]})")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
