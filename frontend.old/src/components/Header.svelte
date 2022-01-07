<script lang="ts">
    export let username: string | null;
</script>

<header>
    <nav>
        <ul>
            <li><strong><a href="/">BlogMyst</a></strong></li>
        </ul>

        <ul>
            {#if username}
                <li><a href="/users/{username}">{username}</a></li>
                <li><a href="/logout">Logout</a></li>
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

    nav  {
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
