<script lang="ts">
    import { onMount } from "svelte";
    import { deletePost, getPost } from "../../api/blog";
    import { getUser, User } from "../../api/user";
    import showdown from "showdown";
    import { getUsername, isLoggedIn } from "../../api/auth";
    import { downvote, Post, upvote } from "../../api/post";

    export let params: { author: string; blog: string; post: string };

    let post: Post;
    let author: User;
    let found = false;

    let date: Date;
    let editDate: Date | null = null;

    let htmlContent: string;

    let isAuthor = false;

    let loggedIn = false;

    onMount(async () => {
        post = await getPost(params.author, params.blog, params.post);
        author = await getUser(params.author);

        if (post) found = true;

        if (!found) return;

        date = new Date(post.createdAt);

        if (post.lastEdit) editDate = new Date(post.lastEdit);

        const converter = new showdown.Converter();
        htmlContent = converter.makeHtml(post.content);

        if (await isLoggedIn()) {
            const currentUser = await getUsername();
            isAuthor = currentUser === author.username;
            loggedIn = true;
        }
    });

    const onDelete = async () => {
        if (confirm("Are you sure you want to delete this post?")) {
            await deletePost(author.username, post.blog.url, post.url);
        }
    };

    const onUpvote = async () => {
        await upvote(author.username, post.blog.url, post.url);
        post.upvotes++;
    };

    const onDownvote = async () => {
        await downvote(author.username, post.blog.url, post.url);
        post.upvotes--;
    };
</script>

{#if found}
    <div class="title">
        <div class="row">
            <div class="title-upvotes">
                <div class="upvotes">
                    <a href="/" on:click|preventDefault={() => onUpvote()}>
                        ⇡
                    </a>

                    <div>
                        {post.upvotes}
                    </div>

                    <a href="/" on:click|preventDefault={() => onDownvote()}>
                        ⇣
                    </a>
                </div>

                <h2>{post.title}</h2>
            </div>

            {#if isAuthor}
                <div class="edit-links">
                    <a href="/~{author.username}/{post.blog.url}/{post.url}/edit" class="edit">edit</a>
                    <a href="/" on:click={onDelete} class="delete">delete</a>
                </div>
            {/if}
        </div>

        <div class="meta">
            <p>Posted on: <span class="date">{date.toDateString()}</span> by <a href="/~{author.username}">{author.username}</a> in <a href="/~{author.username}/{post.blog.url}">{post.blog.name}</a></p>
            {#if editDate}
                <p>Edited on: <span class="date">{editDate.toDateString()}</span></p>
            {/if}
        </div>
    </div>

    <div class="content">
        {@html htmlContent}
    </div>
{:else}
    <h2>Post not found</h2>
{/if}

<style>
    h2 {
        border-bottom: none;
    }

    .title {
        border-bottom: 3px solid var(--nc-bg-2);
        margin-bottom: 1rem;
    }

    .title .row {
        display: flex;
        flex-direction: row;
        justify-content: space-between;
        align-items: center;
    }

    .title .row .edit {
        margin-right: 1rem;
    }

    .title .row .delete {
        color: var(--nc-red);
    }

    .meta {
        font-size: 0.9rem;
        padding-bottom: 1rem;
    }

    .meta p {
        margin: 0;
    }

    .title-upvotes {
        display: flex;
        flex-direction: row;
        align-items: center;
    }

    .title-upvotes .upvotes {
        margin-right: 1rem;
        text-align: center;
    }

    .title-upvotes h2 {
        margin: 0;
    }

    .title-upvotes .upvotes a {
        text-decoration: none;
    }
</style>
