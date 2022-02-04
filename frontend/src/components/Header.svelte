<script lang="ts">
    import { logout } from "../api/auth";
    import type { User } from "../api/user";

    export let user: User | null;

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
            {#if user}
                <li>
                    <a href="/~{user.username}">{user.username}</a>
                    {#if user.role === "ADMIN"}
                        <span class="admin">[A]</span>
                    {/if}
                    {#if user.role === "MOD"}
                        <span class="mod">[M]</span>
                    {/if}
                    {#if user.role === "BANNED"}
                        <span class="mod">[B]</span>
                    {/if}
                </li>

                {#if user.role === "ADMIN" || user.role === "MOD"}
                    <li><a href="/mod/reports">View Reports</a></li>
                {/if}

                {#if user.role !== "BANNED"}
                    <li><a href="/new/blog">New Blog</a></li>
                    <li><a href="/new/post">New Post</a></li>
                {/if}
                <li><a href="/" on:click={onLogout}>Logout</a></li>
            {:else}
                <li><a href="/login">Login</a></li>
                <li><a href="/register">Register</a></li>
            {/if}
        </ul>
    </nav>
</header>

{#if user?.role === "BANNED"}
    <div class="banned">
        You have been banned temporarily. You can still browse the website, however you can't post any new content.
    </div>
{/if}

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

    .admin {
        color: var(--nc-ac-1);
    }

    .mod {
        color: var(--nc-red);
    }

    .banned {
        background-color: var(--nc-red);
        border-radius: 4px;
        padding: 0.5rem 1rem;
        color: var(--nc-bg-1);
        margin-top: -1rem;
        margin-bottom: 1rem;
    }
</style>
