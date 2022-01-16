<script lang="ts">
    import { logout } from "../api/auth";

    export let username: string | null;

    const onLogout = async () => {
        await logout();
        window.location.href = "/";
    };
</script>

<header>
    <nav>
        <ul>
            <li><strong><a href="/">BlogMyst</a></strong></li>
        </ul>

        <ul>
            {#if username}
                <li><a href="/~{username}">{username}</a></li>
                <!-- TODO: just a temporary place for a new blog link -->
                <li><a href="/new/blog">New Blog</a></li>
                <li><a href="/new/post">New Post</a></li>
                <li><a href="/" on:click={onLogout}>Logout</a></li>
            {:else}
                <li><a href="/login">Login</a></li>
                <li><a href="/register">Register</a></li>
            {/if}
        </ul>
    </nav>
</header>

<style>
    header {
        background-color: var(--nc-bg-4);
    }

    nav {
        display: flex;
        flex-direction: row;
        justify-content: space-between;
    }

    nav ul {
        list-style: none;
        padding-left: 0;
        display: flex;
        flex-direction: row;
    }

    nav ul:nth-child(2) li {
        margin-left: 1rem;
    }
</style>
