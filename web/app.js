"use strict";

const CATEGORIAS = {
  LIBRO_FICCION: "Libro · Ficción",
  LIBRO_INFANTIL: "Libro · Infantil",
  LIBRO_ACADEMICO: "Libro · Académico",
  UTILES: "Útiles Escolares",
  ARTE: "Arte y Manualidades",
  REVISTA: "Revistas",
};

const NIVELES = [
  { key: "DIAMANTE", min: 1000, dto: 0.15 },
  { key: "ORO", min: 500, dto: 0.10 },
  { key: "PLATA", min: 200, dto: 0.05 },
  { key: "BRONCE", min: 0, dto: 0.0 },
];

const IVA = 0.19;

const DB = {
  productos: [
    { id: "L-001", nombre: "Cien años de soledad", categoria: "LIBRO_FICCION", pc: 28000, pv: 45000, stock: 13, min: 5, detalle: "Gabriel García Márquez · Sudamericana" },
    { id: "L-002", nombre: "El Principito", categoria: "LIBRO_INFANTIL", pc: 15000, pv: 28000, stock: 18, min: 5, detalle: "Antoine de Saint-Exupéry · Salamandra" },
    { id: "L-003", nombre: "Clean Code", categoria: "LIBRO_ACADEMICO", pc: 60000, pv: 95000, stock: 7, min: 3, detalle: "Robert C. Martin · Prentice Hall" },
    { id: "L-004", nombre: "Harry Potter y la piedra filosofal", categoria: "LIBRO_FICCION", pc: 32000, pv: 52000, stock: 11, min: 4, detalle: "J.K. Rowling · Salamandra" },
    { id: "L-005", nombre: "Hábitos Atómicos", categoria: "LIBRO_ACADEMICO", pc: 35000, pv: 59000, stock: 4, min: 5, detalle: "James Clear · Avery" },
    { id: "L-006", nombre: "La casa en Mango Street", categoria: "LIBRO_FICCION", pc: 18000, pv: 32000, stock: 2, min: 4, detalle: "Sandra Cisneros · Vintage" },
    { id: "U-001", nombre: "Mochila Escolar Pro 25L", categoria: "UTILES", pc: 55000, pv: 89900, stock: 9, min: 3, detalle: "Totto · Mochila" },
    { id: "U-002", nombre: "Set 24 Colores Profesionales", categoria: "ARTE", pc: 22000, pv: 39900, stock: 24, min: 6, detalle: "Faber-Castell · Colores" },
    { id: "U-003", nombre: "Cuaderno Cosido 100h Cuadriculado", categoria: "UTILES", pc: 8000, pv: 14900, stock: 37, min: 10, detalle: "Norma · Cuaderno" },
    { id: "U-004", nombre: "Kit Acuarelas 36 + Pinceles", categoria: "ARTE", pc: 30000, pv: 54900, stock: 3, min: 5, detalle: "Pelikan · Acuarela" },
    { id: "R-001", nombre: "National Geographic Kids", categoria: "REVISTA", pc: 12000, pv: 19900, stock: 17, min: 5, detalle: "Marzo 2026 · Mensual" },
    { id: "R-002", nombre: "Muy Interesante Ciencia", categoria: "REVISTA", pc: 10000, pv: 17900, stock: 14, min: 5, detalle: "N° 412 · Mensual" },
  ],
  clientes: [
    { id: "C-001", nombre: "Sofía Ramírez", email: "sofia@mail.com", tel: "3001112233", puntos: 1200, compras: 14 },
    { id: "C-002", nombre: "Diego Torres", email: "diego@mail.com", tel: "3114445566", puntos: 650, compras: 8 },
    { id: "C-003", nombre: "Lucía Fernández", email: "lucia@mail.com", tel: "3207778899", puntos: 180, compras: 3 },
    { id: "C-004", nombre: "Mateo García", email: "mateo@mail.com", tel: "3012223344", puntos: 40, compras: 1 },
    { id: "C-005", nombre: "Valentina Ruiz", email: "vale@mail.com", tel: "3159990011", puntos: 0, compras: 0 },
  ],
  ventas: [],
};

let ventaSeq = 1000;

function diasAtras(n) {
  return new Date(Date.now() - n * 86400000);
}

