/**
 * Maneja el evento de envío del formulario para agregar una nueva pizza.
 * Captura los datos del formulario, los convierte en un objeto y los envía a la API.
 */
document.getElementById("formAgregarPizza").addEventListener("submit", async (event) => {
    event.preventDefault(); // Evita el comportamiento por defecto del formulario (recargar la página).

    // Capturar valores del formulario
    const nombre = document.getElementById("nombre").value; // Nombre de la pizza
    const descripcion = document.getElementById("descripcion").value; // Descripción de la pizza
    const ingredientes = document.getElementById("ingredientes").value
        .split(",") // Divide la cadena ingresada en una lista de ingredientes separados por comas
        .map(i => i.trim()); // Elimina espacios en blanco alrededor de cada ingrediente
    const precio = parseFloat(document.getElementById("precio").value); // Convierte el precio a un número decimal
    const imagenUrl = document.getElementById("imagenUrl").value; // URL de la imagen de la pizza
    const disponible = document.getElementById("disponible").value === "true"; // Convierte la disponibilidad a booleano

    // Crear objeto con los datos de la nueva pizza
    const nuevaPizza = {
        nombre,
        descripcion,
        ingredientes,
        precio,
        imagenUrl,
        disponible
    };

    try {
        // Realiza una petición HTTP POST para agregar la pizza a la API
        const response = await fetch("http://localhost:8080/api/pizzas", {
            method: "POST", // Método HTTP para enviar datos al servidor
            headers: {
                "Content-Type": "application/json", // Especifica que los datos se envían en formato JSON
                "Authorization": "Bearer " + localStorage.getItem("token") // Incluye el token de autenticación
            },
            body: JSON.stringify(nuevaPizza) // Convierte el objeto en una cadena JSON antes de enviarlo
        });

        if (response.ok) {
            alert("Pizza agregada con éxito"); // Notifica al usuario que la operación fue exitosa
            window.location.href = "/auth/panel_admin"; // Redirige al panel de administración
        } else {
            alert("Error al agregar la pizza"); // Muestra una alerta si la respuesta no es exitosa
        }
    } catch (error) {
        console.error("Error:", error); // Muestra el error en la consola en caso de fallo
        alert("Error en la solicitud"); // Notifica al usuario sobre un error en la solicitud
    }
});
