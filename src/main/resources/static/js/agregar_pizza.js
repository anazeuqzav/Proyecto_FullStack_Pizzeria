document.getElementById("formAgregarPizza").addEventListener("submit", async (event) => {
    event.preventDefault();

    // Capturar valores del formulario
    const nombre = document.getElementById("nombre").value;
    const descripcion = document.getElementById("descripcion").value;
    const ingredientes = document.getElementById("ingredientes").value.split(",").map(i => i.trim());
    const precio = parseFloat(document.getElementById("precio").value);
    const imagenUrl = document.getElementById("imagenUrl").value;
    const disponible = document.getElementById("disponible").value === "true";

    // Crear objeto pizza
    const nuevaPizza = { nombre, descripcion, ingredientes, precio, imagenUrl, disponible };

    try {
        const response = await fetch("http://localhost:8080/api/pizzas", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + localStorage.getItem("token") // Asegúrate de manejar la autenticación
            },
            body: JSON.stringify(nuevaPizza)
        });

        if (response.ok) {
            alert("Pizza agregada con éxito");
            window.location.href = "/auth/panel_admin"; // Volver a la página de administración
        } else {
            alert("Error al agregar la pizza");
        }
    } catch (error) {
        console.error("Error:", error);
        alert("Error en la solicitud");
    }
});