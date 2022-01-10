<script lang="ts">
    import router from "page";

    import Header from "./components/Header.svelte";
    import Footer from "./components/Footer.svelte";
    import Home from "./pages/Home.svelte";
    import Login from "./pages/Login.svelte";
    import Register from "./pages/Register.svelte";
    import { onMount } from "svelte";
    import { getUsername, isLoggedIn } from "./api/auth";
    import CreateBlog from "./pages/Blog/CreateBlog.svelte";
    import Blog from "./pages/Blog/Blog.svelte";
    import CreatePost from "./pages/Post/CreatePost.svelte";

    let page: any;
    let params: any;

    router("/", () => (page = Home));
    router("/login", () => (page = Login));
    router("/register", () => (page = Register));

    router("/new/blog", () => (page = CreateBlog));
    router(
        "/~:author/:blog",
        (ctx, next) => {
            params = ctx.params;
            next();
        },
        () => (page = Blog)
    );

    router("/new/post", () => (page = CreatePost));

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

    <svelte:component this={page} {params} />

    <Footer />
</main>