function ventaHistorica(id, clienteId, dias, items, dto) {
  const cliente = DB.clientes.find((c) => c.id === clienteId);
  const detalles = items.map(([pid, cant]) => ({ productoId: pid, cantidad: cant }));
  DB.ventas.push({ id, clienteId, cliente, fecha: diasAtras(dias), detalles, dto: dto ?? 0.05 });
}

ventaHistorica("V-900", "C-001", 6, [["L-001", 2], ["U-002", 1]]);
ventaHistorica("V-901", "C-002", 4, [["L-003", 1], ["U-001", 1]]);
ventaHistorica("V-902", "C-001", 2, [["L-004", 1], ["U-003", 3]]);
ventaHistorica("V-903", "C-003", 1, [["L-002", 2], ["R-001", 1]]);

function producto(id) {
  return DB.productos.find((p) => p.id === id);
}

function cliente(id) {
  return DB.clientes.find((c) => c.id === id);
}

function nivelDe(puntos) {
  return NIVELES.find((n) => puntos >= n.min) || NIVELES[3];
}

function normalizar(s) {
  return (s || "").toString().toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "");
}

function dinero(v) {
  return "$" + Math.round(v).toLocaleString("es-CO");
}

function porcentaje(v) {
  return (v * 100).toFixed(0) + "%";
}

function margen(p) {
  if (p.pc <= 0) return 0;
  return ((p.pv - p.pc) / p.pc) * 100;
}

function descuentoInteligente(cl, subtotal, unidades) {
  let d = nivelDe(cl.puntos).dto;
  if (unidades >= 5) d += 0.05;
  if (subtotal > 150000) d += 0.03;
  return Math.min(d, 0.25);
}

function detalleDe(venta) {
  return venta.detalles.map((d) => {
    const p = producto(d.productoId);
    return { producto: p, cantidad: d.cantidad, precio: p ? p.pv : 0, subtotal: p ? p.pv * d.cantidad : 0 };
  });
}

function subtotalVenta(venta) {
  return detalleDe(venta).reduce((s, d) => s + d.subtotal, 0);
}

function totalUnidades(venta) {
  return venta.detalles.reduce((s, d) => s + d.cantidad, 0);
}

function totalesVenta(venta) {
  const sub = subtotalVenta(venta);
  const dto = venta.dto || 0;
  const desc = sub * dto;
  const base = sub - desc;
  const iva = base * IVA;
  return { sub, dto, desc, base, iva, total: base + iva };
}

function gananciaVenta(venta) {
  const bruta = detalleDe(venta).reduce((s, d) => s + (d.precio - d.producto.pc) * d.cantidad, 0);
  return bruta - totalesVenta(venta).desc;
}

function popularidad() {
  const mapa = {};
  DB.ventas.forEach((v) =>
    v.detalles.forEach((d) => {
      mapa[d.productoId] = (mapa[d.productoId] || 0) + d.cantidad;
    })
  );
  return mapa;
}

function buscarProductos(q) {
  if (!q || !q.trim()) return DB.productos.slice();
  const n = normalizar(q);
  return DB.productos.filter(
    (p) =>
      normalizar(p.nombre).includes(n) ||
      normalizar(p.detalle).includes(n) ||
      normalizar(CATEGORIAS[p.categoria]).includes(n) ||
      normalizar(p.id).includes(n)
  );
}

function categoriasVendidas() {
  const mapa = {};
  DB.ventas.forEach((v) =>
    detalleDe(v).forEach((d) => {
      const k = d.producto.categoria;
      mapa[k] = (mapa[k] || 0) + d.subtotal;
    })
  );
  return Object.entries(mapa)
    .map(([k, v]) => ({ cat: k, label: CATEGORIAS[k], valor: v }))
    .sort((a, b) => b.valor - a.valor);
}

function ingresosTotales() {
  return DB.ventas.reduce((s, v) => s + totalesVenta(v).total, 0);
}

function gananciaTotal() {
  return DB.ventas.reduce((s, v) => s + gananciaVenta(v), 0);
}

function unidadesTotales() {
  return DB.ventas.reduce((s, v) => s + totalUnidades(v), 0);
}

