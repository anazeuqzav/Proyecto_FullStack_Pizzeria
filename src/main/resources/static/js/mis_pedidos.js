/**
 * Escucha el evento de carga del DOM para verificar si el usuario está autenticado.
 * - Si no hay un token JWT en el localStorage, redirige al usuario a la página de inicio de sesión.
 * - Si el usuario está autenticado, carga y muestra sus pedidos.
 * @listens DOMContentLoaded
 */
document.addEventListener("DOMContentLoaded", () => {
  const token = localStorage.getItem("token");

  // Verifica si el usuario está autenticado
  if (!token) {
    alert("Acceso denegado. Inicia sesión.");
    window.location.href = "login.html";
    return;
  }

  // Si el usuario está autenticado, muestra sus pedidos
  mostrarMisPedidos();
});

/**
 * Obtiene y muestra los pedidos del usuario autenticado.
 * - Realiza una petición al servidor para obtener los pedidos.
 * - Muestra los pedidos en la página, incluyendo una barra de progreso para el estado del pedido.
 * - Si no hay pedidos, muestra un mensaje indicándolo.
 * @async
 */
function mostrarMisPedidos() {
  const tokenData = getDecodedToken();
  const username = tokenData ? tokenData.sub : "Usuario"; // Obtiene el nombre de usuario desde el token

  // Realiza una petición al servidor para obtener los pedidos del usuario
  fetch("/api/pedidos/misPedidos", {
    headers: {
      Authorization: "Bearer " + localStorage.getItem("token"), // Incluye el token en la cabecera
    },
  })
    .then((res) => {
      // Verifica si la respuesta es exitosa
      if (!res.ok) {
        throw new Error("Error al obtener los pedidos.");
      }
      return res.json(); // Parsea la respuesta como JSON
    })
    .then((data) => {
      let contenido = `<h2>Pedidos de: ${username}</h2><ul>`; // Encabezado con el nombre del usuario
      const estados = ["PENDIENTE", "EN_PREPARACION", "LISTO", "ENTREGADO"]; // Estados posibles de un pedido
      const colores = {
        PENDIENTE: "#ff0000", // Rojo
        EN_PREPARACION: "#ffa500", // Naranja
        LISTO: "#ffff00", // Amarillo
        ENTREGADO: "#00ff00", // Verde
      };

      // Si no hay pedidos, muestra un mensaje
      if (data.length === 0) {
        contenido += "<p>No tienes pedidos registrados.</p>";
      } else {
        // Recorre cada pedido y genera el HTML correspondiente
        data.forEach((pedido) => {
          let index = estados.indexOf(pedido.estado); // Índice del estado actual del pedido
          let progressBar = "<div class='progress-bar'>"; // Inicia la barra de progreso

          // Genera los segmentos de la barra de progreso
          for (let i = 0; i < estados.length; i++) {
            const estadoActual = estados[i];
            const color = colores[estadoActual];
            progressBar += `<div class='progress-step ${
              i <= index ? "active" : ""
            }' style="background-color: ${i <= index ? color : "#ccc"};"></div>`;
            if (i < estados.length - 1) {
              progressBar += `<div class='progress-line ${
                i < index ? "active" : ""
              }' style="background-color: ${i < index ? color : "#ccc"};"></div>`;
            }
          }
          progressBar += "</div>"; // Cierra la barra de progreso

          // Añade el pedido al contenido HTML
          contenido += `
                    <li class="order">
                        <strong>Número de pedido:</strong> ${pedido.id} <br>
                        <strong>Total:</strong> ${pedido.total} € <br>
                        <strong>Fecha:</strong> ${new Date(
                          pedido.fecha
                        ).toLocaleString()} <br>
                        <strong>Estado:</strong> ${pedido.estado} <br>
                        ${progressBar}
                    </li><br>
                `;
        });
      }

      contenido += "</ul>"; // Cierra la lista de pedidos
      document.getElementById("order-container").innerHTML = contenido; // Inserta el contenido en el contenedor
    })
    .catch((error) => {
      // Maneja errores de conexión o del servidor
      console.error("Error:", error);
      document.getElementById("order-container").innerHTML =
        "<p>Error al cargar los pedidos.</p>";
    });
}

/**
 * Decodifica el token JWT almacenado para obtener la información del usuario.
 * - Extrae el payload del token y lo decodifica desde Base64.
 * - Devuelve los datos decodificados.
 * @returns {Object} Datos decodificados del token (payload).
 */
function getDecodedToken() {
  const token = localStorage.getItem("token");
  if (!token) {
    console.log("No hay token almacenado.");
    return;
  }

  // Decodifica el payload del token
  const payload = token.split(".")[1];
  const decodedPayload = atob(payload);
  return JSON.parse(decodedPayload);
}