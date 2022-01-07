<script lang="ts">
    import router from "page";

    import Header from "./components/Header.svelte";
    import Footer from "./components/Footer.svelte";
    import Home from "./pages/Home.svelte";
    import Login from "./pages/Login.svelte";
    import Register from "./pages/Register.svelte";
    import { onMount } from "svelte";
    import { getUsername, isLoggedIn } from "./auth";

    let page: any;

    router("/", () => (page = Home));
    router("/login", () => (page = Login));
    router("/register", () => (page = Register));

    router.start();

    let username: string | null = null;

    onMount(async () => {
        const loggedIn = await isLoggedIn();

        if (loggedIn) {
            username = await getUsername();
        }
    });
</script>

<main>
    <Header {username} />

    <svelte:component this={page} />

    <Footer />
</main>