const VISTAS = {
  dashboard: { titulo: "Dashboard", sub: "Resumen inteligente de la librería" },
  inventario: { titulo: "Inventario", sub: "Libros, útiles/arte y revistas en memoria" },
  pos: { titulo: "Punto de venta", sub: "Carrito, descuento inteligente, IVA 19% y factura" },
  clientes: { titulo: "Clientes y fidelización", sub: "Niveles Bronce → Plata → Oro → Diamante" },
  recomendaciones: { titulo: "Recomendaciones inteligentes", sub: "Por categoría, autor/marca e historial" },
  alertas: { titulo: "Alertas de stock", sub: "Productos que necesitan reposición" },
};

let vistaActual = "dashboard";
let terminoBusqueda = "";
let filtroCategoria = "";
let carrito = [];
let baseReco = "L-001";

const content = document.getElementById("content");
const viewTitle = document.getElementById("viewTitle");
const viewSubtitle = document.getElementById("viewSubtitle");

function irA(vista) {
  if (!VISTAS[vista]) vista = "dashboard";
  vistaActual = vista;
  if (vista !== "inventario") terminoBusqueda = "";
  if (location.hash.slice(1) !== vista) {
    try { history.replaceState(null, "", "#" + vista); } catch (e) { location.hash = vista; }
  }
  document.querySelectorAll(".nav-item").forEach((b) =>
    b.classList.toggle("active", b.dataset.view === vista)
  );
  viewTitle.textContent = VISTAS[vista].titulo;
  viewSubtitle.textContent = VISTAS[vista].sub;
  render();
  document.getElementById("sidebar").classList.remove("open");
}

function render() {
  if (vistaActual === "dashboard") renderDashboard();
  else if (vistaActual === "inventario") renderInventario();
  else if (vistaActual === "pos") renderPOS();
  else if (vistaActual === "clientes") renderClientes();
  else if (vistaActual === "recomendaciones") renderRecomendaciones();
  else if (vistaActual === "alertas") renderAlertas();
}

function estadoStock(p) {
  if (p.stock === 0) return '<span class="pill out">Agotado</span>';
  if (p.stock <= p.min) return '<span class="pill low">⚠ Bajo</span>';
  return '<span class="pill ok">OK</span>';
}

/* ===================== DASHBOARD ===================== */
function renderDashboard() {
  const ingresos = ingresosTotales();
  const ganancia = gananciaTotal();
  const ticket = DB.ventas.length ? ingresos / DB.ventas.length : 0;
  const unidades = unidadesTotales();
  const cats = categoriasVendidas();
  const maxCat = Math.max(1, ...cats.map((c) => c.valor));

  const ultimas = DB.ventas.slice().sort((a, b) => b.fecha - a.fecha).slice(0, 5);

  content.innerHTML = `
    <div class="grid cols-4">
      ${kpi("Ingresos totales", dinero(ingresos), DB.ventas.length + " ventas registradas", "💰", "#12a150")}
      ${kpi("Ganancia estimada", dinero(ganancia), "precio − costo − descuentos", "📈", "#4a3fd6")}
      ${kpi("Ticket promedio", dinero(ticket), "por venta", "🧾", "#e17055")}
      ${kpi("Unidades vendidas", unidades, "en total", "📦", "#12b3a3")}
    </div>

    <div class="grid two-thirds mt-24">
      <div class="card">
        <div class="section-title">Ventas por categoría</div>
        ${
          cats.length
            ? `<div class="bars">${cats
                .map(
                  (c) => `
              <div class="bar-row">
                <span>${c.label}</span>
                <div class="bar-track"><div class="bar-fill" style="width:${(c.valor / maxCat) * 100}%"></div></div>
                <span class="bar-val">${dinero(c.valor)}</span>
              </div>`
                )
                .join("")}</div>`
            : '<div class="empty">Aún no hay ventas</div>'
        }
      </div>

      <div class="card">
        <div class="section-title">Top productos (histórico)</div>
        <div class="table-wrap">
          <table>
            <tbody>
              ${topProductos(5)
                .map(
                  (t) => `
                <tr>
                  <td><strong>${t.producto.nombre}</strong><div class="hint">${t.producto.id}</div></td>
                  <td class="num">${t.unidades} u.</td>
                </tr>`
                )
                .join("")}
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <div class="card mt-24">
      <div class="section-title">Últimas ventas</div>
      <div class="table-wrap">
        <table>
          <thead><tr><th>ID</th><th>Cliente</th><th>Fecha</th><th class="num">Unds</th><th class="num">Total</th></tr></thead>
          <tbody>
            ${ultimas
              .map((v) => {
                const t = totalesVenta(v);
                return `<tr>
                  <td><strong>${v.id}</strong></td>
                  <td>${v.cliente ? v.cliente.nombre : "—"}</td>
                  <td>${v.fecha.toLocaleDateString("es-CO", { day: "2-digit", month: "short", year: "numeric" })}</td>
                  <td class="num">${totalUnidades(v)}</td>
                  <td class="num"><strong>${dinero(t.total)}</strong></td>
                </tr>`;
              })
              .join("")}
          </tbody>
        </table>
      </div>
    </div>
  `;
}

