document.addEventListener("DOMContentLoaded", function () {
    document.getElementById("registerForm").addEventListener("submit", async function (event) {
        event.preventDefault(); // Evita el envío del formulario

        const username = document.getElementById("username").value.trim();
        const email = document.getElementById("email").value.trim();
        const password = document.getElementById("password").value.trim();
        const role = document.getElementById("role").value;
        const errorMsg = document.getElementById("errorMsg");

        if (!username || !email || !password) {
            errorMsg.textContent = "Todos los campos son obligatorios.";
            errorMsg.classList.remove("hidden");
            return;
        }

        if (password.length < 6) {
            errorMsg.textContent = "La contraseña debe tener al menos 6 caracteres.";
            errorMsg.classList.remove("hidden");
            return;
        }

        // Verificar si el nombre de usuario ya existe
        try {
            const checkResponse = await fetch(`http://localhost:8080/auth/checkUsername?username=${username}`);
            if (!checkResponse.ok) {
                const errorText = await checkResponse.text(); // Extraer el mensaje de error del cuerpo de la respuesta
                errorMsg.textContent = errorText || "El nombre de usuario ya está en uso.";
                errorMsg.classList.remove("hidden");
                return;
            }
        } catch (error) {
            console.error("Error al verificar usuario:", error);
            errorMsg.textContent = "Error al verificar disponibilidad del nombre de usuario.";
            errorMsg.classList.remove("hidden");
            return;
        }

        // Si el usuario no existe, proceder con el registro
        const userData = { username, email, password, roles: role };

        try {
            const response = await fetch("http://localhost:8080/auth/addNewUser", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(userData),
            });

            if (response.ok) {
                alert("Registro exitoso. Ahora inicia sesión.");
                window.location.href = "/auth/login";
            } else {
                const errorText = await response.text();
                errorMsg.textContent = errorText || "Error al registrar el usuario.";
                errorMsg.classList.remove("hidden");
            }
        } catch (error) {
            console.error("Error en el registro:", error);
            errorMsg.textContent = "Hubo un problema con la conexión. Inténtalo más tarde.";
            errorMsg.classList.remove("hidden");
        }
    });
});