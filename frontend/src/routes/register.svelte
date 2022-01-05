<h2>Create a new account</h2>

<!-- TODO: form validation -->

<form>
    <label for="username">Username:</label>
    <input
        type="text"
        id="username"
        name="username"
        placeholder="username"
        required
    />

    <label for="password">Password:</label>
    <input
        type="password"
        id="password"
        name="password"
        placeholder="************"
        required
    />

    <label for="password-confirm">Confirm Password:</label>
    <input
        type="password"
        id="password-confirm"
        name="password-confirm"
        placeholder="************"
        required
    />

    <button type="submit">Register</button>
</form>


<style>
    form {
        display: grid;
        grid-template-columns: 150px 1fr;
    }

    form label {
        grid-column: 1/ 2;
    }


    form input,
    form button {
        grid-column: 2 / 3;
    }
</style>