function kpi(label, value, sub, ico, color) {
  return `
    <div class="kpi" style="--kpi-color:${color}">
      <div class="kpi-ico">${ico}</div>
      <div class="kpi-label">${label}</div>
      <div class="kpi-value">${value}</div>
      <div class="kpi-sub">${sub}</div>
    </div>`;
}

function topProductos(n) {
  const pop = popularidad();
  return DB.productos
    .map((p) => ({ producto: p, unidades: pop[p.id] || 0 }))
    .filter((t) => t.unidades > 0)
    .sort((a, b) => b.unidades - a.unidades)
    .slice(0, n);
}

/* ===================== INVENTARIO ===================== */
function renderInventario() {
  const lista = buscarProductos(terminoBusqueda).filter(
    (p) => !filtroCategoria || p.categoria === filtroCategoria
  );

  content.innerHTML = `
    <div class="toolbar">
      <div class="search">
        <input id="buscar" type="text" placeholder="Buscar por nombre, autor, marca, categoría o ID…" value="${escapar(terminoBusqueda)}" />
      </div>
      <select id="filtroCat">
        <option value="">Todas las categorías</option>
        ${Object.entries(CATEGORIAS)
          .map(([k, v]) => `<option value="${k}" ${filtroCategoria === k ? "selected" : ""}>${v}</option>`)
          .join("")}
      </select>
    </div>

    <div class="card tight">
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>ID</th><th>Producto</th><th>Categoría</th><th class="num">Precio</th>
              <th class="num">Stock</th><th>Estado</th><th class="num">Margen</th>
            </tr>
          </thead>
          <tbody>
            ${
              lista.length
                ? lista
                    .map(
                      (p) => `
              <tr>
                <td><strong>${p.id}</strong></td>
                <td>${p.nombre}<div class="hint">${p.detalle}</div></td>
                <td><span class="pill cat">${CATEGORIAS[p.categoria]}</span></td>
                <td class="num">${dinero(p.pv)}</td>
                <td class="num">${p.stock}</td>
                <td>${estadoStock(p)}</td>
                <td class="num">${margen(p).toFixed(1)}%</td>
              </tr>`
                    )
                    .join("")
                : '<tr><td colspan="7"><div class="empty">Sin coincidencias para la búsqueda</div></td></tr>'
            }
          </tbody>
        </table>
      </div>
      <div class="hint">↳ ${lista.length} producto(s) · la búsqueda ignora mayúsculas y tildes</div>
    </div>
  `;

  const input = document.getElementById("buscar");
  input.addEventListener("input", (e) => {
    terminoBusqueda = e.target.value;
    renderInventario();
    const again = document.getElementById("buscar");
    again.focus();
    again.setSelectionRange(again.value.length, again.value.length);
  });
  document.getElementById("filtroCat").addEventListener("change", (e) => {
    filtroCategoria = e.target.value;
    renderInventario();
  });
}

