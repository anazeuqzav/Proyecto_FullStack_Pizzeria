document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("token");

    // Si no hay token, redirige a la página de inicio de sesión
    if (!token) {
        alert("Debes iniciar sesión para ver el catálogo.");
        window.location.href = "login.html";
        return;
    }

    try {
        // Solicita las pizzas disponibles al backend con autenticación token
        const response = await fetch("http://localhost:8080/api/pizzas/disponibles", {
            headers: { "Authorization": `Bearer ${token}` }
        });

        // si la respuesta falla lanza un error
        if (!response.ok) throw new Error("Error al cargar el catálogo");

        // Convierte la respuesta a JSON y mostrar las pizzas
        const pizzas = await response.json();
        mostrarPizzas(pizzas);

    } catch (error) {
        console.error("Error:", error);
        document.getElementById("pizzaCatalog").innerHTML = "<p>Error al cargar las pizzas.</p>";
    }
});


/**
 * Función para mostrar las pizzas en el html
 * @param {*} pizzas Respuesta JSON con las pizzas de la base de datos
 */
function mostrarPizzas(pizzas) {
    // obtiene el contenedor donde se van a mostrar las pizzas
    const catalogo = document.getElementById("pizzaCatalog");
    catalogo.innerHTML = ""; // limpia 

    pizzas.forEach(pizza => {
        const pizzaCard = document.createElement("div"); // por cada pizza crea un div
        pizzaCard.classList.add("pizza-card"); // le añade la clase

        pizzaCard.innerHTML = `
            <img src="${pizza.imagenUrl}" alt="${pizza.nombre}">
            <h2>${pizza.nombre}</h2>
            <p>${pizza.descripcion}</p>
            <p><strong>Ingredientes:</strong> ${pizza.ingredientes.join(", ")}</p>
            <p><strong>Precio:</strong> ${pizza.precio.toFixed(2)}€</p>
            <label for="cantidad-${pizza.id}">Cantidad:</label>
            <input type="number" id="cantidad-${pizza.id}" value="1" min="1">
            <button class="addToCart" data-id="${pizza.id}" data-nombre="${pizza.nombre}">Añadir al pedido</button>
        `;

        catalogo.appendChild(pizzaCard); // añade cada pizza al catalogo
    });

    // Obtiene del DOM todos los botones para añadir al pedido de cada pizza
    document.querySelectorAll(".addToCart").forEach(button => {
        // agrega el evento al boton
        button.addEventListener("click", () => {
            // datos necesarios de cada pizza
            const pizzaId = button.dataset.id;
            const pizzaNombre = button.dataset.nombre;
            const cantidad = parseInt(document.getElementById(`cantidad-${pizzaId}`).value); // coge la cantidad que se quiere añadir
            const precio =  pizzas.find(p => p.id == pizzaId).precio; // busca la pizza con el id en el array para conseguir el precio
        
            agregarAlPedido(pizzaId, pizzaNombre, cantidad, precio);
        });
    });
}

/**
 * Función para añadir pizzas al pedido
 * @param {*} pizzaId identificador de la pizza
 * @param {*} pizzaNombre nombre de la pizza
 * @param {*} cantidad cantidad que se quiere pedir
 * @param {*} precio precio individual de cada pizza
 */
function agregarAlPedido(pizzaId, pizzaNombre, cantidad, precio) {
    // se obtiene o se crea un pedido en localStorage (un array vacio)
    let pedido = JSON.parse(localStorage.getItem("pedido")) || [];

    // Agregar cada pizza al pedido
    for (let i = 0; i < cantidad; i++) {
        pedido.push({ pizzaId, pizzaNombre, precio });
    }

    // guarda el pedido en localStorage convertido en una cadena JSON
    localStorage.setItem("pedido", JSON.stringify(pedido));
    
    alert(`${cantidad} ${pizzaNombre}(s) añadida(s) al pedido.`);
}

/**
 * Función para redirigir a la página del pedido (carrito)
 */
function irAHacerPedido() {
    window.location.href = "pedido.html";
}
