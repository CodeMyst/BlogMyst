<script lang="ts" context="module">
    declare const EasyMDE: any;
</script>

<script lang="ts">
    import { onMount, tick } from "svelte";
    import { getUsername } from "../../api/auth";
    import { Blog, getBlogs } from "../../api/blog";

    let blogs: Blog[];

    onMount(async () => {
        blogs = await getBlogs(await getUsername());

        await tick();

        new EasyMDE({
            autofocus: true,
            placeholder: "Insert your content here...",
            promptURLs: true
        });
    });
</script>

<h2>Create a new Post</h2>

{#if blogs === undefined || blogs.length === 0}
    <p>You have no blogs. Create one <a href="/new/blog">here</a>.</p>
{:else}
    <form>
        <label for="blog">Blog to publish in:</label>
        <select name="blog" id="blog">
            {#each blogs as blog}
                <option value={blog.url}>{blog.name}</option>
            {/each}
        </select>

        <textarea />

        <button type="submit">Publish</button>
    </form>
{/if}

<style>
    h2 {
        margin-bottom: 1rem;
    }

    button {
        margin-top: 1rem;
        margin-bottom: 1rem;
    }
</style>