/* ===================== POS ===================== */
function renderPOS() {
  const cl = cliente("C-001");
  const subtotal = carrito.reduce((s, i) => {
    const p = producto(i.productoId);
    return s + (p ? p.pv * i.cantidad : 0);
  }, 0);
  const unidades = carrito.reduce((s, i) => s + i.cantidad, 0);
  const dto = subtotal > 0 ? descuentoInteligente(cl, subtotal, unidades) : 0;
  const desc = subtotal * dto;
  const base = subtotal - desc;
  const iva = base * IVA;
  const total = base + iva;

  content.innerHTML = `
    <div class="pos-grid">
      <div class="card tight">
        <div class="section-title">Catálogo · clic para agregar</div>
        <div class="search" style="margin-bottom:12px">
          <input id="posBuscar" type="text" placeholder="Buscar producto para vender…" value="${escapar(terminoBusqueda)}" />
        </div>
        <div class="table-wrap">
          <table>
            <thead><tr><th>ID</th><th>Producto</th><th class="num">Precio</th><th class="num">Stock</th></tr></thead>
            <tbody>
              ${buscarProductos(terminoBusqueda)
                .map(
                  (p) => `
                <tr class="product-pick" data-add="${p.id}">
                  <td><strong>${p.id}</strong></td>
                  <td>${p.nombre}</td>
                  <td class="num">${dinero(p.pv)}</td>
                  <td class="num">${p.stock}</td>
                </tr>`
                )
                .join("")}
            </tbody>
          </table>
        </div>
        <div class="hint">Haz clic en una fila para añadir una unidad al carrito</div>
      </div>

      <div class="card">
        <div class="section-title">Carrito</div>
        <div class="field" style="margin-bottom:14px">
          <label>Cliente</label>
          <select id="posCliente">
            ${DB.clientes
              .map(
                (c) =>
                  `<option value="${c.id}" ${c.id === "C-001" ? "selected" : ""}>${c.nombre} · ${nivelDe(c.puntos).key} (${c.puntos} pts)</option>`
              )
              .join("")}
          </select>
        </div>

        <div class="cart-list">
          ${
            carrito.length
              ? carrito
                  .map((i) => {
                    const p = producto(i.productoId);
                    return `
                <div class="cart-item">
                  <div>
                    <div class="ci-name">${p.nombre}</div>
                    <div class="ci-id">${p.id} · ${dinero(p.pv)} c/u</div>
                  </div>
                  <div class="qty">
                    <button data-dec="${p.id}">−</button>
                    <span>${i.cantidad}</span>
                    <button data-inc="${p.id}">+</button>
                  </div>
                  <div style="text-align:right">
                    <strong>${dinero(p.pv * i.cantidad)}</strong>
                    <div><button class="btn btn-danger btn-mini" data-del="${p.id}">✕</button></div>
                  </div>
                </div>`;
                  })
                  .join("")
              : '<div class="empty">El carrito está vacío</div>'
          }
        </div>

        <div class="mt-16">
          <div class="summary-row"><span>Subtotal</span><span>${dinero(subtotal)}</span></div>
          <div class="summary-row"><span>Descuento inteligente (${porcentaje(dto)})</span><span class="discount">− ${dinero(desc)}</span></div>
          <div class="summary-row"><span>IVA 19%</span><span>${dinero(iva)}</span></div>
          <div class="summary-row total"><span>Total</span><span>${dinero(total)}</span></div>
        </div>

        <button class="btn btn-primary mt-16" id="cobrar" style="width:100%" ${carrito.length ? "" : "disabled"}>
          Cobrar y generar factura
        </button>
        ${
          dto > 0
            ? `<div class="hint">Descuento aplicado por nivel ${nivelDe(cl.puntos).key}${unidades >= 5 ? " + volumen (5+ unds)" : ""}${subtotal > 150000 ? " + monto (> $150.000)" : ""}.</div>`
            : ""
        }
      </div>
    </div>
  `;

  document.querySelectorAll("[data-add]").forEach((el) =>
    el.addEventListener("click", () => agregarAlCarrito(el.dataset.add))
  );
  document.querySelectorAll("[data-inc]").forEach((el) =>
    el.addEventListener("click", () => cambiarCantidad(el.dataset.inc, 1))
  );
  document.querySelectorAll("[data-dec]").forEach((el) =>
    el.addEventListener("click", () => cambiarCantidad(el.dataset.dec, -1))
  );
  document.querySelectorAll("[data-del]").forEach((el) =>
    el.addEventListener("click", () => {
      carrito = carrito.filter((i) => i.productoId !== el.dataset.del);
      renderPOS();
    })
  );
  document.getElementById("posCliente").addEventListener("change", renderPOS);
  document.getElementById("cobrar").addEventListener("click", cobrar);
  const pb = document.getElementById("posBuscar");
  pb.addEventListener("input", (e) => {
    terminoBusqueda = e.target.value;
    renderPOS();
    const again = document.getElementById("posBuscar");
    again.focus();
    again.setSelectionRange(again.value.length, again.value.length);
  });
}

