/**
 * Escucha el evento de carga del DOM para verificar si el usuario está autenticado.
 * - Si no hay un token JWT en el localStorage, redirige al usuario a la página de inicio de sesión.
 * @listens DOMContentLoaded
 */
document.addEventListener("DOMContentLoaded", () => {
    const token = localStorage.getItem("token");

    // Verifica si el usuario está autenticado
    if (!token) {
        alert("Acceso denegado. Inicia sesión.");
        window.location.href = "/auth/login";
        return;
    }
});

/**
 * Obtiene y muestra las pizzas disponibles en la página.
 * - Realiza una petición al servidor para obtener las pizzas.
 * - Muestra las pizzas en la página con opciones para editar y eliminar.
 * - Si no hay pizzas, muestra un mensaje indicándolo.
 * @async
 */
function mostrarPizzas() {
    fetch("/api/pizzas", {
        headers: { "Authorization": "Bearer " + localStorage.getItem("token") }
    })
    .then(res => {
        // Verifica si la respuesta es exitosa
        if (!res.ok) {
            throw new Error("Error al obtener las pizzas.");
        }
        return res.json(); // Parsea la respuesta como JSON
    })
    .then(data => {
        // Verifica que la respuesta sea un array
        if (!Array.isArray(data)) {
            throw new Error("La respuesta no es una lista.");
        }

        let contenido = "<h2>Gestión de Pizzas</h2><ul>";

        // Recorre cada pizza y genera el HTML correspondiente
        data.forEach(pizza => {
            const ingredientes = pizza.ingredientes ? pizza.ingredientes.join(", ") : "Sin ingredientes"; // Asegura que los ingredientes no sean null

            contenido += `
                <li id="pizza-${pizza.id}">
                    <img src="${pizza.imagenUrl || "default.jpg"}" alt="${pizza.nombre}" width="100">
                    <strong>${pizza.nombre || "Sin nombre"}</strong> - ${pizza.precio ?? "0.00"}€
                    <p>${pizza.descripcion || "Sin descripción"}</p>
                    <p>Ingredientes: ${ingredientes}</p>
                    <p>Disponible: ${pizza.disponible ? "Sí" : "No"}</p>
                    <button class="btn secondary" onclick="habilitarEdicion('${pizza.id}', '${pizza.nombre || ""}', '${pizza.descripcion || ""}', '${ingredientes}', '${pizza.precio ?? "0.00"}', '${pizza.imagenUrl || ""}', ${pizza.disponible})">Editar</button>
                    <button class="btn secondary" onclick="eliminarPizza('${pizza.id}')">Eliminar</button>
                </li>`;
        });

        contenido += "</ul>";
        contenido += `<div class="btn-container"><button class="btn" onclick="window.location.href='/auth/agregar_pizza'">Agregar Pizza</button></div>`;
        document.getElementById("contenido").innerHTML = contenido; // Inserta el contenido en el contenedor
    })
    .catch(error => {
        // Maneja errores de conexión o del servidor
        console.error("Error:", error);
        document.getElementById("contenido").innerHTML = "<p>Error al cargar las pizzas.</p>";
    });
}

/**
 * Habilita la edición de una pizza en la interfaz.
 * - Reemplaza la información de la pizza con campos de entrada para editar.
 * @param {string} id - El ID de la pizza.
 * @param {string} nombre - El nombre de la pizza.
 * @param {string} descripcion - La descripción de la pizza.
 * @param {string} ingredientes - Los ingredientes de la pizza.
 * @param {number} precio - El precio de la pizza.
 * @param {string} imagenUrl - La URL de la imagen de la pizza.
 * @param {boolean} disponible - Indica si la pizza está disponible.
 */
function habilitarEdicion(id, nombre, descripcion, ingredientes, precio, imagenUrl, disponible) {
    document.getElementById(`pizza-${id}`).innerHTML = `
        <img src="${imagenUrl}" alt="${nombre}" width="100">
        <input type="text" id="input-nombre-${id}" value="${nombre}">
        <textarea id="input-descripcion-${id}">${descripcion}</textarea>
        <input type="text" id="input-ingredientes-${id}" value="${ingredientes}">
        <input type="number" id="input-precio-${id}" value="${precio}" step="0.01">
        <input type="text" id="input-imagen-${id}" value="${imagenUrl}">
        <label>
            <input type="checkbox" id="input-disponible-${id}" ${disponible ? "checked" : ""}> Disponible
        </label>
        <button class="btn secondary" onclick="guardarEdicion('${id}')">Guardar</button>
        <button class="btn secondary" onclick="mostrarPizzas()">Cancelar</button>
    `;
}

/**
 * Guarda los cambios realizados en una pizza.
 * - Realiza una petición PUT al servidor para actualizar la pizza.
 * @param {string} id - El ID de la pizza.
 */
function guardarEdicion(id) {
    const nombre = document.getElementById(`input-nombre-${id}`).value;
    const descripcion = document.getElementById(`input-descripcion-${id}`).value;
    const ingredientes = document.getElementById(`input-ingredientes-${id}`).value.split(",");
    const precio = document.getElementById(`input-precio-${id}`).value;
    const imagenUrl = document.getElementById(`input-imagen-${id}`).value;
    const disponible = document.getElementById(`input-disponible-${id}`).checked;

    fetch(`/api/pizzas/${id}`, {
        method: "PUT",
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token"),
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ nombre, descripcion, ingredientes, precio, imagenUrl, disponible })
    })
    .then(response => {
        if (response.ok) {
            alert("Pizza actualizada correctamente.");
            mostrarPizzas(); // Recarga la lista de pizzas
        } else {
            alert("Error al actualizar la pizza.");
        }
    })
    .catch(error => console.error("Error al actualizar la pizza:", error));
}

