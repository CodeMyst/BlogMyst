<script lang="ts">
    import Showdown from "showdown";
    import { onMount } from "svelte";
    import { isLoggedIn } from "../api/auth";
    import { getAllPosts, getFollowedPosts } from "../api/post";
    import { getFollowedBlogs } from "../api/user";

    let loggedIn: boolean = false;
    let viewFollowed: boolean = false;

    let allPostsPage = 0;
    let followedPostsPage = 0;

    let postsPromise = getAllPosts(allPostsPage);

    onMount(async () => {
        loggedIn = await isLoggedIn();
    });

    const convertToHtml = (content: string): string => {
        const converter = new Showdown.Converter();
        return converter.makeHtml(content);
    };

    const toggleViewFollowed = (v: boolean) => {
        viewFollowed = v;
        postsPromise = viewFollowed ? getFollowedPosts(followedPostsPage) : getAllPosts(allPostsPage);
    };

    const prevPage = () => {
        if (viewFollowed) {
            followedPostsPage--;
        } else {
            allPostsPage--;
        }
        postsPromise = viewFollowed ? getFollowedPosts(followedPostsPage) : getAllPosts(allPostsPage);
        window.scrollTo(0, 0);
    };

    const nextPage = () => {
        if (viewFollowed) {
            followedPostsPage++;
        } else {
            allPostsPage++;
        }
        postsPromise = viewFollowed ? getFollowedPosts(followedPostsPage) : getAllPosts(allPostsPage);
        window.scrollTo(0, 0);
    };
</script>

{#if loggedIn}
<div class="select-posts">
    <a href="/" class:selected={!viewFollowed} on:click|preventDefault={() => toggleViewFollowed(false)}>all blogs</a>
    <a href="/" class:selected={viewFollowed} on:click|preventDefault={() => toggleViewFollowed(true)}>followed blogs</a>
</div>
{/if}

<div class="feed">
    {#await postsPromise}
        <p>Loading posts...</p>
    {:then posts} 
        {#if viewFollowed}
            {#await getFollowedBlogs() then followed}
                {#if followed.length === 0}
                    <p>You aren't following any blogs.</p>
                {:else}
                    <details>
                        <summary>List of followed blogs</summary>

                        <ul>
                            {#each followed as blog}
                                <li><a href="/~{blog.author.username}/{blog.url}">{blog.author.username}/{blog.url}</a></li>
                            {/each}
                        </ul>
                    </details>
                {/if}
            {/await}
        {/if}

        {#if posts.content.length === 0}
            <p>No posts.</p>
        {/if}

        {#each posts.content as post}
            <article>
                <div class="post-heading">
                    <div class="title">
                        <a href="/~{post.blog.author.username}/{post.blog.url}/{post.url}"><h3>{post.title}</h3></a>
                        <div class="meta">
                            <small class="date">Published on {new Date(post.createdAt).toDateString()}</small>

                            {#if post.lastEdit !== null}
                                <small class="date">Edited on {new Date(post.lastEdit).toDateString()}</small>
                            {/if}

                            <small class="posted-by">
                                by <a href="/~{post.blog.author.username}">{post.blog.author.username}</a> in
                                <a href="/~{post.blog.author.username}/{post.blog.url}">{post.blog.name}</a>
                            </small>
                        </div>
                    </div>
                </div>

                <div class="content">
                    {@html convertToHtml(post.content)}
                </div>
            </article>
        {/each}

        {#if posts.totalPages > 1}
            <div class="pager">
                {#if !posts.first}
                    <a href="/" on:click|preventDefault={() => prevPage()}>&laquo;</a>
                {/if}

                <span>page {posts.number + 1}</span>

                {#if !posts.last}
                    <a href="/" on:click|preventDefault={() => nextPage()}>&raquo;</a>
                {/if}
            </div>
        {/if}
    {/await}
</div>

<style>
    article {
        padding: 1.5rem;
        border: 3px dashed var(--nc-bg-2);
        margin-bottom: 2rem;
    }

    .title {
        display: flex;
        flex-direction: row;
        justify-content: space-between;
        border-bottom: 3px dashed var(--nc-bg-2);
        padding-bottom: 1rem;
        margin-bottom: 1rem;
        align-items: center;
    }

    .title > a {
        text-decoration: none;
    }

    .title h3 {
        padding: 0;
        border-bottom: none;
    }

    .title .meta small {
        display: block;
        text-align: right;
    }

    .select-posts {
        margin-bottom: 2rem;
    }

    .select-posts a {
        margin-right: 0.5rem;
        background-color: var(--nc-bg-2);
        color: var(--nc-tx-1);
        text-decoration: none;
        padding: 0.5rem 1rem;
        border-radius: 4px;
    }

    .select-posts a.selected {
        color: var(--nc-lk-1);
    }

    .pager {
        text-align: center;
        margin-bottom: 1rem;
    }
</style>
