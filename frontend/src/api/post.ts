import { API_BASE } from "./api";
import type { Blog } from "./blog";

export interface Post {
    url: string;
    title: string;
    content: string;
    blog: Blog;
    createdAt: Date;
    lastEdit: Date;
    upvotes: number;
}

export interface PostPage {
    content: Post[];
    totalPages: number;
    number: number;
    first: boolean;
    last: boolean;
}

export const getAllPosts = async (page: number): Promise<PostPage> => {
    const res = await fetch(`${API_BASE}/post/all/${page}`, {
        method: "get",
        mode: "cors",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include"
    });

    return await res.json();
};

export const getFollowedPosts = async (page: number): Promise<PostPage> => {
    const res = await fetch(`${API_BASE}/post/followed/${page}`, {
        method: "get",
        mode: "cors",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include"
    });

    return await res.json();
};

export const upvote = async (author: string, blog: string, post: string) => {
    await fetch(`${API_BASE}/post/${author}/${blog}/${post}/upvote`, {
        method: "post",
        mode: "cors",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include"
    });
};

export const downvote = async (author: string, blog: string, post: string) => {
    await fetch(`${API_BASE}/post/${author}/${blog}/${post}/downvote`, {
        method: "post",
        mode: "cors",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include"
    });
};
