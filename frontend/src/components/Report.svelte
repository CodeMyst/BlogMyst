<script lang="ts">
    import { createEventDispatcher } from "svelte";

    export let visible: boolean = false;

    let contentElement: HTMLElement;

    let reason: string;

    const dispatch = createEventDispatcher();

    const backgroundClick = (event: MouseEvent) => {
        if (event.target !== contentElement) close();
    };

    const close = () => {
        visible = false;
        reason = "";
    };

    const report = () => {
        dispatch("report", {
            reason: reason
        });
        close();
    };
</script>

<div class="report">
    <div class="background" class:visible on:click={backgroundClick} />
    <div class="content" class:visible bind:this={contentElement}>
        <div class="title">Report</div>

        <textarea placeholder="Reason for reporting..." rows="6" bind:value={reason}></textarea>

        <div class="buttons">
            <button class="cancel" on:click={close}>Cancel</button>
            <button on:click={report}>Report</button>
        </div>
    </div>
</div>

<style>
    .background {
        width: 100%;
        height: 100%;
        position: fixed;
        z-index: 10;
        top: 0;
        left: 0;
        background-color: var(--nc-bg-1);
        display: block;
        opacity: 0;
        visibility: hidden;
    }

    .background.visible {
        opacity: 0.5;
        visibility: visible;
    }

    .content {
        z-index: 15;
        position: fixed;
        top: 35vh;
        left: 0;
        right: 0;
        max-width: 90%;
        width: 720px;
        margin: 0 auto;
        display: none;
        background-color: var(--nc-bg-2);
        border-radius: 4px;
        padding: 1rem;
    }

    .content.visible {
        display: flex;
        flex-direction: column;
    }

    .title {
        font-size: 1.5rem;
        margin-bottom: 1rem;
    }

    textarea {
        resize: none;
        background-color: var(--nc-bg-1);
        margin-bottom: 1rem;
    }

    .buttons {
        display: flex;
        flex-direction: row;
        justify-content: space-evenly;
    }

    .buttons .cancel {
        background-color: var(--nc-bg-3);
        color: var(--nc-tx-1);
    }

    .buttons button {
        width: 100%;
        margin-right: 1rem;
    }
</style>