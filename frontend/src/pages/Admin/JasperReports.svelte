<script lang="ts">
    import { onMount } from "svelte";
import { API_BASE } from "../../api/api";
    import { getUser, isLoggedIn } from "../../api/auth";

    let isMod = false;

    let postsReportFrom: Date;
    let postsReportTo: Date;

    let usersReportFrom: Date;
    let usersReportTo: Date;

    onMount(async () => {
        if (await isLoggedIn()) {
            const user = await getUser();
            isMod = user.role === "ADMIN" || user.role === "MOD";
        }
    });
</script>

{#if isMod}
    <h2>Generate Jasper Reports</h2>

    <h3>Posts Report</h3>
    <span class="description">Generates a report on posts created in the specified time frame.</span>

    <div class="fields">
        <span>From</span>
        <input type="date" bind:value={postsReportFrom} />

        <span>To</span>
        <input type="date" bind:value={postsReportTo} />

        <a target="_blank" href="{API_BASE}/jasper/posts?from={postsReportFrom}&to={postsReportTo}">Generate Report</a>
    </div>

    <h3>Users Report</h3>
    <span class="description">Generates a report on all users that registered in the specified time frame.</span>

    <div class="fields">
        <span>From</span>
        <input type="date" bind:value={usersReportFrom} />

        <span>To</span>
        <input type="date" bind:value={usersReportTo} />

        <a target="_blank" href="{API_BASE}/jasper/users?from={usersReportFrom}&to={usersReportTo}">Generate Report</a>
    </div>
{:else}
    <h2>Forbidden</h2>
{/if}

<style>
    .description {
        margin-bottom: 2rem;
        display: block;
    }

    .fields {
        display: grid;
        grid-template-columns: 150px 1fr;
        margin-bottom: 1rem;
    }

    .fields a {
        grid-column: 2/3;
        background-color: var(--nc-lk-1);
        border-radius: 4px;
        padding: 0.25rem 0.5rem;
        color: var(--nc-bg-1);
        text-decoration: none;
    }

    .fields a:hover {
        background-color: var(--nc-lk-2);
    }
</style>
