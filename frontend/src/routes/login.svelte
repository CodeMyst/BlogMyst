<h2>Login</h2>

<form>
    <label for="username">Username:</label>
    <input type="text" id="username" name="username" placeholder="username" required />

    <label for="password">Password:</label>
    <input type="password" id="password" name="password" placeholder="************" required />

    <button type="submit">Login</button>
</form>

<style>
    form {
        display: grid;
        grid-template-columns: 100px 1fr;
    }

    form label {
        grid-column: 1/ 2;
    }

    form input,
    form button {
        grid-column: 2 / 3;
    }
</style>
