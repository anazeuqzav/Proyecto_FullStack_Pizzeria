document.addEventListener("DOMContentLoaded", function () {
    const pedido = JSON.parse(localStorage.getItem("pedido")) || [];
    const container = document.getElementById("pedidoContainer");

    if (pedido.length === 0) {
        container.innerHTML = "<p>No hay pizzas en el pedido.</p>";
        return;
    }

    pedido.forEach(item => {
        const div = document.createElement("div");
        div.innerHTML = `
            <h3>${item.pizzaNombre}</h3>
            <p>Cantidad: ${item.cantidad}</p>
        `;
        container.appendChild(div);
    });
});

async function enviarPedido() {
    const token = localStorage.getItem("token");
    if (!token) {
        alert("Debes iniciar sesión para hacer un pedido.");
        return;
    }

    // Recuperar datos del token
    const tokenData = getDecodedToken();
    if (!tokenData || !tokenData.sub) {
        alert("No se pudo obtener el usuario del token.");
        return;
    }

    const pedido = JSON.parse(localStorage.getItem("pedido")) || [];
    if (pedido.length === 0) {
        alert("No hay pizzas en el pedido.");
        return;
    }

    // Transformar los datos para que coincidan con el backend
    const pizzas = pedido.map(item => ({
        nombre: item.pizzaNombre, // Cambia "pizzaNombre" a "nombre"
        precio: item.precio       // Asegura que haya un precio
    }));

    // Calcular el total del pedido
    const total = pizzas.reduce((sum, pizza) => sum + pizza.precio, 0);

    // Crear el objeto del pedido
    const pedidoData = {
        cliente: tokenData.sub,  // Nombre de usuario desde el token
        pizzas: pizzas,
        total: total,
        fecha: new Date().toISOString(),
        estado: "Pendiente"
    };

    // Enviar el pedido al backend
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
        localStorage.removeItem("pedido"); // Limpiar pedido después de enviarlo
        window.location.href = "misPedidos.html";
    } else {
        alert("Error al realizar el pedido.");
    }
}

function getDecodedToken() {
    const token = localStorage.getItem("token");
    if (!token) {
        console.log("No hay token almacenado.");
        return;
    }

    const payload = token.split(".")[1]; // Extraer la parte del payload (sin cabecera ni firma)
    const decodedPayload = atob(payload); // Decodificar Base64
    const tokenData = JSON.parse(decodedPayload);

    console.log("Datos del token:", tokenData); // Mostrar los datos en la consola
    return tokenData;
}

// Llamar a la función
getDecodedToken();
