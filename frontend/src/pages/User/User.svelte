<script lang="ts">
    import { onMount } from "svelte";
    import { getUsername, isLoggedIn } from "../../api/auth";
    import { Blog, getBlogs } from "../../api/blog";
    import { getUser, User } from "../../api/user";

    export let params: { user: string; };

    let user: User = null;
    let blogs: Blog[] = [];

    let currentUser: string;

    onMount(async () => {
        user = await getUser(params.user);

        if (await isLoggedIn()) {
            currentUser = await getUsername();
        }

        if (user === null) return;

        blogs = await getBlogs(params.user);
    });
</script>

{#if user === null}
    <h2>User not found</h2>
{:else}
    <h2>{user.username}</h2>

    {#if blogs.length === 0}
        {#if currentUser === user.username}
            <p>You don't have any blogs. Create one <a href="/new/blog">here</a>.</p>
        {:else}
            <p>This user has no blogs.</p>
        {/if}
    {:else}
        {#each blogs as blog}
            <div class="blog">
                <a href="/~{user.username}/{blog.url}">{blog.name}</a>
                <p>{blog.description}</p>
            </div>
        {/each}
    {/if}
{/if}

<style>
    .blog {
        border: 3px dashed var(--nc-bg-2);
        padding: 1rem;
        margin-bottom: 1rem;
    }

    .blog a {
        padding-bottom: 0.5rem;
        display: inline-block;
        font-size: 1.25rem;
    }

    .blog p {
        margin-bottom: 0;
    }
</style>
