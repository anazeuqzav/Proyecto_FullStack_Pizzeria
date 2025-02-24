document.addEventListener("DOMContentLoaded", () => {
    // Obtiene el token de autenticación almacenado en localStorage
    const token = localStorage.getItem("token");

    // Si no hay token, el usuario no está autenticado, por lo que se le bloquea el acceso.
    if (!token) {
        alert("Acceso denegado. Inicia sesión.");
        window.location.href = "login.html"; // Redirección a la página de inicio de sesión
        return;
    }

    // Si el usuario está autenticado, se carga la lista de pedidos
    mostrarMisPedidos();
});

/**
 * Función que obtiene los pedidos del usuario autenticado y los muestra en la página.
 */
function mostrarMisPedidos() {
    const tokenData = getDecodedToken(); // Obtener los datos del usuario desde el token
    const username = tokenData ? tokenData.sub : "Usuario"; // Extraer el nombre de usuario

    fetch("/api/pedidos/misPedidos", {
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token") // Se envía el token en la cabecera
        }
    })
    .then(res => {
        // Verifica si la respuesta de la API es válida
        if (!res.ok) {
            throw new Error("Error al obtener los pedidos.");
        }
        return res.json(); // Convierte la respuesta en JSON
    })
    .then(data => {
        let contenido = `<h2>Pedidos de: ${username}</h2><ul>`; // Mostrar el nombre del usuario

        // Si el usuario no tiene pedidos registrados
        if (data.length === 0) {
            contenido += "<p>No tienes pedidos registrados.</p>";
        } else {
            // Recorre la lista de pedidos y genera el HTML correspondiente
            data.forEach(pedido => {
                contenido += `
                    <li class="pedido">
                        <strong>Número de pedido:</strong> ${pedido.id} <br>
                        <strong>Total:</strong> ${pedido.total} € <br>
                        <strong>Fecha:</strong> ${new Date(pedido.fecha).toLocaleString()} <br>
                        <strong>Estado:</strong> ${pedido.estado} <br>
                    </li><br>
                `;
            });
        }

        contenido += "</ul>";
        document.getElementById("contenido").innerHTML = contenido; // Inserta el contenido en el div con id "contenido"
    })
    .catch(error => {
        // Captura y muestra errores en la consola y en la interfaz de usuario
        console.error("Error:", error);
        document.getElementById("contenido").innerHTML = "<p>Error al cargar los pedidos.</p>";
    });
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
