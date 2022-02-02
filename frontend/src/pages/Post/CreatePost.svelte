<script lang="ts" context="module">
    declare const EasyMDE: any;
</script>

<script lang="ts">
    import { onMount, tick } from "svelte";
    import { getUser} from "../../api/auth";
    import { Blog, createPost, getBlogs, PostCreateResult } from "../../api/blog";

    let blogs: Blog[];
    let selectedBlogUrl: string;

    let title: string;

    let res: PostCreateResult | null = null;

    let currentUsername: string;

    let editor: any;

    onMount(async () => {
        currentUsername = (await getUser()).username;
        blogs = await getBlogs(currentUsername);

        await tick();

        editor = new EasyMDE({
            autofocus: true,
            placeholder: "Insert your content here...",
            promptURLs: true
        });
    });

    const onSubmit = async () => {
        res = await createPost(currentUsername, selectedBlogUrl, title, editor.value());
    };
</script>

<h2>Create a new Post</h2>

{#if blogs === undefined || blogs.length === 0}
    <p>You have no blogs. Create one <a href="/new/blog">here</a>.</p>
{:else}
    {#if res && res.success}
        <div class="status-message success">
            Successfully created the post. You can view it at: <a href="/~{currentUsername}/{selectedBlogUrl}/{res.url}">/~{currentUsername}/{selectedBlogUrl}/{res.url}</a>.
        </div>
    {:else if res && !res.success}
        <div class="status-message error">
            {res.message}
        </div>
    {/if}

    <form on:submit|preventDefault={onSubmit}>
        <div class="title-input">
            <label for="title">Title:</label>
            <input type="text" name="title" id="title" placeholder="Post Title..." bind:value={title} />
        </div>

        <textarea />

        <div class="publish">
            <button type="submit">Publish</button>

            <div>
                <label for="blog">Blog to publish in:</label>
                <select name="blog" id="blog" bind:value={selectedBlogUrl}>
                    {#each blogs as blog}
                        <option value={blog.url}>{blog.name}</option>
                    {/each}
                </select>
            </div>
        </div>
    </form>
{/if}

<style>
    h2 {
        margin-bottom: 1rem;
    }

    .publish {
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