function agregarAlCarrito(id) {
  const p = producto(id);
  if (!p) return;
  if (p.stock <= 0) {
    toast("Producto agotado", "error");
    return;
  }
  const item = carrito.find((i) => i.productoId === id);
  const enCarrito = item ? item.cantidad : 0;
  if (enCarrito + 1 > p.stock) {
    toast("Stock insuficiente (disponible: " + p.stock + ")", "error");
    return;
  }
  if (item) item.cantidad += 1;
  else carrito.push({ productoId: id, cantidad: 1 });
  renderPOS();
}

function cambiarCantidad(id, delta) {
  const item = carrito.find((i) => i.productoId === id);
  if (!item) return;
  const p = producto(id);
  const nueva = item.cantidad + delta;
  if (nueva <= 0) {
    carrito = carrito.filter((i) => i.productoId !== id);
  } else if (nueva > p.stock) {
    toast("Stock insuficiente (disponible: " + p.stock + ")", "error");
    return;
  } else {
    item.cantidad = nueva;
  }
  renderPOS();
}

function cobrar() {
  if (!carrito.length) return;
  const cl = cliente(document.getElementById("posCliente").value);
  const subtotal = carrito.reduce((s, i) => s + producto(i.productoId).pv * i.cantidad, 0);
  const unidades = carrito.reduce((s, i) => s + i.cantidad, 0);
  const dto = descuentoInteligente(cl, subtotal, unidades);

  for (const i of carrito) {
    if (producto(i.productoId).stock < i.cantidad) {
      toast("Stock insuficiente al confirmar", "error");
      return;
    }
  }

  const venta = {
    id: "V-" + ++ventaSeq,
    clienteId: cl.id,
    cliente: cl,
    fecha: new Date(),
    detalles: carrito.map((i) => ({ productoId: i.productoId, cantidad: i.cantidad })),
    dto,
  };
  carrito.forEach((i) => (producto(i.productoId).stock -= i.cantidad));
  const t = totalesVenta(venta);
  cl.puntos += Math.floor(t.total / 10000);
  cl.compras += 1;

  DB.ventas.push(venta);
  mostrarFactura(venta);
  carrito = [];
  renderPOS();
}

function mostrarFactura(v) {
  const t = totalesVenta(v);
  const detalles = detalleDe(v);
  const overlay = document.getElementById("modalOverlay");
  document.getElementById("modalBody").innerHTML = `
    <div class="invoice-head">
      <div class="inv-ico">🧾</div>
      <h2>Factura ${v.id}</h2>
      <p>Lumina Librería Inteligente</p>
    </div>
    <div class="invoice-meta">
      <span><strong>Cliente:</strong> ${v.cliente.nombre}</span>
      <span>${v.fecha.toLocaleString("es-CO")}</span>
    </div>
    <div class="table-wrap">
      <table>
        <thead><tr><th>Producto</th><th class="num">Cant</th><th class="num">P.Unit</th><th class="num">Subtotal</th></tr></thead>
        <tbody>
          ${detalles
            .map(
              (d) => `<tr>
                <td>${d.producto.nombre}</td>
                <td class="num">${d.cantidad}</td>
                <td class="num">${dinero(d.precio)}</td>
                <td class="num">${dinero(d.subtotal)}</td>
              </tr>`
            )
            .join("")}
        </tbody>
      </table>
    </div>
    <div class="mt-16">
      <div class="summary-row"><span>Subtotal</span><span>${dinero(t.sub)}</span></div>
      <div class="summary-row"><span>Descuento (${porcentaje(t.dto)})</span><span class="discount">− ${dinero(t.desc)}</span></div>
      <div class="summary-row"><span>IVA 19%</span><span>${dinero(t.iva)}</span></div>
      <div class="summary-row total"><span>Total</span><span>${dinero(t.total)}</span></div>
    </div>
    <div class="hint" style="text-align:center">Puntos de ${v.cliente.nombre}: ${v.cliente.puntos} (${nivelDe(v.cliente.puntos).key})</div>
  `;
  overlay.hidden = false;
}

