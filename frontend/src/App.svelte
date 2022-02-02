<script lang="ts">
    import router from "page";

    import Header from "./components/Header.svelte";
    import Footer from "./components/Footer.svelte";
    import Home from "./pages/Home.svelte";
    import Login from "./pages/Login.svelte";
    import Register from "./pages/Register.svelte";
    import { onMount } from "svelte";
    import { getUser, isLoggedIn } from "./api/auth";
    import CreateBlog from "./pages/Blog/CreateBlog.svelte";
    import Blog from "./pages/Blog/Blog.svelte";
    import CreatePost from "./pages/Post/CreatePost.svelte";
    import Post from "./pages/Post/Post.svelte";
    import EditPost from "./pages/Post/EditPost.svelte";
    import User from "./pages/User/User.svelte";
    import type { User as UserObj } from "./api/user";

    let page: any;
    let params: any;

    router("/", () => (page = Home));
    router("/login", () => (page = Login));
    router("/register", () => (page = Register));

    router(
        "/~:user",
        (ctx, next) => {
            params = ctx.params;
            next();
        },
        () => (page = User)
    );

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
    router(
        "/~:author/:blog/:post",
        (ctx, next) => {
            params = ctx.params;
            next();
        },
        () => (page = Post)
    );
    router(
        "/~:author/:blog/:post/edit",
        (ctx, next) => {
            params = ctx.params;
            next();
        },
        () => (page = EditPost)
    );

    router.start();

    let user: UserObj | null = null;

    onMount(async () => {
        const loggedIn = await isLoggedIn();

        if (loggedIn) {
            user = await getUser();
        }
    });
</script>

<main>
    <Header {user} />

    <svelte:component this={page} {params} />

    <Footer />
</main>
