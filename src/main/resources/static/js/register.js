document.addEventListener("DOMContentLoaded", function () {
    document.getElementById("registerForm").addEventListener("submit", async function (event) {
        event.preventDefault(); // Evita que el formulario se envíe de forma convencional

        // Captura los valores del formulario
        const username = document.getElementById("username").value.trim();
        const email = document.getElementById("email").value.trim();
        const password = document.getElementById("password").value.trim();
        const role = document.getElementById("role").value;
        const errorMsg = document.getElementById("errorMsg");
        const loadingSpinner = document.getElementById("loadingSpinner");

        // Ocultar mensaje de error antes de validar
        errorMsg.classList.add("d-none");

        // Validación básica
        if (!username || !email || !password) {
            errorMsg.textContent = "Todos los campos son obligatorios.";
            errorMsg.classList.remove("d-none");
            return;
        }

        if (password.length < 6) {
            errorMsg.textContent = "La contraseña debe tener al menos 6 caracteres.";
            errorMsg.classList.remove("d-none");
            return;
        }

        // Datos a enviar
        const userData = { username, email, password, roles: role };

        // Mostrar spinner de carga
        loadingSpinner.style.display = "block";

        try {
            const response = await fetch("http://localhost:8080/auth/addNewUser", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(userData),
            });

            // Ocultar spinner de carga
            loadingSpinner.style.display = "none";

            if (response.ok) {
                alert("Registro exitoso. Ahora inicia sesión.");
                window.location.href = "/auth/login";
            } else {
                const errorText = await response.text();
                errorMsg.textContent = errorText || "Error al registrar el usuario.";
                errorMsg.classList.remove("d-none");
            }
        } catch (error) {
            console.error("Error en el registro:", error);
            errorMsg.textContent = "Hubo un problema con la conexión. Inténtalo más tarde.";
            errorMsg.classList.remove("d-none");
            loadingSpinner.style.display = "none"; // Asegurar que el spinner desaparezca en caso de error
        }
    });
});
