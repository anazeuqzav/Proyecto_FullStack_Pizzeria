document.addEventListener("DOMContentLoaded", function () {
    document.getElementById("registerForm").addEventListener("submit", async function (event) {
        event.preventDefault();

        const username = document.getElementById("username").value;
        const email = document.getElementById("email").value;
        const password = document.getElementById("password").value;
        const role = document.getElementById("role").value;  // Captura el rol seleccionado

        const userData = { username, email, password, roles: role };

        try {
            const response = await fetch("http://localhost:8080/auth/addNewUser", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(userData),
            });

    
            if (response.ok) {
                alert("Registro exitoso. Ahora inicia sesión.");
                window.location.href = "login.html";
            }
        } catch (error) {
            console.error("Error en el registro:", error);
        }
    });
});