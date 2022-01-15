<script lang="ts">
    import { AuthResult, register } from "../api/auth";

    let form: HTMLFormElement;

    let username: string = "";
    let password: string = "";
    let passwordConfirm: string = "";

    let authRes: AuthResult | null = null;

    const onSubmit = async () => {
        authRes = await register(username, password);

        if (authRes.success) form.reset();
    };
</script>

<!-- TODO: Form validation (on the backend as well) -->

<svelte:head>
    <title>BlogMyst | Register</title>
</svelte:head>

<h2>Create a new account</h2>

{#if authRes}
    {#if authRes.success}
        <div class="status-message success">
            Successfully created the account. You can now <a href="/login">login</a>.
        </div>
    {:else}
        <div class="status-message error">
            {authRes.message}
        </div>
    {/if}
{/if}

<form on:submit|preventDefault={onSubmit} bind:this={form}>
    <label for="username">Username:</label>
    <input
        type="text"
        id="username"
        name="username"
        placeholder="username"
        required
        bind:value={username}
    />

    <label for="password">Password:</label>
    <input
        type="password"
        id="password"
        name="password"
        placeholder="************"
        required
        bind:value={password}
    />

    <label for="password-confirm">Confirm Password:</label>
    <input
        type="password"
        id="password-confirm"
        name="password-confirm"
        placeholder="************"
        required
        bind:value={passwordConfirm}
    />

    <button type="submit">Register</button>
</form>

{#if password !== passwordConfirm}
    <div class="error status-message">
        Passwords don't match.
    </div>
{/if}

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
