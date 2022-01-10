<script lang="ts">
    import { onMount } from "svelte";
    import { Blog, getBlog, getPost, Post } from "../../api/blog";
    import { getUser, User } from "../../api/user";
    import showdown from "showdown";

    export let params: { author: string; blog: string; post: string };

    let post: Post;
    let author: User;
    let found = false;

    let date: Date;

    let htmlContent: string;

    onMount(async () => {
        post = await getPost(params.author, params.blog, params.post);
        author = await getUser(params.author);

        if (post) found = true;

        if (!found) return;

        date = new Date(post.createdAt);

        const converter = new showdown.Converter();
        htmlContent = converter.makeHtml(post.content);
    });
</script>

{#if found}
    <h2>{post.title}</h2>

    <div class="meta">
        <p>Posted on <span class="date">{date.toDateString()}</span> by <a href="/~{author.username}">{author.username}</a> in <a href="/~{author.username}/{post.blog.url}">{post.blog.name}</a></p>
    </div>

    <div class="content">
        {@html htmlContent}
    </div>
{:else}
    <h2>Post not found</h2>
{/if}

<style>
    .meta {
        font-size: 0.9rem;
    }

    .meta .date {
        color: var(--nc-lk-2);
    }
</style>
