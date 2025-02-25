document.addEventListener("DOMContentLoaded", async () => {
    // Recupera el token almacenado en localStorage
    const token = localStorage.getItem("token");

    // Si no hay token, redirige a la página de inicio de sesión
    if (!token) {
        alert("Debes iniciar sesión para ver el catálogo.");
        window.location.href = "/auth/login";
        return;
    }

    try {
        // Realiza una solicitud fetch al backend para obtener las pizzas disponibles
        const response = await fetch("http://localhost:8080/api/pizzas/disponibles", {
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            },
            credentials: "include"
        });

        // Si la respuesta no es correcta, lanza un error
        if (!response.ok) throw new Error("Error al cargar el catálogo");

        // Convierte la respuesta a JSON
        const pizzas = await response.json();
        // Llama a la función para mostrar las pizzas en la interfaz
        mostrarPizzas(pizzas);

    } catch (error) {
        console.error("Error:", error);
        document.getElementById("pizzaCatalog").innerHTML = "<p>Error al cargar las pizzas.</p>";
    }
});

/**
 * Función para mostrar las pizzas en el HTML utilizando tarjetas
 * @param {Array} pizzas - Array de objetos con los datos de las pizzas
 */
function mostrarPizzas(pizzas) {
    // Selecciona el contenedor del catálogo (fila de Bootstrap)
    const catalogo = document.getElementById("pizzaCatalog");
    catalogo.innerHTML = ""; // Limpia el contenido previo

    pizzas.forEach(pizza => {
        console.log(pizza)
        // Crea una columna para cada tarjeta (tarjetas responsivas en grid)
        const colDiv = document.createElement("div");
        colDiv.classList.add("col-md-4", "mb-4");

        // Crea la tarjeta con el estilo de Bootstrap
        colDiv.innerHTML = `
            <div class="card h-100">
                <img src="${pizza.imagenUrl}" class="card-img-top" alt="${pizza.nombre}">
                <div class="card-body">
                    <h5 class="card-title">${pizza.nombre}</h5>
                    <p class="card-text">${pizza.descripcion}</p>
                    <p class="card-text"><small class="text-muted">Ingredientes: ${pizza.ingredientes.join(", ")}</small></p>
                    <p class="card-text"><strong>Precio:</strong> ${pizza.precio.toFixed(2)}€</p>
                </div>
                <div class="card-footer">
                    <div class="form-group">
                        <label for="cantidad-${pizza.id}">Cantidad:</label>
                        <input type="number" id="cantidad-${pizza.id}" value="1" min="1" class="form-control">
                    </div>
                    <button class="btn btn-primary btn-block addToCart" data-id="${pizza.id}" data-nombre="${pizza.nombre}">Añadir al pedido</button>
                </div>
            </div>
        `;
        // Agrega la columna (con la tarjeta) al contenedor del catálogo
        catalogo.appendChild(colDiv);
    });

    // Asigna eventos a cada botón "Añadir al pedido" de las tarjetas
    document.querySelectorAll(".addToCart").forEach(button => {
        button.addEventListener("click", () => {
            const pizzaId = button.dataset.id;
            const pizzaNombre = button.dataset.nombre;
            const cantidad = parseInt(document.getElementById(`cantidad-${pizzaId}`).value);
            
            const foundPizza = pizzas.find(p => p.id == pizzaId);
            if (!foundPizza) {
                console.error("No se encontró la pizza con id:", pizzaId);
                return;
            }
            
            const precio = foundPizza.precio;

            agregarAlPedido(pizzaId, pizzaNombre, cantidad, precio);
        });
    });
}

/**
 * Función para añadir pizzas al pedido y guardarlo en localStorage
 * @param {number|string} pizzaId - Identificador de la pizza
 * @param {string} pizzaNombre - Nombre de la pizza
 * @param {number} cantidad - Cantidad que se quiere pedir
 * @param {number} precio - Precio individual de la pizza
 * @param {string} imagen - url de la imagen
 */
function agregarAlPedido(pizzaId, pizzaNombre, cantidad, precio) {
    let pedido = JSON.parse(localStorage.getItem("pedido")) || [];

    for (let i = 0; i < cantidad; i++) {
        pedido.push({ pizzaId, pizzaNombre, precio });
    }

    localStorage.setItem("pedido", JSON.stringify(pedido));
    alert(`${cantidad} ${pizzaNombre}(s) añadida(s) al pedido.`);
}

/**
 * Función para redirigir a la página del pedido (carrito)
 */
function irAHacerPedido() {
    window.location.href = "/auth/hacerPedido";
}

/**
 * Función para redirigir a la página de los pedidos del usuario
 */
function verMisPedidos() {
    window.location.href = "/auth/mis_pedidos";
}

/**
 * Función para cerrar sesión
 */
function cerrarSesion() {
    localStorage.removeItem("token"); // Elimina el token almacenado
    document.cookie = "token=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;"; // Borra la cookie
    window.location.href = "/auth/login"; // Redirige al login
}