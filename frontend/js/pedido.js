// Carga el pedido del localStorage
document.addEventListener("DOMContentLoaded", function () {
    const pedido = JSON.parse(localStorage.getItem("pedido")) || [];

    const container = document.getElementById("pedidoContainer");

    // si el pedido está vacío
    if (pedido.length === 0) {
        container.innerHTML = "<p>No hay pizzas en el pedido.</p>";
        return;
    }

    // Objeto vacío para agrupar las pizzas que son iguales
    const pedidoAgrupado = {};

    pedido.forEach(item => {
        if (!pedidoAgrupado[item.pizzaNombre]) {
            pedidoAgrupado[item.pizzaNombre] = { cantidad: 1, precio: item.precio };
        } else {
            // si la pizza tiene el mismo nombre se suma la cantidad
            pedidoAgrupado[item.pizzaNombre].cantidad++;
        }
    });

    // recorre el objeto pedido agrupado y crea un div por cada pizza
    for (let pizzaNombre in pedidoAgrupado) {
        const div = document.createElement("div");
        // coloca los datos de la pizza en el html
        div.innerHTML = `
            <h3>${pizzaNombre}</h3>
            <p>Cantidad: ${pedidoAgrupado[pizzaNombre].cantidad}</p>
            <p>Precio unitario: ${pedidoAgrupado[pizzaNombre].precio.toFixed(2)}€ </p>
            <p>Subtotal: $${(pedidoAgrupado[pizzaNombre].precio * pedidoAgrupado[pizzaNombre].cantidad).toFixed(2)}</p>
        `;
        container.appendChild(div);
    }
});

/**
 * Función que envía el pedido a la API
 * @returns 
 */
async function enviarPedido() {
    // Obtiene el token del usuario
    const token = localStorage.getItem("token");
    if (!token) {
        alert("Debes iniciar sesión para hacer un pedido.");
        return;
    }

    // decodifica el token para obtener el username
    const tokenData = getDecodedToken();
    
    // obtiene el pedido del localstorage
    const pedido = JSON.parse(localStorage.getItem("pedido")) || [];
    if (pedido.length === 0) {
        alert("No hay pizzas en el pedido.");
        return;
    }

    // Crea un array con los nombres y los precios de cada pizza
    const pizzas = pedido.map(item => ({
        nombre: item.pizzaNombre,
        precio: item.precio
    }));

    // calcula el total del pedido
    const total = pizzas.reduce((sum, pizza) => sum + pizza.precio, 0).toFixed(2);

    // Objeto con los datos finales del pedido
    const pedidoData = {
        clienteId: tokenData.id,
        pizzas: pizzas,
        total: total,
        fecha: new Date().toISOString(),
        estado: "PENDIENTE"
    };

    // Petición fetch para enviar el pedido a la pai
    const response = await fetch("http://localhost:8080/api/pedidos", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(pedidoData) // envia los datos del pedido en JSON
    });

    if (response.ok) {
        alert("Pedido realizado con éxito!");
        localStorage.removeItem("pedido"); // elimina el pedido
        window.location.href = "misPedidos.html"; // redirige a los pedidos del usuario
    } else {
        alert("Error al realizar el pedido.");
    }
}

/**
 * Función que decodifica el token, se utiliza para conseguir el id del usuario y enviarlo en el pedido
 * @returns un objeto JSON con los datos del token
 */
function getDecodedToken() {
    const token = localStorage.getItem("token");
    if (!token) {
        console.log("No hay token almacenado.");
        return;
    }

    const payload = token.split(".")[1]; // Extraer la parte del payload (sin cabecera ni firma)
    const decodedPayload = atob(payload); // Decodificar Base64
    const tokenData = JSON.parse(decodedPayload);

    return tokenData;
}

// Llamar a la función
console.log(getDecodedToken());
