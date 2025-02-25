/**
 * Elimina cualquier token JWT previo almacenado en las cookies del navegador.
 */
document.cookie = "JWT-TOKEN=; Path=/; Expires=Thu, 01 Jan 1970 00:00:00 UTC;";


/**
 * Maneja el evento de envío del formulario de inicio de sesión.
 * - Valida los campos de entrada.
 * - Realiza la autenticación con el backend.
 * - Almacena el token JWT y redirige al usuario si la autenticación es exitosa.
 * - Muestra mensajes de error en caso de fallos.
 * @param {Event} event - El evento de envío del formulario.
 */
document.getElementById("loginForm").addEventListener("submit", async function (event) {
    event.preventDefault(); // Evita el envío tradicional del formulario

    // Obtiene los valores de los campos de entrada
    const username = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value.trim();
    const errorMsg = document.getElementById("errorMsg");

    // Valida que ambos campos estén llenos
    if (!username || !password) {
        errorMsg.textContent = "Por favor, completa todos los campos.";
        errorMsg.classList.remove("hidden");
        return;
    }

    try {
        // Intenta autenticar al usuario
        const data = await loginUser(username, password);

        // Si la autenticación es exitosa, almacena el token y redirige al usuario
        if (data.token) {
            localStorage.setItem("token", data.token); // Almacena el token en localStorage
            document.cookie = `jwtToken=${data.token}; Path=/; SameSite=Strict`; // Almacena el token en una cookie
            window.location.href = data.redirectUrl || "/"; // Redirige al usuario
        }
    } catch (error) {
        // Muestra un mensaje de error si la autenticación falla.
        errorMsg.textContent = "Usuario o contraseña incorrectos";
        errorMsg.classList.remove("hidden");
    }
});

/**
 * Realiza una petición al backend para autenticar al usuario y obtener un token JWT.
 * @param {string} username - El nombre de usuario proporcionado en el formulario.
 * @param {string} password - La contraseña proporcionada en el formulario.
 * @returns {Promise<Object>} - Una promesa que resuelve con los datos de la respuesta del servidor.
 * @throws {Error} - Lanza un error si la autenticación falla o si hay un problema de red.
 */
async function loginUser(username, password) {
    try {
        const response = await fetch("http://localhost:8080/auth/generateToken", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, password }),
            credentials: "include"
        });

        // Si la respuesta no es exitosa, lanza un error
        if (!response.ok) throw new Error("Error en la autenticación");

        // Parsea la respuesta como JSON y la devuelve
        const data = await response.json();
        return data;
    } catch (error) {
        console.error("Error en el login:", error);
        throw error;
    }
}
