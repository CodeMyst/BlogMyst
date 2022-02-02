<script lang="ts">
    import { onMount } from "svelte";
    import { deletePost, getPost } from "../../api/blog";
    import { getUser, User } from "../../api/user";
    import showdown from "showdown";
    import { getUser as getCurrentUser, isLoggedIn } from "../../api/auth";
    import { Comment, deleteComment, downvote, editComment, getComments, Page, Post, postComment, upvote } from "../../api/post";
    import Report from "../../components/Report.svelte";
    import { reportComment, reportPost } from "../../api/report";

    export let params: { author: string; blog: string; post: string };

    let post: Post;
    let author: User;
    let found = false;

    let date: Date;
    let editDate: Date | null = null;

    let htmlContent: string;

    let isAuthor = false;

    let loggedIn = false;

    let commentContent: string;

    let commentsPage = 0;
    let commentsPromise: Promise<Page<Comment>>;

    let currentUser: string;

    let editingComment: number;
    let editingCommentContent: string;

    let reportPostVisible = false;
    let reportCommentVisible = false;
    let reportCommentId: number;

    let targetCommentId: number;
    targetCommentId = parseInt(window.location.hash.slice(1));

    onMount(async () => {
        post = await getPost(params.author, params.blog, params.post);
        author = await getUser(params.author);

        if (post) found = true;

        if (!found) return;

        date = new Date(post.createdAt);

        if (post.lastEdit) editDate = new Date(post.lastEdit);

        const converter = new showdown.Converter();
        htmlContent = converter.makeHtml(post.content);

        if (await isLoggedIn()) {
            currentUser = (await getCurrentUser()).username;
            isAuthor = currentUser === author.username;
            loggedIn = true;
        }

        commentsPromise = getComments(author.username, post.blog.url, post.url, commentsPage);
    });

    const onDelete = async () => {
        if (confirm("Are you sure you want to delete this post?")) {
            await deletePost(author.username, post.blog.url, post.url);
        }
    };

    const onUpvote = async () => {
        await upvote(author.username, post.blog.url, post.url);
        post.upvotes++;
    };

    const onDownvote = async () => {
        await downvote(author.username, post.blog.url, post.url);
        post.upvotes--;
    };

    const onPostComment = async () => {
        await postComment(author.username, post.blog.url, post.url, commentContent);
        commentContent = "";
        commentsPage = 0;
        commentsPromise = getComments(author.username, post.blog.url, post.url, commentsPage);
        setTimeout(() => {window.scrollTo(0, document.body.scrollHeight);}, 50);
    };

    const prevPage = () => {
        commentsPage--;
        commentsPromise = getComments(author.username, post.blog.url, post.url, commentsPage);
        setTimeout(() => {window.scrollTo(0, document.body.scrollHeight);}, 50);
    };

    const nextPage = () => {
        commentsPage++;
        commentsPromise = getComments(author.username, post.blog.url, post.url, commentsPage);
        setTimeout(() => {window.scrollTo(0, document.body.scrollHeight);}, 50);
    };

    const onCommentDelete = async (id: number) => {
        if (confirm("Are you sure you want to delete this comment?")) {
            await deleteComment(id);
            commentsPromise = getComments(author.username, post.blog.url, post.url, commentsPage);
            setTimeout(() => {window.scrollTo(0, document.body.scrollHeight);}, 50);
        }
    };

    const onCommentEdit = (id: number, content: string) => {
        editingComment = id;
        editingCommentContent = content;
    };

    const onCommentEditCancel = () => {
        editingComment = null;
        editingCommentContent = "";
    };

    const onCommentEditSave = async (id: number) => {
        await editComment(id, editingCommentContent);

        editingComment = null;
        editingCommentContent = "";

        commentsPromise = getComments(author.username, post.blog.url, post.url, commentsPage);
        setTimeout(() => {window.scrollTo(0, document.body.scrollHeight);}, 50);
    };

    const onReportPostClick = () => {
        reportPostVisible = true;
    };

    const onReportPostSubmit = async (event: CustomEvent) => {
        await reportPost(author.username, post.blog.url, post.url, event.detail.reason);
    };

    const onReportCommentClick = (id: number) => {
        reportCommentVisible = true;
        reportCommentId = id;
    };

    const onReportCommentSubmit = async (event: CustomEvent) => {
        await reportComment(reportCommentId, event.detail.reason);
    };
</script>

