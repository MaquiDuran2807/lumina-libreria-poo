# -*- coding: utf-8 -*-
"""Genera un mapa mental estilo Canva (SVG) sobre el Polimorfismo en la POO.
Fuente: tareas/tuto4/Asignacion polimorfica.pdf
Ramas: Asignacion polimerfica, Tipos estaticos y dinamicos, Herencia y sistemas de tipos,
Compatibilidad de tipos (Liskov), Sobrecarga de metodos, Sobrescritura de metodos.
Cada vineta es una idea completa y coherente (no fragmentos de oracion).
"""

CARD_W = 700
FONT = 'Trebuchet MS, Verdana, Arial, sans-serif'

BRANCHES = [
    {
        "title": "1. Asignación polimórfica",
        "color": "#FF6B6B", "dark": "#C0392B", "side": "right",
        "bullets": [
            "Una variable de tipo general (clase padre) puede guardar objetos de sus clases hijas.",
            "El compilador ve un tipo estático, pero en ejecución manda el tipo real del objeto.",
            "Ejemplo: una variable Animal puede referirse a un Perro o a un Gato.",
            "Al llamar hacerSonido(), cada objeto responde de forma distinta.",
            "Favorece la reutilización y la extensibilidad del código.",
        ],
    },
    {
        "title": "2. Tipos estáticos y dinámicos",
        "color": "#4ECDC4", "dark": "#0E8074", "side": "right",
        "bullets": [
            "El tipo estático se declara en el código fuente y el compilador lo verifica.",
            "El tipo dinámico se adquiere en ejecución y decide qué implementación corre.",
            "Conviven: dan seguridad al compilar y flexibilidad al ejecutar.",
            "Ejemplo: una variable Figura puede ser un Círculo o un Cuadrado.",
        ],
    },
    {
        "title": "3. Herencia y sistemas de tipos",
        "color": "#FFD93D", "dark": "#B8860B", "side": "right",
        "bullets": [
            "Las subclases heredan atributos y comportamientos de la superclase.",
            "Cada subclase puede sobrescribir métodos para especializar su comportamiento.",
            "La jerarquía comparte una estructura común y permite especializar cada clase.",
            "Un objeto de subclase puede tratarse como objeto de su superclase.",
        ],
    },
    {
        "title": "4. Compatibilidad de tipos",
        "subtitle": "(Principio de Sustitución de Liskov)",
        "color": "#6C5CE7", "dark": "#4834B0", "side": "left",
        "bullets": [
            "Una subclase puede sustituir a su superclase sin romper el programa.",
            "Los objetos de la clase base se reemplazan por objetos de sus subclases.",
            "Se conservan las propiedades deseables del sistema.",
            "Se conoce como Principio de Sustitución de Liskov (Liskov & Wing, 1994).",
        ],
    },
    {
        "title": "5. Sobrecarga de métodos",
        "color": "#00B894", "dark": "#00795F", "side": "left",
        "bullets": [
            "Varios métodos comparten el mismo nombre pero tienen distintos parámetros.",
            "Se resuelve en tiempo de compilación, según los argumentos de la llamada.",
            "Ejemplo: imprimir(int), imprimir(String) e imprimir(Object).",
            "Mejora la legibilidad y reduce la duplicación de código.",
        ],
    },
    {
        "title": "6. Sobrescritura de métodos",
        "subtitle": "(Override)",
        "color": "#E17055", "dark": "#A8432B", "side": "left",
        "bullets": [
            "La subclase redefine un método heredado de la superclase.",
            "Se resuelve en tiempo de ejecución, según el tipo real del objeto.",
            "Es lo opuesto a la sobrecarga, que se resuelve al compilar.",
            "Permite que cada clase aporte su propia versión del comportamiento.",
        ],
    },
]

LINE_H = 27          # alto de cada linea de texto
TITLE_LH = 30        # alto de linea del titulo
BODY_TOP = 22
BULLET_GAP = 9
BODY_BOTTOM = 26
TITLE_PAD = 26
TITLE_WRAP = 30      # caracteres por linea de titulo
BULLET_WRAP = 60     # caracteres por linea de vineta
GAP = 46             # separacion vertical entre tarjetas
Y0 = 210             # Y de la primera tarjeta
FOOT = 90


