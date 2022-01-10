<script lang="ts" context="module">
    declare const EasyMDE: any;
</script>

<script lang="ts">
    import { onMount } from "svelte";

    onMount(() => {
        new EasyMDE({
            autofocus: true,
            placeholder: "Insert your content here...",
            promptURLs: true
        });
    });
</script>

<h2>Create a new Post</h2>

<textarea />

<button type="submit">Publish</button>

<style>
    h2 {
        margin-bottom: 1rem;
    }

    button {
        margin-top: 1rem;
        margin-bottom: 1rem;
    }
</style>
