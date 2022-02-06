<script lang="ts">
    import { onMount, tick } from "svelte";
    import { getUser as getCurrentUser, isLoggedIn } from "../../api/auth";
    import { Blog, getBlogs } from "../../api/blog";
    import { banUser, deleteUser, getUser, setUserRole, unbanUser, User } from "../../api/user";

    export let params: { user: string; };

    let user: User = null;
    let blogs: Blog[] = [];

    let currentUser: User;
    let isAdmin = false;
    let isMod = false;

    let selectedRole: string;

    onMount(async () => {
        user = await getUser(params.user);

        if (await isLoggedIn()) {
            currentUser = await getCurrentUser();
            isAdmin = currentUser.role === "ADMIN";
            isMod = isAdmin || currentUser.role === "MOD";
        }

        if (user === null) return;

        selectedRole = user.role;

        blogs = await getBlogs(params.user);
    });

    const onRoleChange = async () => {
        await tick();

        await setUserRole(user.username, selectedRole);

        location.reload();
    };

    const onDeleteUser = async () => {
        if (confirm("Are you sure you want to delete this account?")) {
            await deleteUser(user.username);
            window.location.href = "/";
        }
    };

    const onBanUser = async () => {
        if (confirm("Are you sure you want to ban this user?")) {
            await banUser(user.username);
            user.role = "BANNED";
            selectedRole = user.role;
        }
    };

    const onUnbanUser = async () => {
        if (confirm("Are you sure you want to unban this user?")) {
            await unbanUser(user.username);
            user.role = "USER";
            selectedRole = user.role;
        }
    };
</script>

{#if user === null}
    <h2>User not found</h2>
{:else}
    <h2>
        <div>
            {user.username}
            {#if user.role === "ADMIN"}
                <span class="admin">[A]</span>
            {/if}
            {#if user.role === "MOD"}
                <span class="mod">[M]</span>
            {/if}
            {#if user.role === "BANNED"}
                <span class="mod">[BANNED]</span>
            {/if}
        </div>

        <div class="links">
            {#if isAdmin && user.role !== "BANNED"}
                <select name="role" on:change={onRoleChange} bind:value={selectedRole}>
                    <option value="ADMIN">Admin</option>
                    <option value="MOD">Mod</option>
                    <option value="USER">User</option>
                </select>
            {/if}

            {#if isMod}
                {#if user.role === "BANNED"}
                    <a class="delete" href="/" on:click|preventDefault={onUnbanUser}>unban</a>
                {/if}
                {#if user.role === "USER"}
                    <a class="delete" href="/" on:click|preventDefault={onBanUser}>ban</a>
                {/if}
            {/if}

            {#if user.username === currentUser?.username || isAdmin}
                <a class="delete" href="/" on:click|preventDefault={onDeleteUser}>delete user</a>
            {/if}
        </div>
    </h2>

    <p>Registered on {new Date(user?.createdAt).toLocaleString()}</p>

    {#if blogs.length === 0}
        {#if currentUser?.username === user.username && currentUser?.role !== "BANNED"}
            <p>You don't have any blogs. Create one <a href="/new/blog">here</a>.</p>
        {:else if currentUser?.username === user.username && currentUser?.role === "BANNED"}
            <p>You don't have any blogs. And you can't create one since you're banned.</p>
        {:else}
            <p>This user has no blogs.</p>
        {/if}
    {:else}
        {#each blogs as blog}
            <div class="blog">
                <a href="/~{user.username}/{blog.url}">{blog.name}</a>
                <p>{blog.description}</p>
            </div>
        {/each}
    {/if}
{/if}

<style>
    .blog {
        border: 3px dashed var(--nc-bg-2);
        padding: 1rem;
        margin-bottom: 1rem;
    }

    .blog a {
        padding-bottom: 0.5rem;
        display: inline-block;
        font-size: 1.25rem;
    }

    .blog p {
        margin-bottom: 0;
    }

    .admin {
        color: var(--nc-ac-1);
    }

    .mod {
        color: var(--nc-red);
    }

    h2 {
        display: flex;
        flex-direction: row;
        justify-content: space-between;
        align-items: center;
    }

    .delete {
        font-size: 1rem;
        color: var(--nc-red);
        margin-left: 0.5rem;
    }

    h2 .links {
        display: flex;
        flex-direction: row;
        align-items: center;
    }

    h2 select {
        margin: 0;
    }
</style>
