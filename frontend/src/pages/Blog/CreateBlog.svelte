<script lang="ts">
    import { onMount } from "svelte";
    import { getUser } from "../../api/auth";
    import { BlogCreateResult, createBlog } from "../../api/blog";
    import type { User } from "../../api/user";

    let currentUser: User;

    let name: string;
    let description: string;

    let res: BlogCreateResult | null = null;

    onMount(async () => {
        currentUser= await getUser();
    });

    const onSubmit = async () => {
        res = await createBlog(name, description);
    };
</script>

<h2>Create a new Blog</h2>

{#if res && res.success}
    <div class="status-message success">
        Successfully created the blog. You can view it at: <a href="/~{currentUser.username}/{res.url}">/~{currentUser.username}/{res.url}</a>.
    </div>
{:else if res && !res.success}
    <div class="status-message error">
        {res.message}
    </div>
{/if}

<form on:submit|preventDefault={onSubmit}>
    <label for="name">Name:</label>
    <input type="text" name="name" id="name" placeholder="Blog Name" required bind:value={name} />

    <label for="description">Description:</label>
    <textarea
        name="description"
        id="description"
        cols="30"
        rows="10"
        placeholder="Blog Description"
        bind:value={description}
    />

    <button type="submit">Create</button>
</form>

<style>
    form {
        display: grid;
        grid-template-columns: 125px 1fr;
    }

    form label {
        grid-column: 1/ 2;
    }

    form input,
    form button {
        grid-column: 2 / 3;
    }

    textarea {
        resize: vertical;
    }
</style>
