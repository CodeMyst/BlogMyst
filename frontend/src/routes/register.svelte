<script lang="ts">
    let form: HTMLFormElement;

    let username: string;
    let password: string;

    let error: boolean = false;

    let success: boolean = false;

    const onSubmit = async () => {
        const data = {
            username: username,
            password: password
        };

        const res = await fetch("http://localhost:8080/users/register", {
            method: "POST",
            mode: "cors",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        });

        error = !res.ok;
        success = res.ok;

        if (success) {
            form.reset();
        }
    };
</script>

<h2>Create a new account</h2>

<!-- TODO: form validation (pass and confirm must match) -->

{#if success}
    <div class="success">
        Successfully created the account. You can now <a href="/login">login</a>.
    </div>
{/if}

{#if error}
    <div class="error">
        Something went wrong.
    </div>
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

    .success,
    .error {
        padding-left: 1rem;
        margin-top: 1rem;
        margin-bottom: 1rem;
        border-left: 5px solid var(--nc-green);
    }

    .error {
        border-color: var(--nc-red);
    }
</style>
