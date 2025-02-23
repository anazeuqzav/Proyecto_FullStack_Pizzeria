// Elimina cualquier token JWT previo almacenado en las cookies del navegador
document.cookie = "JWT-TOKEN=; Path=/; Expires=Thu, 01 Jan 1970 00:00:00 UTC;";

document.getElementById("loginForm").addEventListener("submit", async function (event) {
    event.preventDefault();

    const username = document.getElementById("username").value.trim(); // Nombre de usuario
    const password = document.getElementById("password").value.trim(); // Contraseña
    const errorMsg = document.getElementById("errorMsg");

    // Validación simple: verifica que ambos campos estén llenos
    if (!username || !password) {
        errorMsg.textContent = "Por favor, completa todos los campos.";
        errorMsg.classList.remove("d-none");
        return;
    }

    try {
        // Realiza la petición al backend para autenticar al usuario y obtener un token JWT
        const response = await fetch("http://localhost:8080/auth/generateToken", {
            method: "POST",
            headers: { "Content-Type": "application/json" }, // Especifica el tipo de contenido de la solicitud
            body: JSON.stringify({ username, password }), // Envía los datos de inicio de sesión en formato JSON
            credentials: "include" // Permite que el navegador envíe cookies en la petición
        });

        const text = await response.text(); // Obtiene la respuesta como texto

        try {
            const data = JSON.parse(text); // Intenta convertir la respuesta en un objeto JSON


            if (response.ok && data.token) {
                localStorage.setItem("token", data.token); // Guarda el token en el almacenamiento local
                document.cookie = `jwtToken=${data.token}; Path=/; SameSite=Strict`; // Almacena el token en una cookie

                window.location.href = data.redirectUrl || "/"; // Redirige al usuario a la página correspondiente
            } else {
                errorMsg.textContent = "Usuario o contraseña incorrectos.";
                errorMsg.classList.remove("d-none");
            }
        } catch (err) {
            console.error("Error parsing JSON:", err);
            errorMsg.textContent = "Usuario o contraseña incorrectos.";
            errorMsg.classList.remove("d-none");
        }
    } catch (error) {
        console.error("Error en el login:", error);
        errorMsg.textContent = "Error de conexión. Verifica tu red.";
        errorMsg.classList.remove("d-none");
    }
});