def esc(s):
    return (s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
             .replace('"', "&quot;"))


def wrap(text, width):
    words = text.split()
    out, line = [], ""
    for w in words:
        if len(line) + len(w) + (1 if line else 0) <= width:
            line = (line + " " + w).strip()
        else:
            if line:
                out.append(line)
            line = w
    if line:
        out.append(line)
    return out


# ---- 1) Precalcular lineas envueltas y alto de cada tarjeta ----
for b in BRANCHES:
    b["title_lines"] = wrap(b["title"], TITLE_WRAP)
    title_h = TITLE_PAD + len(b["title_lines"]) * TITLE_LH + (14 if "subtitle" in b else 8)
    b["title_h"] = title_h
    b["wrapped"] = [(bul, wrap(bul, BULLET_WRAP)) for bul in b["bullets"]]
    body = 0
    for _, lines in b["wrapped"]:
        body += len(lines) * LINE_H
    body += (len(b["wrapped"]) - 1) * BULLET_GAP
    b["height"] = title_h + BODY_TOP + body + BODY_BOTTOM

# ---- 2) Posicionar: apilar por lado ----
def apilar(lado):
    x = 70 if lado == "left" else None
    y = Y0
    for b in BRANCHES:
        if b["side"] != lado:
            continue
        b["y"] = y
        b["x"] = x
        y += b["height"] + GAP
    return y - GAP

bottom_left = apilar("left")
bottom_right = apilar("right")
for b in BRANCHES:
    if b["side"] == "right":
        b["x"] = None  # se fija abajo

max_bottom = max(bottom_left, bottom_right)
H = int(max_bottom + FOOT)
CY = int((Y0 + max_bottom) / 2)
W = 2400
CX = W // 2

for b in BRANCHES:
    if b["side"] == "right":
        b["x"] = W - 70 - CARD_W

# ---- 3) Dibujar ----
svg = ['<?xml version="1.0" encoding="UTF-8"?>']
svg.append(f'<svg xmlns="http://www.w3.org/2000/svg" width="{W}" height="{H}" '
           f'viewBox="0 0 {W} {H}" font-family="{FONT}">')

svg.append('<defs>')
svg.append('<linearGradient id="bg" x1="0" y1="0" x2="1" y2="1">'
           '<stop offset="0%" stop-color="#F4F7FB"/>'
           '<stop offset="100%" stop-color="#E7EEF9"/></linearGradient>')
svg.append('<radialGradient id="coreGrad" cx="50%" cy="35%" r="75%">'
           '<stop offset="0%" stop-color="#7B6CF6"/>'
           '<stop offset="55%" stop-color="#4A3FD6"/>'
           '<stop offset="100%" stop-color="#2E258F"/></radialGradient>')
svg.append('<filter id="shadow" x="-20%" y="-20%" width="150%" height="150%">'
           '<feDropShadow dx="0" dy="6" stdDeviation="8" flood-color="#000000" '
           'flood-opacity="0.16"/></filter>')
svg.append('<filter id="coreShadow" x="-30%" y="-30%" width="170%" height="170%">'
           '<feDropShadow dx="0" dy="10" stdDeviation="14" flood-color="#2E258F" '
           'flood-opacity="0.35"/></filter>')
svg.append('</defs>')

svg.append(f'<rect width="{W}" height="{H}" fill="url(#bg)"/>')

svg.append(f'<text x="{CX}" y="78" text-anchor="middle" font-size="48" '
           f'font-weight="bold" fill="#2E258F">MAPA MENTAL · EL POLIMORFISMO EN LA POO</text>')
svg.append(f'<text x="{CX}" y="120" text-anchor="middle" font-size="23" '
           f'fill="#5A6473" font-style="italic">'
           f'La capacidad de que un mismo mensaje adopte "muchas formas" según el objeto</text>')

