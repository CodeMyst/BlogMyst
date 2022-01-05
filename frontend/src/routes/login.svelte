<script lang="ts">
    import { goto } from "$app/navigation";

    let username: string;
    let password: string;

    let error: boolean = false;

    const onSubmit = async () => {
        const data = {
            username: username,
            password: password
        };

        const res = await fetch("http://localhost:8080/login", {
            method: "POST",
            mode: "cors",
            headers: {
                "Content-Type": "application/json"
            },
            credentials: "include",
            body: JSON.stringify(data)
        });

        error = !res.ok;

        if (res.ok) {
            await goto("/");
        }
    };
</script>

<h2>Login</h2>

{#if error}
    <div class="error">
        Wrong username and/or password.
    </div>
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

    .error {
        padding-left: 1rem;
        margin-top: 1rem;
        margin-bottom: 1rem;
        border-left: 5px solid var(--nc-red);
    }
</style>
