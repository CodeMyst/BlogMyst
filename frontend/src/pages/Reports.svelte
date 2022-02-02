<script lang="ts">
    import { onMount } from "svelte";
    import { getUser } from "../api/auth";
    import { getReports, Report, resolveReport } from "../api/report";

    let isMod = false;
    let reportsPromise = getReports();

    onMount(async () => {
        const user = await getUser();

        isMod = user.role === "ADMIN" || user.role === "MOD";
    });

    const markResolved = async (id: number) => {
        if (confirm("Are you sure you wan't to mark this report as resolved?")) {
            await resolveReport(id);
        }

        reportsPromise = getReports();
    };
</script>

{#if isMod}
    <h2>Reports</h2>

    {#await reportsPromise then reports}
        {#each reports as report}
            <div class="report">
                <div class="title">
                    <span>Report #{report.id} by <a href="/~{report.user.username}">{report.user.username}</a></span>
                    <a href="/" on:click|preventDefault={() => markResolved(report.id)} class="resolve">Mark resolved</a>
                </div>
                <div class="type">Type: {report.type}</div>
                <div class="date">Date: {new Date(report.date).toLocaleString()}</div>
                <div class="reported">
                    {#if report.type === "BLOG"}
                        Reported blog: <a href="/~{report.subjectId}">{report.subjectId}</a>
                    {/if}

                    {#if report.type === "POST"}
                        Reported post: <a href="/~{report.subjectId}">{report.subjectId}</a>
                    {/if}

                    {#if report.type === "COMMENT"}
                        Reported comment: <a href="/~{report.subjectId}">{report.subjectId}</a>
                    {/if}
                </div>
                <div class="reason">Reason: {report.reason}</div>
            </div>
        {/each}
    {/await}
{:else}
    <h2>Forbidden</h2>
{/if}

<style>
    .report {
        background-color: var(--nc-bg-2);
        border-radius: 4px;
        margin-bottom: 1rem;
        padding: 1rem;
    }

    .report .title {
        font-size: 1.25rem;
        margin-bottom: 0.5rem;
    }

    .report .type,
    .report .date,
    .report .reported {
        font-size: 0.9rem;
    }

    .report .reason {
        margin-top: 1rem;
        font-size: 1.1rem;
    }

    .report .title {
        display: flex;
        flex-direction: row;
        justify-content: space-between;
    }

    .report .resolve {
        color: var(--nc-red);
    }
</style>
