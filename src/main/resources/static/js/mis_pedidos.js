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
    fetch("/api/pedidos/misPedidos", {
        headers: { "Authorization": "Bearer " + localStorage.getItem("token") }
    })
    .then(res => {
        if (!res.ok) {
            throw new Error("Error al obtener los pedidos.");
        }
        return res.json();
    })
    .then(data => {
        let contenido = "<h2>Mis Pedidos</h2><ul>";

        if (data.length === 0) {
            contenido += "<p>No tienes pedidos registrados.</p>";
        } else {
            data.forEach(pedido => {
                contenido += `
                    <li class="pedido">
                        <strong>ID:</strong> ${pedido.id} <br>
                        <strong>Total:</strong> ${pedido.total} € <br>
                        <strong>Fecha:</strong> ${new Date(pedido.fecha).toLocaleString()} <br>
                        <strong>Estado:</strong> ${pedido.estado} <br>
                    </li><br>
                `;
            });
        }

        contenido += "</ul>";
        document.getElementById("contenido").innerHTML = contenido;
    })
    .catch(error => {
        console.error("Error:", error);
        document.getElementById("contenido").innerHTML = "<p>Error al cargar los pedidos.</p>";
    });
}

function verDetallesPedido(pedidoId) {
    window.location.href = `detalle_pedido.html?id=${pedidoId}`;
}
