<script lang="ts">
    import { onMount } from "svelte";
import { getUsername } from "../../api/auth";
    import { Blog, getBlog } from "../../api/blog";
    import { getUser, User } from "../../api/user";

    export let params: { author: string; blog: string };

    let blog: Blog;
    let author: User;
    let found = false;

    let isAuthor = false;

    onMount(async () => {
        blog = await getBlog(params.author, params.blog);
        author = await getUser(params.author);

        if (blog) found = true;

        isAuthor = (await getUsername()) === author.username;
    });
</script>

{#if found}
    <div class="title">
        <h2>{blog.name}</h2>
        <p>Author: <a href="/~{author.username}">{author.username}</a></p>
    </div>

    {#if isAuthor}
        <p class="empty">There's no posts here. You can create a new post <a href="/new/post">here</a>.</p>
    {:else}
        <p class="empty">There's no posts here.</p>
    {/if}

{:else}
    <h2>Blog not found</h2>
{/if}

<style>
    .title {
        border-bottom: 3px solid var(--nc-bg-2);
        margin-bottom: 1rem;
        display: flex;
        flex-direction: row;
        justify-content: space-between;
        align-items: center;
    }

    .title h2 {
        border-bottom: none;
    }
</style>
