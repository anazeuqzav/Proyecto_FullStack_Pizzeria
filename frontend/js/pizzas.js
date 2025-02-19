document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("token");

    if (!token) {
        alert("Debes iniciar sesión para ver el catálogo.");
        window.location.href = "login.html";
        return;
    }

    try {
        const response = await fetch("http://localhost:8080/api/pizzas/disponibles", {
            headers: { "Authorization": `Bearer ${token}` }
        });

        if (!response.ok) throw new Error("Error al cargar el catálogo");

        const pizzas = await response.json();
        mostrarPizzas(pizzas);
    } catch (error) {
        console.error("Error:", error);
        document.getElementById("pizzaCatalog").innerHTML = "<p>Error al cargar las pizzas.</p>";
    }
});

// Función para mostrar las pizzas en la página
function mostrarPizzas(pizzas) {
    const catalog = document.getElementById("pizzaCatalog");
    catalog.innerHTML = "";

    pizzas.forEach(pizza => {
        const pizzaCard = document.createElement("div");
        pizzaCard.classList.add("pizza-card");

        pizzaCard.innerHTML = `
            <img src="${pizza.imagenUrl}" alt="${pizza.nombre}">
            <h2>${pizza.nombre}</h2>
            <p>${pizza.descripcion}</p>
            <p><strong>Ingredientes:</strong> ${pizza.ingredientes.join(", ")}</p>
            <p><strong>Precio:</strong> $${pizza.precio.toFixed(2)}</p>
            <label for="cantidad-${pizza.id}">Cantidad:</label>
            <input type="number" id="cantidad-${pizza.id}" value="1" min="1">
            <button class="addToCart" data-id="${pizza.id}" data-nombre="${pizza.nombre}">Añadir al pedido</button>
        `;

        catalog.appendChild(pizzaCard);
    });

    // Agregar eventos a los botones de añadir al pedido
    document.querySelectorAll(".addToCart").forEach(button => {
        button.addEventListener("click", () => {
            const pizzaId = button.dataset.id;
            const pizzaNombre = button.dataset.nombre;
            const cantidad = parseInt(document.getElementById(`cantidad-${pizzaId}`).value);

            agregarAlPedido(pizzaId, pizzaNombre, cantidad);
        });
    });
}

function agregarAlPedido(pizzaId, pizzaNombre, cantidad) {
    let pedido = JSON.parse(localStorage.getItem("pedido")) || [];

    const index = pedido.findIndex(item => item.pizzaId === pizzaId);

    if (index !== -1) {
        pedido[index].cantidad += cantidad;
    } else {
        pedido.push({ pizzaId, pizzaNombre, cantidad });
    }

    localStorage.setItem("pedido", JSON.stringify(pedido));
    alert(`${cantidad} ${pizzaNombre}(s) añadida(s) al pedido.`);
}

// Redirigir a la página de pedido
function irAHacerPedido() {
    window.location.href = "pedido.html";
}