/**
 * Elimina una pizza.
 * - Realiza una petición DELETE al servidor para eliminar la pizza.
 * @param {string} id - El ID de la pizza.
 */
function eliminarPizza(id) {
    if (confirm("¿Estás seguro de que quieres eliminar esta pizza?")) {
        fetch(`/api/pizzas/${id}`, {
            method: "DELETE",
            headers: { "Authorization": "Bearer " + localStorage.getItem("token") }
        })
        .then(response => {
            if (response.ok) {
                alert("Pizza eliminada correctamente.");
                mostrarPizzas(); // Recarga la lista de pizzas
            } else {
                alert("Error al eliminar la pizza.");
            }
        })
        .catch(error => console.error("Error al eliminar la pizza:", error));
    }
}

/**
 * Obtiene y muestra los pedidos en la página.
 * - Realiza una petición al servidor para obtener los pedidos.
 * - Muestra los pedidos con opciones para editar su estado.
 * @async
 */
function mostrarPedidos() {
    fetch("/api/pedidos", {
        headers: { "Authorization": "Bearer " + localStorage.getItem("token") }
    })
    .then(res => {
        if (!res.ok) {
            throw new Error("Error al obtener los pedidos.");
        }
        return res.json(); // Parsea la respuesta como JSON
    })
    .then(data => {
        let contenido = "<h2>Gestión de Pedidos</h2><ul>";

        // Recorre cada pedido y genera el HTML correspondiente
        data.forEach(pedido => {
            contenido += `
                <li>
                    <strong>Número de pedido:</strong> ${pedido.id} <br>
                    <strong>Cliente:</strong> ${pedido.clienteUsername} <br>
                    <strong>Total:</strong> $${pedido.total} <br>
                    <strong>Fecha:</strong> ${new Date(pedido.fecha).toLocaleString()} <br>
                    <strong>Estado:</strong>
                    <span id="estado-texto-${pedido.id}">${pedido.estado}</span>
                    <select id="estado-select-${pedido.id}" style="display: none;">
                        <option value="PENDIENTE">Pendiente</option>
                        <option value="EN_PREPARACION">En preparación</option>
                        <option value="LISTO">Listo</option>
                        <option value="ENTREGADO">Entregado</option>
                    </select>
                    <button class="btn secondary" id="editar-${pedido.id}" onclick="editarEstado('${pedido.id}')">Editar Estado</button>
                    <button class="btn secondary" id="guardar-${pedido.id}" style="display: none;" onclick="actualizarEstadoPedido('${pedido.id}')">Guardar</button>
                </li><br>`;
        });

        contenido += "</ul>";
        document.getElementById("contenido").innerHTML = contenido; // Inserta el contenido en el contenedor
    })
    .catch(error => {
        console.error("Error:", error);
        document.getElementById("contenido").innerHTML = "<p>Error al cargar los pedidos.</p>";
    });
}

/**
 * Habilita la edición del estado de un pedido.
 * - Muestra un selector de estado y oculta el texto del estado actual.
 * @param {string} pedidoId - El ID del pedido.
 */
function editarEstado(pedidoId) {
    document.getElementById(`estado-texto-${pedidoId}`).style.display = "none";
    document.getElementById(`estado-select-${pedidoId}`).style.display = "inline";
    document.getElementById(`editar-${pedidoId}`).style.display = "none";
    document.getElementById(`guardar-${pedidoId}`).style.display = "inline";
}

/**
 * Actualiza el estado de un pedido en el servidor.
 * - Realiza una petición PUT al servidor para actualizar el estado del pedido.
 * @param {string} pedidoId - El ID del pedido.
 */
function actualizarEstadoPedido(pedidoId) {
    const nuevoEstado = document.getElementById(`estado-select-${pedidoId}`).value;

    fetch(`/api/pedidos/${pedidoId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + localStorage.getItem("token")
        },
        body: JSON.stringify({ estado: nuevoEstado })
    })
    .then(res => {
        if (!res.ok) {
            throw new Error("Error al actualizar el estado.");
        }
        return res.json(); // Parsea la respuesta como JSON
    })
    .then(pedidoActualizado => {
        alert("Estado actualizado con éxito.");

        // Actualiza la interfaz con el nuevo estado
        document.getElementById(`estado-texto-${pedidoId}`).innerText = pedidoActualizado.estado;
        document.getElementById(`estado-texto-${pedidoId}`).style.display = "inline";
        document.getElementById(`estado-select-${pedidoId}`).style.display = "none";
        document.getElementById(`editar-${pedidoId}`).style.display = "inline";
        document.getElementById(`guardar-${pedidoId}`).style.display = "none";
    })
    .catch(error => console.error("Error al actualizar el estado:", error));
}

/**
 * Cierra la sesión del usuario.
 * - Elimina el token JWT del localStorage y las cookies.
 * - Redirige al usuario a la página de inicio de sesión.
 */
function cerrarSesion() {
    localStorage.removeItem("token"); // Elimina el token almacenado
    document.cookie = "token=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;"; // Borra la cookie
    window.location.href = "/auth/login"; // Redirige al login
}