/* ===================== CLIENTES ===================== */
function renderClientes() {
  const mejor = DB.clientes.slice().sort((a, b) => b.puntos - a.puntos)[0];
  content.innerHTML = `
    <div class="grid two-thirds">
      <div class="card tight">
        <div class="section-title">Clientes registrados</div>
        <div class="table-wrap">
          <table>
            <thead><tr><th>ID</th><th>Nombre</th><th>Nivel</th><th class="num">Puntos</th><th class="num">Compras</th><th>Teléfono</th></tr></thead>
            <tbody>
              ${DB.clientes
                .map((c) => {
                  const n = nivelDe(c.puntos);
                  return `<tr>
                    <td><strong>${c.id}</strong></td>
                    <td>${c.nombre}<div class="hint">${c.email}</div></td>
                    <td><span class="pill nivel-${n.key.toLowerCase()}">${n.key} · ${porcentaje(n.dto)}</span></td>
                    <td class="num">${c.puntos}</td>
                    <td class="num">${c.compras}</td>
                    <td>${c.tel}</td>
                  </tr>`;
                })
                .join("")}
            </tbody>
          </table>
        </div>
        <div class="hint">1 punto por cada $10.000 · niveles con descuento 0 / 5 / 10 / 15%</div>
      </div>

      <div class="card">
        <div class="section-title">🏆 Mejor cliente</div>
        <div class="reco-base">
          <div class="rb-ico">👑</div>
          <div>
            <strong>${mejor.nombre}</strong>
            <small>${nivelDe(mejor.puntos).key} · ${mejor.puntos} pts · descuento ${porcentaje(nivelDe(mejor.puntos).dto)}</small>
          </div>
        </div>
        <div class="mt-16">
          <div class="section-title">Niveles</div>
          <div class="bars">
            ${NIVELES.slice()
              .reverse()
              .map(
                (n) => `
              <div class="bar-row" style="grid-template-columns:120px 1fr 60px">
                <span><span class="pill nivel-${n.key.toLowerCase()}">${n.key}</span></span>
                <div class="bar-track"><div class="bar-fill" style="width:${n.dto * 100 / 0.15 * 100}%;background:linear-gradient(90deg,#f4b400,#e17055)"></div></div>
                <span class="bar-val">${porcentaje(n.dto)}</span>
              </div>`
              )
              .join("")}
          </div>
          <div class="hint">Desde 1.000 pts se alcanza el nivel Diamante con 15% de descuento.</div>
        </div>
      </div>
    </div>
  `;
}

/* ===================== RECOMENDACIONES ===================== */
function recomendar(baseId, max) {
  const base = producto(baseId);
  if (!base) return [];
  const pop = popularidad();
  return DB.productos
    .filter((p) => p.id !== base.id && p.stock > 0)
    .map((p) => {
      let s = 0;
      const razones = [];
      if (p.categoria === base.categoria) {
        s += 30;
        razones.push("misma categoría");
      }
      const a = (p.detalle.split(/[ ·,]+/)[0] || "").toLowerCase();
      const b = (base.detalle.split(/[ ·,]+/)[0] || "").toLowerCase();
      if (a && a === b) {
        s += 50;
        razones.push("mismo autor/marca");
      }
      const u = pop[p.id] || 0;
      if (u > 0) {
        s += u * 2;
        razones.push(u + " u. vendidas");
      }
      s += Math.floor(margen(p) / 10);
      return { producto: p, score: s, razones };
    })
    .sort((x, y) => y.score - x.score)
    .slice(0, max);
}

