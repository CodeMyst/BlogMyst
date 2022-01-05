<script lang="ts" context="module">
    import "../app.css";

    import Header from "../components/Header.svelte";
    import Footer from "..//components/Footer.svelte";
</script>

<main>
    <Header />

    <slot />

    <Footer />
</main>
