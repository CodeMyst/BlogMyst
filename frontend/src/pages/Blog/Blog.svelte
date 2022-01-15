<script lang="ts">
    import { onMount } from "svelte";
    import { getUsername } from "../../api/auth";
    import { Blog, editBlog, getBlog, getBlogPosts, Post } from "../../api/blog";
    import { getUser, User } from "../../api/user";

    export let params: { author: string; blog: string };

    let blog: Blog;
    let author: User;
    let found = false;

    let isAuthor = false;

    let editing = false;

    let posts: Post[] = [];

    onMount(async () => {
        blog = await getBlog(params.author, params.blog);
        author = await getUser(params.author);

        if (blog) found = true;

        if (!found) return;

        isAuthor = (await getUsername()) === author.username;

        posts = await getBlogPosts(author.username, blog.url);
    });

    const saveBlogMeta = async () => {
        await editBlog(author.username, blog.url, blog.name, blog.description);
        editing = false;
    };
</script>

{#if found}
    <div class="title">
        {#if editing}
            <input class="edit-title" type="text" bind:value={blog.name}>
        {:else}
            <h2>{blog.name}</h2>
        {/if}
        <p>Author: <a href="/~{author.username}">{author.username}</a></p>

        <div class="description">
            {#if editing}
                <input class="edit-description" type="text" bind:value={blog.description}>
            {:else}
                <p>{blog.description}</p>
            {/if}

            {#if isAuthor}
                <div>
                    {#if !editing}
                        <a href="/" class="edit" on:click|preventDefault={() => editing = true}>edit</a>
                    {:else}
                        <a href="/" class="save" on:click|preventDefault={saveBlogMeta}>save</a>
                    {/if}
                    <a href="/" class="delete">delete</a>
                </div>
            {/if}
        </div>
    </div>

    {#if posts.length === 0}
        {#if isAuthor}
            <p class="empty">There's no posts here. You can create a new post <a href="/new/post">here</a>.</p>
        {:else}
            <p class="empty">There's no posts here.</p>
        {/if}
    {:else}
        {#each posts.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()) as post}
            <div class="post">
                <a href="/~{author.username}/{blog.url}/{post.url}" class="post-title">{post.title}</a>
                <div class="meta">
                    <p>{new Date(post.createdAt).toDateString()}</p>

                    {#if post.lastEdit}
                        <p>Edited at: {new Date(post.lastEdit).toDateString()}</p>
                    {/if}

                    <p>{post.upvotes} upvotes</p>
                </div>
            </div>
        {/each}
    {/if}

{:else}
    <h2>Blog not found</h2>
{/if}

<style>
    .title {
        border-bottom: 3px solid var(--nc-bg-2);
        margin-bottom: 1rem;
        padding-bottom: 1rem;
        display: flex;
        flex-direction: row;
        justify-content: space-between;
        align-items: center;
        flex-wrap: wrap;
    }

    .title h2 {
        border-bottom: none;
    }

    .title .description {
        width: 100%;
        display: flex;
        flex-direction: row;
        justify-content: space-between;
        align-items: center;
    }

    .title .description p {
        margin: 0;
    }

    .title .description .edit {
        margin-right: 0.5rem;
    }

    .title .description .save {
        margin-right: 0.5rem;
        color: var(--nc-green);
    }

    .title .description .delete {
        color: var(--nc-red);
    }

    .title .edit-title {
        font-size: 1.85rem;
        margin-bottom: 1.15rem;
    }

    .title .edit-description {
        margin: 0;
    }

    .post {
        border: 3px dashed var(--nc-bg-2);
        padding: 1rem;
        margin-bottom: 1rem;
    }

    .post .post-title {
        padding-bottom: 0.5rem;
        display: inline-block;
        font-size: 1.25rem;
    }

    .post .meta {
        display: flex;
        flex-direction: row;
    }

    .post .meta p {
        margin: 0;
        margin-right: 1rem;
    }

    .post .meta p::after {
        content: '-';
        padding-left: 1rem;
    }

    .post .meta p:last-child::after {
        content: '';
    }
</style>
