<script lang="ts">
    import { AuthResult, login } from "../api/auth";

    let username: string;
    let password: string;

    let authRes: AuthResult | null = null;

    const onSubmit = async () => {
        authRes = await login(username, password);

        if (authRes.success) window.location.href = "/";
    };
</script>

<h2>Login</h2>

{#if authRes && !authRes.success}
    <div class="status-message error">{authRes.message}</div>
{/if}

<form on:submit|preventDefault={onSubmit}>
    <label for="username">Username:</label>
    <input type="text" id="username" name="username" placeholder="username" required bind:value={username} />

    <label for="password">Password:</label>
    <input type="password" id="password" name="password" placeholder="************" required bind:value={password} />

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
