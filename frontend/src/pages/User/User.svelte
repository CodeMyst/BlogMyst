<script lang="ts">
    import { onMount, tick } from "svelte";
    import { getUser as getCurrentUser, isLoggedIn } from "../../api/auth";
    import { Blog, getBlogs } from "../../api/blog";
    import { deleteUser, getUser, setUserRole, User } from "../../api/user";

    export let params: { user: string; };

    let user: User = null;
    let blogs: Blog[] = [];

    let currentUser: User;
    let isAdmin = false;

    let selectedRole: string;

    onMount(async () => {
        user = await getUser(params.user);

        if (await isLoggedIn()) {
            currentUser = await getCurrentUser();
            isAdmin = currentUser.role === "ADMIN";
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
        </div>

        <div>
            {#if isAdmin}
                <select name="role" on:change={onRoleChange} bind:value={selectedRole}>
                    <option value="ADMIN">Admin</option>
                    <option value="MOD">Mod</option>
                    <option value="USER">User</option>
                </select>
            {/if}

            {#if user.username === currentUser?.username || isAdmin}
                <a class="delete" href="/" on:click|preventDefault={onDeleteUser}>delete user</a>
            {/if}
        </div>
    </h2>

    {#if blogs.length === 0}
        {#if currentUser?.username === user.username}
            <p>You don't have any blogs. Create one <a href="/new/blog">here</a>.</p>
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
</style>