function renderRecomendaciones() {
  const base = producto(baseReco);
  const reco = recomendar(baseReco, 5);
  const tops = topProductos(3);

  content.innerHTML = `
    <div class="card">
      <div class="reco-head">
        <div class="field" style="min-width:280px">
          <label>Producto base</label>
          <select id="recoBase">
            ${DB.productos
              .map((p) => `<option value="${p.id}" ${p.id === baseReco ? "selected" : ""}>${p.id} · ${p.nombre}</option>`)
              .join("")}
          </select>
        </div>
        <div class="reco-base">
          <div class="rb-ico">✨</div>
          <div>
            <strong>Si te gusta "${base.nombre}"</strong>
            <small>${CATEGORIAS[base.categoria]} · ${base.detalle}</small>
          </div>
        </div>
      </div>
    </div>

    <div class="grid two-thirds mt-24">
      <div class="card tight">
        <div class="section-title">Sugerencias</div>
        <div class="table-wrap">
          <table>
            <thead><tr><th>ID</th><th>Producto</th><th>Motivo</th><th class="num">Score</th></tr></thead>
            <tbody>
              ${
                reco.length
                  ? reco
                      .map(
                        (r) => `
                <tr>
                  <td><strong>${r.producto.id}</strong></td>
                  <td>${r.producto.nombre}<div class="hint">${dinero(r.producto.pv)}</div></td>
                  <td>${r.razones.map((x) => `<span class="pill cat" style="margin:2px 2px 2px 0">${x}</span>`).join("") || '<span class="hint">desempate por margen</span>'}</td>
                  <td class="num"><span class="score-badge">${r.score}</span></td>
                </tr>`
                      )
                      .join("")
                  : '<tr><td colspan="4"><div class="empty">Sin sugerencias disponibles</div></td></tr>'
              }
            </tbody>
          </table>
        </div>
      </div>

      <div class="card">
        <div class="section-title">🔥 Top ventas históricas</div>
        <div class="table-wrap">
          <table>
            <tbody>
              ${tops
                .map(
                  (t) => `<tr>
                    <td>${t.producto.nombre}<div class="hint">${t.producto.id}</div></td>
                    <td class="num">${t.unidades} u.</td>
                  </tr>`
                )
                .join("")}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  `;

  document.getElementById("recoBase").addEventListener("change", (e) => {
    baseReco = e.target.value;
    renderRecomendaciones();
  });
}

/* ===================== ALERTAS ===================== */
function renderAlertas() {
  const bajos = DB.productos
    .filter((p) => p.stock <= p.min)
    .sort((a, b) => a.stock - b.stock);

  content.innerHTML = `
    ${
      bajos.length
        ? `<div class="alert-banner"><span class="ab-ico">⚠️</span><div><strong>${bajos.length} producto(s)</strong> por debajo del mínimo. Conviene reponer pronto.</div></div>`
        : `<div class="alert-banner ok"><span class="ab-ico">🎉</span><div>Todo el inventario está saludable.</div></div>`
    }

    <div class="card tight">
      <div class="table-wrap">
        <table>
          <thead><tr><th>ID</th><th>Producto</th><th>Categoría</th><th class="num">Stock</th><th class="num">Mínimo</th><th>Estado</th></tr></thead>
          <tbody>
            ${
              bajos.length
                ? bajos
                    .map(
                      (p) => `
              <tr>
                <td><strong>${p.id}</strong></td>
                <td>${p.nombre}<div class="hint">${p.detalle}</div></td>
                <td><span class="pill cat">${CATEGORIAS[p.categoria]}</span></td>
                <td class="num"><strong>${p.stock}</strong></td>
                <td class="num">${p.min}</td>
                <td>${estadoStock(p)}</td>
              </tr>`
                    )
                    .join("")
                : '<tr><td colspan="6"><div class="empty">Sin alertas</div></td></tr>'
            }
          </tbody>
        </table>
      </div>
    </div>
  `;
}

/* ===================== UTILIDADES ===================== */
function escapar(s) {
  return (s || "").replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
}

let toastTimer = null;
function toast(msg, tipo) {
  const el = document.getElementById("toast");
  el.textContent = msg;
  el.className = "toast" + (tipo ? " " + tipo : "");
  el.hidden = false;
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => (el.hidden = true), 2400);
}

document.getElementById("nav").addEventListener("click", (e) => {
  const btn = e.target.closest(".nav-item");
  if (btn) irA(btn.dataset.view);
});

document.getElementById("menuToggle").addEventListener("click", () => {
  document.getElementById("sidebar").classList.toggle("open");
});

document.getElementById("modalClose").addEventListener("click", () => {
  document.getElementById("modalOverlay").hidden = true;
});
document.getElementById("modalOverlay").addEventListener("click", (e) => {
  if (e.target.id === "modalOverlay") e.target.hidden = true;
});

window.addEventListener("hashchange", () => irA(location.hash.slice(1) || "dashboard"));

irA(location.hash.slice(1) || "dashboard");
