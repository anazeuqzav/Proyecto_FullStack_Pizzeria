document.getElementById("loginForm").addEventListener("submit", async function (event) {
    event.preventDefault();

    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    const loginData = { username, password };

    try {
        const response = await fetch("http://localhost:8080/auth/generateToken", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(loginData),
        });

        const data = await response.json();

        if (response.ok) {
            localStorage.setItem("token", data.token);
            alert("Inicio de sesión exitoso");
            window.location.href = "pizzas.html";  // esto deberia ser una accion ? 
        } else {
            alert("Error en el login: " + data.message);
        }
    } catch (error) {
        console.error("Error en el login:", error);
    }
});