{#if found}
    <div class="title">
        <div class="row">
            <div class="title-upvotes">
                <div class="upvotes">
                    <a href="/" on:click|preventDefault={() => onUpvote()}>
                        ⇡
                    </a>

                    <div>
                        {post.upvotes}
                    </div>

                    <a href="/" on:click|preventDefault={() => onDownvote()}>
                        ⇣
                    </a>
                </div>

                <h2>{post.title}</h2>
            </div>

            {#if loggedIn}
                <div class="edit-links">
                    {#if isAuthor}
                        <a href="/~{author.username}/{post.blog.url}/{post.url}/edit" class="edit">edit</a>
                        <a href="/" on:click={onDelete} class="delete">delete</a>
                    {/if}
                    <a href="/" on:click|preventDefault={onReportPostClick}>report</a>
                </div>
            {/if}
        </div>

        <div class="meta">
            <p>Posted on: <span class="date">{date.toDateString()}</span> by <a href="/~{author.username}">{author.username}</a> in <a href="/~{author.username}/{post.blog.url}">{post.blog.name}</a></p>
            {#if editDate}
                <p>Edited on: <span class="date">{editDate.toDateString()}</span></p>
            {/if}
        </div>
    </div>

    <div class="content">
        {@html htmlContent}
    </div>

    <div class="comments">
        <h4>Comments</h4>

        {#if loggedIn}
            <div class="post-comment-top">
                <p>Post a comment</p>
                <button on:click={() => onPostComment()}>Submit</button>
            </div>
            <textarea bind:value={commentContent} placeholder="Comment..." rows="4"></textarea>
        {/if}

        {#if commentsPromise !== undefined}
            {#await commentsPromise then commentsPage}
                {#each commentsPage.content as comment}
                    <div class="comment" id="{comment.id.toString()}" class:target={comment.id === targetCommentId}>
                        {#if comment.id === editingComment}
                            <textarea cols="4" bind:value={editingCommentContent}></textarea>
                            <button on:click={() => onCommentEditCancel()}>Cancel</button>
                            <button on:click={() => onCommentEditSave(comment.id)}>Submit</button>
                        {:else}
                            <div class="meta">
                                #{comment.id} | Posted by <a href="/~{comment.author.username}">{comment.author.username}</a> on {new Date(comment.createdAt).toLocaleString()}

                                {#if comment.lastEdit}
                                    <br />Edited on {new Date(comment.lastEdit).toLocaleString()}
                                {/if}
                            </div>
                            <p>{comment.content}</p>

                            <div class="comment-edit">
                                {#if currentUser === comment.author.username}
                                    <a href="/" on:click|preventDefault={() => onCommentEdit(comment.id, comment.content)}>edit</a>
                                    <a href="/" on:click|preventDefault={() => onCommentDelete(comment.id)}>delete</a>
                                {/if}

                                {#if loggedIn}
                                    <a href="/" on:click|preventDefault={() => onReportCommentClick(comment.id)}>report</a>
                                {/if}
                            </div>
                        {/if}
                    </div>
                {/each}

                {#if commentsPage.content.length === 0}
                    <p>There's no comments here yet.</p>
                {/if}

                {#if commentsPage.totalPages > 1}
                    <div class="pager">
                        {#if !commentsPage.first}
                            <a href="/" on:click|preventDefault={() => prevPage()}>&laquo;</a>
                        {/if}

                        <span>page {commentsPage.number + 1}</span>

                        {#if !commentsPage.last}
                            <a href="/" on:click|preventDefault={() => nextPage()}>&raquo;</a>
                        {/if}
                    </div>
                {/if}
            {/await}
        {/if}
    </div>
{:else}
    <h2>Post not found</h2>
{/if}

{#if loggedIn}
    <Report bind:visible={reportPostVisible} on:report={onReportPostSubmit} />
    <Report bind:visible={reportCommentVisible} on:report={onReportCommentSubmit} />
{/if}

<style>
    h2 {
        border-bottom: none;
    }

    .title {
        border-bottom: 3px solid var(--nc-bg-2);
        margin-bottom: 1rem;
    }

    .title .row {
        display: flex;
        flex-direction: row;
        justify-content: space-between;
        align-items: center;
    }

    .title .row .edit {
        margin-right: 1rem;
    }

    .title .row .delete {
        color: var(--nc-red);
    }

    .title .row .edit-links a:last-child {
        margin-left: 1rem;
    }

    .meta {
        font-size: 0.9rem;
        padding-bottom: 1rem;
    }

    .meta p {
        margin: 0;
    }

    .title-upvotes {
        display: flex;
        flex-direction: row;
        align-items: center;
    }

    .title-upvotes .upvotes {
        margin-right: 1rem;
        text-align: center;
    }

    .title-upvotes h2 {
        margin: 0;
    }

    .title-upvotes .upvotes a {
        text-decoration: none;
    }

    .comments {
        border-top: 3px solid var(--nc-bg-2);
        padding-bottom: 1rem;
    }

    .comments h4 {
        margin-bottom: 1rem;
    }

    .comments textarea {
        width: 100%;
        margin-bottom: 1rem;
        resize: none;
        font-size: 1rem;
    }

    .comment {
        background: var(--nc-bg-2);
        border-radius: 4px;
        padding: 0.5rem 1rem;
        margin-top: 1rem;
    }

    .comment.target {
        border: 2px solid var(--nc-lk-1);
    }

    .post-comment-top {
        display: flex;
        flex-direction: row;
        justify-content: space-between;
        margin-bottom: 0.5rem;
    }

    .pager {
        text-align: center;
        margin-top: 0.5rem;
    }

    .comment-edit a {
        margin-right: 0.5rem;
    }
</style>
