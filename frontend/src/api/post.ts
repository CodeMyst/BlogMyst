import { API_BASE } from "./api";
import type { Blog } from "./blog";
import type { User } from "./user";

export interface Post {
    url: string;
    title: string;
    content: string;
    blog: Blog;
    createdAt: Date;
    lastEdit: Date;
    upvotes: number;
}

export interface Page<T> {
    content: T[];
    totalPages: number;
    number: number;
    first: boolean;
    last: boolean;
}

export interface Comment {
    id: number;
    createdAt: Date;
    lastEdit: Date;
    content: string;
    author: User;
}

export const getAllPosts = async (page: number): Promise<Page<Post>> => {
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

export const getFollowedPosts = async (page: number): Promise<Page<Post>> => {
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

export const postComment = async (author: string, blog: string, post: string, content: string): Promise<Comment> => {
    const res = await fetch(`${API_BASE}/post/${author}/${blog}/${post}/comment`, {
        method: "post",
        mode: "cors",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include",
        body: content
    });

    return await res.json();
};

export const getComments = async (author: string, blog: string, post: string, page: number): Promise<Page<Comment>> => {
    const res = await fetch(`${API_BASE}/post/${author}/${blog}/${post}/comments/${page}`, {
        method: "get",
        mode: "cors",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include"
    });

    return await res.json();
};

export const deleteComment = async (id: number) => {
    await fetch(`${API_BASE}/post/comment/${id}`, {
        method: "delete",
        mode: "cors",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include"
    });
};

export const editComment = async (id: number, content: string) => {
    await fetch(`${API_BASE}/post/comment/${id}`, {
        method: "PATCH",
        mode: "cors",
        headers: {
            "Content-Type": "text/plain"
        },
        credentials: "include",
        body: content
    });
};
