/**
 * Carga el pedido desde el localStorage al cargar la página.
 */
document.addEventListener("DOMContentLoaded", function () {
    cargarPedido();
});

/**
 * Obtiene el pedido almacenado, lo agrupa por tipo de pizza y lo muestra en la página.
 */
function cargarPedido() {
    const pedido = JSON.parse(localStorage.getItem("pedido")) || [];
    const container = document.getElementById("pedidoContainer");

    container.innerHTML = "";

    if (pedido.length === 0) {
        container.innerHTML = "<p>No hay pizzas en el pedido.</p>";
        return;
    }

    const pedidoAgrupado = [];
    const pedidoMap = new Map();

    pedido.forEach(item => {
        if (!pedidoMap.has(item.pizzaNombre)) {
            pedidoMap.set(item.pizzaNombre, {
                nombre: item.pizzaNombre,
                cantidad: 1,
                precio: item.precio
            });
            pedidoAgrupado.push(pedidoMap.get(item.pizzaNombre));
        } else {
            pedidoMap.get(item.pizzaNombre).cantidad++;
        }
    });

    pedidoAgrupado.forEach(item => {
        const div = document.createElement("div");
        div.classList.add("pedido-item");


        div.innerHTML = `
            <h3 class = "item-order">${item.nombre}</h3>
            <p class = "item-order">Precio unitario: ${item.precio.toFixed(2)}€</p>
            <p class = "item-order">Subtotal: <span id="subtotal-${item.nombre}">${(item.precio * item.cantidad).toFixed(2)}</span>€</p>
            <div class="cantidad-control">
                <button onclick="modificarCantidad('${item.nombre}', -1)">➖</button>
                <span id="cantidad-${item.nombre}">${item.cantidad}</span>
                <button onclick="modificarCantidad('${item.nombre}', 1)">➕</button>
            </div>
            <button onclick="eliminarPizza('${item.nombre}')">🗑️ Eliminar</button>
            <hr>
        `;
        container.appendChild(div);
    });

    let totalPedido = pedido.reduce((sum, item) => sum + item.precio, 0).toFixed(2);

    const totalDiv = document.createElement("div");
    totalDiv.innerHTML = `<h3>Total del pedido: ${totalPedido}€</h3>`;
    container.appendChild(totalDiv);
}

/**
 * Modifica la cantidad de una pizza en el pedido.
 * @param {string} pizzaNombre - Nombre de la pizza.
 * @param {number} cambio - Valor a modificar (+1 para aumentar, -1 para disminuir).
 */
function modificarCantidad(pizzaNombre, cambio) {
    let pedido = JSON.parse(localStorage.getItem("pedido")) || [];

    if (cambio === -1) {
        const index = pedido.findIndex(item => item.pizzaNombre === pizzaNombre);
        if (index !== -1) {
            pedido.splice(index, 1); // Elimina una unidad de la pizza
        }
    } else {
        const pizza = pedido.find(item => item.pizzaNombre === pizzaNombre);
        if (pizza) {
            pedido.push({ ...pizza }); // Agrega otra unidad de la misma pizza
        }
    }

    localStorage.setItem("pedido", JSON.stringify(pedido));
    cargarPedido();
}

/**
 * Elimina todas las unidades de una pizza del pedido.
 * @param {string} pizzaNombre - Nombre de la pizza a eliminar.
 */
function eliminarPizza(pizzaNombre) {
    let pedido = JSON.parse(localStorage.getItem("pedido")) || [];
    pedido = pedido.filter(item => item.pizzaNombre !== pizzaNombre);

    localStorage.setItem("pedido", JSON.stringify(pedido));
    cargarPedido();
}

/**
 * Envía el pedido a la API.
 */
async function enviarPedido() {
    const token = localStorage.getItem("token");
    if (!token) {
        alert("Debes iniciar sesión para hacer un pedido.");
        return;
    }

    const tokenData = getDecodedToken();
    const pedido = JSON.parse(localStorage.getItem("pedido")) || [];

    if (pedido.length === 0) {
        alert("No hay pizzas en el pedido.");
        return;
    }

    // Construye la estructura del pedido
    const pizzas = pedido.map(item => ({
        nombre: item.pizzaNombre,
        precio: item.precio
    }));

    const total = pizzas.reduce((sum, pizza) => sum + pizza.precio, 0).toFixed(2);

    const pedidoData = {
        clienteUsername: tokenData.sub,
        pizzas: pizzas,
        total: total,
        fecha: new Date().toISOString(),
        estado: "PENDIENTE"
    };

    // Enviar el pedido a la API
    const response = await fetch("http://localhost:8080/api/pedidos", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(pedidoData)
    });

    if (response.ok) {
        alert("Pedido realizado con éxito!");
        localStorage.removeItem("pedido");
        window.location.href = "/auth/mis_pedidos";
    } else {
        alert("Error al realizar el pedido.");
    }
}


/**
 * Vacía el carrito eliminando todos los elementos del localStorage y recargando la vista.
 */
function vaciarCarrito() {
    // Elimina el pedido del localStorage
    localStorage.removeItem("pedido");

    // Recarga la vista del carrito
    cargarPedido();
}

/**
 * Decodifica el token JWT almacenado para obtener la información del usuario.
 * @returns {Object} Datos decodificados del token.
 */
function getDecodedToken() {
    const token = localStorage.getItem("token");
    if (!token) {
        console.log("No hay token almacenado.");
        return;
    }

    const payload = token.split(".")[1];
    const decodedPayload = atob(payload);
    return JSON.parse(decodedPayload);
}
