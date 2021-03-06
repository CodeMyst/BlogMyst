<script lang="ts">
    import { onMount } from "svelte";
    import { getUser as getCurrentUser, isLoggedIn } from "../../api/auth";
    import { Blog, deleteBlog, editBlog, getBlog, getBlogPosts } from "../../api/blog";
    import type { Post } from "../../api/post";
    import { reportBlog } from "../../api/report";
    import { getUser, isFollowingBlog, toggleFollowBlog, User } from "../../api/user";
    import Report from "../../components/Report.svelte";

    export let params: { author: string; blog: string };

    let blog: Blog;
    let author: User;
    let found = false;

    let loggedIn = false;
    let isAuthor = false;

    let editing = false;

    let posts: Post[] = [];

    let isFollowing = false;

    let reportVisible = false;

    let currentUser: User;
    let isAdmin = false;

    onMount(async () => {
        blog = await getBlog(params.author, params.blog);
        author = await getUser(params.author);

        if (blog) found = true;

        if (!found) return;

        loggedIn = await isLoggedIn();

        if (loggedIn) {
            currentUser = await getCurrentUser();
            isAuthor = currentUser.username === author.username;
            isFollowing = await isFollowingBlog(author.username, blog.url);
            isAdmin = currentUser.role === "ADMIN";
        }

        posts = await getBlogPosts(author.username, blog.url);
    });

    const saveBlogMeta = async () => {
        await editBlog(author.username, blog.url, blog.name, blog.description);
        editing = false;
    };

    const onDeleteBlog = async () => {
        if (confirm("Are you sure you want to delete your blog?")) {
            await deleteBlog(author.username, blog.url);
        }
    };

    const onFollow = async () => {
        toggleFollowBlog(author.username, blog.url);
        isFollowing = !isFollowing;
    };

    const onReportClick = () => {
        reportVisible = true;
    };

    const onReportSubmit = async (event: CustomEvent) => {
        await reportBlog(author.username, blog.url, event.detail.reason);
    };
</script>

{#if found}
    <div class="title">
        {#if editing}
            <input class="edit-title" type="text" bind:value={blog.name}>
        {:else}
            <div class="title-follow">
                <h2>{blog.name}</h2>
                {#if loggedIn}
                    {#if isFollowing}
                        <a href="/" on:click|preventDefault={onFollow}>- unfollow</a>
                    {:else}
                        <a href="/" on:click|preventDefault={onFollow}>+ follow</a>
                    {/if}
                    {#if currentUser?.role !== "BANNED"}
                        <span class="separator">|</span>
                        <a href="/" on:click|preventDefault={onReportClick}>report</a>
                    {/if}
                {/if}
            </div>
        {/if}
        <p>Author: <a href="/~{author.username}">{author.username}</a></p>

        <div class="description">
            {#if editing}
                <input class="edit-description" type="text" bind:value={blog.description}>
            {:else}
                <p>{blog.description}</p>
            {/if}

            <div>
                {#if isAuthor && currentUser?.role !== "BANNED"}
                    {#if !editing}
                        <a href="/" class="edit" on:click|preventDefault={() => editing = true}>edit</a>
                    {:else}
                        <a href="/" class="save" on:click|preventDefault={saveBlogMeta}>save</a>
                    {/if}
                {/if}

                {#if (isAuthor && currentUser?.role !== "BANNED") || isAdmin}
                    <a href="/" class="delete" on:click={onDeleteBlog}>delete</a>
                {/if}
            </div>
        </div>
    </div>

    {#if posts.length === 0}
        {#if isAuthor && currentUser?.role !== "BANNED"}
            <p class="empty">There's no posts here. You can create a new post <a href="/new/post">here</a>.</p>
        {:else if isAuthor && currentUser?.role === "BANNED"}
            <p class="empty">There's no posts here. And you can't create one since you are banned.</p>
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

{#if loggedIn}
    <Report bind:visible={reportVisible} on:report={onReportSubmit} />
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
        margin: 0;
        padding: 0;
        margin-right: 1rem;
    }

    .title .title-follow {
        display: flex;
        flex-direction: row;
        align-items: center;
        margin-bottom: 1rem;
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

    .separator {
        margin-left: 0.5rem;
        margin-right: 0.5rem;
    }
</style>
