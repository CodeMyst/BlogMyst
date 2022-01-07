<script lang="ts" context="module">
    import "../app.css";

    import Header from "../components/Header.svelte";
    import Footer from "..//components/Footer.svelte";

    export const load = async ({ session }) => {
        return {
            props: {
                username: session.user
            }
        };
    };
</script>

<script lang="ts">
    export let username: string | null;
</script>

<main>
    <Header {username} />

    <slot />

    <Footer />
</main>
