document.addEventListener("DOMContentLoaded", () => {
  const token = localStorage.getItem("token");

  if (!token) {
    alert("Acceso denegado. Inicia sesión.");
    window.location.href = "login.html";
    return;
  }

  mostrarMisPedidos();
});

function mostrarMisPedidos() {
  const tokenData = getDecodedToken();
  const username = tokenData ? tokenData.sub : "Usuario";

  fetch("/api/pedidos/misPedidos", {
    headers: {
      Authorization: "Bearer " + localStorage.getItem("token"),
    },
  })
    .then((res) => {
      if (!res.ok) {
        throw new Error("Error al obtener los pedidos.");
      }
      return res.json();
    })
    .then((data) => {
      let contenido = `<h2>Pedidos de: ${username}</h2><ul>`;
      const estados = ["PENDIENTE", "EN_PREPARACION", "LISTO", "ENTREGADO"];
      const colores = {
        PENDIENTE: "#ff0000", // Rojo
        EN_PREPARACION: "#ffa500", // Naranja
        LISTO: "#ffff00", // Amarillo
        ENTREGADO: "#00ff00", // Verde
      };

      if (data.length === 0) {
        contenido += "<p>No tienes pedidos registrados.</p>";
      } else {
        data.forEach((pedido) => {
          let index = estados.indexOf(pedido.estado);
          let progressBar = "<div class='progress-bar'>";

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
          progressBar += "</div>";
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

      contenido += "</ul>";
      document.getElementById("order-container").innerHTML = contenido;
    })
    .catch((error) => {
      console.error("Error:", error);
      document.getElementById("order-container").innerHTML =
        "<p>Error al cargar los pedidos.</p>";
    });
}

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