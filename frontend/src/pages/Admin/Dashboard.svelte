<script lang="ts">
    import { onMount } from "svelte";
    import { getUser, isLoggedIn } from "../../api/auth";

    let isMod = false;

    onMount(async () => {
        if (await isLoggedIn()) {
            const user = await getUser();
            isMod = user.role === "ADMIN" || user.role === "MOD";
        }
    });
</script>

{#if isMod}
    <h2>Admin Dashboard</h2>

    <h4><a href="/admin/reports">View Reports</a></h4>
    <span>View content that was reported by other users.</span>

    <h4><a href="/admin/jasper">Generate Jasper Reports</a></h4>
    <span>Generate various reports on the website data in PDF form.</span>
{:else}
    <h2>Forbidden</h2>
{/if}

<style>
    span {
        margin-bottom: 1rem;
        display: inline-block;
    }
</style>