# Conectores
for b in BRANCHES:
    card_cy = b["y"] + b["height"] / 2
    if b["side"] == "right":
        x1, y1, x2, y2 = CX + 215, CY, b["x"], card_cy
    else:
        x1, y1, x2, y2 = CX - 215, CY, b["x"] + CARD_W, card_cy
    mx = (x1 + x2) / 2
    path = f'M {x1} {y1} C {mx} {y1}, {mx} {y2}, {x2} {y2}'
    svg.append(f'<path d="{path}" fill="none" stroke="{b["color"]}" '
               f'stroke-width="6" stroke-linecap="round" opacity="0.85"/>')

# Nucleo
svg.append(f'<ellipse cx="{CX}" cy="{CY}" rx="220" ry="134" fill="url(#coreGrad)" '
           f'filter="url(#coreShadow)"/>')
svg.append(f'<ellipse cx="{CX}" cy="{CY}" rx="220" ry="134" fill="none" '
           f'stroke="#FFFFFF" stroke-width="4" opacity="0.6"/>')
svg.append(f'<text x="{CX}" y="{CY-38}" text-anchor="middle" font-size="42" '
           f'font-weight="bold" fill="#FFFFFF">POLIMORFISMO</text>')
svg.append(f'<text x="{CX}" y="{CY+8}" text-anchor="middle" font-size="31" '
           f'font-weight="bold" fill="#D9D4FF">EN LA POO</text>')
svg.append(f'<text x="{CX}" y="{CY+58}" text-anchor="middle" font-size="21" '
           f'fill="#CFC8FF" font-style="italic">'
           f'griego: polymorphos = "muchas formas"</text>')

# Tarjetas
for b in BRANCHES:
    x, y, w, h, color = b["x"], b["y"], CARD_W, b["height"], b["color"]
    th = b["title_h"]

    svg.append(f'<rect x="{x}" y="{y}" width="{w}" height="{h}" rx="22" '
               f'fill="#FFFFFF" filter="url(#shadow)"/>')
    svg.append(f'<path d="M {x} {y+22} Q {x} {y} {x+22} {y} L {x+w-22} {y} '
               f'Q {x+w} {y} {x+w} {y+22} L {x+w} {y+th} L {x} {y+th} Z" fill="{color}"/>')
    svg.append(f'<rect x="{x}" y="{y+th}" width="10" height="{h-th}" '
               f'fill="{color}" opacity="0.9"/>')

    # Titulo
    ty = y + TITLE_PAD
    for tl in b["title_lines"]:
        svg.append(f'<text x="{x+26}" y="{ty}" font-size="27" font-weight="bold" '
                   f'fill="#FFFFFF">{esc(tl)}</text>')
        ty += TITLE_LH
    if "subtitle" in b:
        svg.append(f'<text x="{x+26}" y="{ty+2}" font-size="15" fill="#FFFFFF" '
                   f'opacity="0.9" font-style="italic">{esc(b["subtitle"])}</text>')

    # Vinetas con sangria francesa
    cx = x + 30                     # centro del punto
    tx = x + 50                     # inicio del texto
    by = y + th + BODY_TOP + LINE_H - 8
    for _, lines in b["wrapped"]:
        svg.append(f'<circle cx="{cx}" cy="{by-6}" r="5" fill="{color}"/>')
        for i, ln in enumerate(lines):
            svg.append(f'<text x="{tx}" y="{by + i*LINE_H}" font-size="20" '
                       f'fill="#333A45">{esc(ln)}</text>')
        by += len(lines) * LINE_H + BULLET_GAP

# Pie
svg.append(f'<text x="70" y="{H-32}" font-size="16" fill="#8A93A3">'
           f'Elementos de Programación Orientada a Objetos · Grupo 1</text>')
svg.append(f'<text x="{W-70}" y="{H-32}" text-anchor="end" font-size="16" '
           f'fill="#8A93A3">Fuente: Asignación polimórfica · Guía Unidad 4 (POO)</text>')

svg.append('</svg>')

out_svg = r"D:\desarrollos\proyecto poo\tareas\tuto4\mapa-mental-polimorfismo.svg"
with open(out_svg, "w", encoding="utf-8") as f:
    f.write("\n".join(svg))
print("SVG generado:", out_svg, "| H =", H)
