<script lang="ts" context="module">
    declare const EasyMDE: any;
</script>

<script lang="ts">
    import { onMount, tick } from "svelte";
    import { editPost, getPost, Post, PostCreateResult } from "../../api/blog";
    import { getUser, User } from "../../api/user";
    import showdown from "showdown";
    import { getUsername } from "../../api/auth";

    export let params: { author: string; blog: string; post: string };

    let post: Post;
    let author: User;
    let found = false;

    let date: Date;

    let htmlContent: string;

    let isAuthor = false;

    let editor: any;

    let res: PostCreateResult | null = null;

    onMount(async () => {
        post = await getPost(params.author, params.blog, params.post);
        author = await getUser(params.author);

        if (post) found = true;

        if (!found) return;

        date = new Date(post.createdAt);

        const converter = new showdown.Converter();
        htmlContent = converter.makeHtml(post.content);

        const currentUser = await getUsername();
        isAuthor = currentUser === author.username;

        if (!isAuthor) return;

        await tick();

        editor = new EasyMDE({
            autofocus: true,
            placeholder: "Insert your content here...",
            promptURLs: true
        });

        editor.value(post.content);
    });

    const onSubmit = async () => {
        res = await editPost(author.username, params.blog, params.post, post.title, editor.value());
    };
</script>

{#if !isAuthor}
    <h2>Forbidden</h2>
{:else}
    <h2>Edit a post</h2>

    {#if res && res.success}
        <div class="status-message success">
            Successfully edited the post. You can view it at: <a href="/~{author.username}/{params.blog}/{params.post}">/~{author.username}/{params.blog}/{params.post}</a>.
        </div>
    {:else if res && !res.success}
        <div class="status-message error">
            {res.message}
        </div>
    {/if}

    <form on:submit|preventDefault={onSubmit}>
        <div class="title-input">
            <label for="title">Title:</label>
            <input type="text" name="title" id="title" placeholder="Post Title..." bind:value={post.title}>
        </div>

        <textarea />

        <div class="edit">
            <button type="submit">Edit</button>
        </div>
    </form>
{/if}

<style>
    h2 {
        margin-bottom: 1rem;
    }

    .edit {
        margin-top: 1rem;
        margin-bottom: 1rem;
        display: flex;
        flex-direction: row;
        justify-content: space-between;
        align-items: center;
    }

    .title-input {
        display: flex;
        flex-direction: row;
        width: 100%;
        align-items: center;
        margin-bottom: 1rem;
    }

    .title-input label {
        margin-right: 1rem;
    }

    .title-input input {
        flex-grow: 1;
        margin: 0;
    }
</style>