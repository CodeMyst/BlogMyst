import type Post__SvelteComponent_ from "../pages/Post/Post.svelte";
import { API_BASE } from "./api";

export interface BlogCreateResult {
    success: boolean;
    message: string;
    url: string;
}

export interface PostCreateResult {
    success: boolean;
    message: string;
    url: string;
}

export interface Blog {
    url: string;
    name: string;
    description: string;
}

export interface Post {
    url: string;
    title: string;
    content: string;
    blog: Blog;
    createdAt: Date;
    lastEdit: Date;
    upvotes: number;
    downvotes: number;
}

export const createBlog = async (name: string, description: string): Promise<BlogCreateResult> => {
    const data = {
        name: name,
        description: description
    };

    const res = await fetch(`${API_BASE}/blog/create`, {
        method: "POST",
        mode: "cors",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include",
        body: JSON.stringify(data)
    });

    if (res.ok) {
        return {
            success: true,
            message: "",
            url: (await res.json()).url
        };
    } else {
        let msg: string;

        try {
            msg = (await res.json()).message;
        } catch (_) {
            msg = "Something went wrong. Try again."
        }

        return {
            success: false,
            message: msg,
            url: ""
        };
    }
};

export const getBlog = async (author: string, url: string): Promise<Blog | null> => {
    const res = await fetch(`${API_BASE}/blog/${author}/${url}`, {
        method: "GET",
        mode: "cors",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include"
    });

    if (!res.ok) return null;

    return await res.json();
};

export const getBlogs = async (author: string): Promise<Blog[]> => {
    const res = await fetch(`${API_BASE}/blog/${author}`, {
        method: "GET",
        mode: "cors",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include"
    });

    if (!res.ok) return null;

    return await res.json();
};

export const createPost = async (author: string, blogUrl: string, title: string, content: string): Promise<PostCreateResult> => {
    const data = {
        title: title,
        content: content
    };

    const res = await fetch(`${API_BASE}/blog/${author}/${blogUrl}`, {
        method: "POST",
        mode: "cors",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include",
        body: JSON.stringify(data)
    });

    if (res.ok) {
        return {
            success: true,
            message: "",
            url: (await res.json()).url
        };
    } else {
        let msg: string;

        try {
            msg = (await res.json()).message;
        } catch (_) {
            msg = "Something went wrong. Try again."
        }

        return {
            success: false,
            message: msg,
            url: ""
        };
    }
};

export const editPost = async (author: string, blogUrl: string, postUrl: string, title: string, content: string): Promise<PostCreateResult> => {
    const data = {
        title: title,
        content: content
    };

    const res = await fetch(`${API_BASE}/blog/${author}/${blogUrl}/${postUrl}/edit`, {
        method: "POST",
        mode: "cors",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include",
        body: JSON.stringify(data)
    });

    if (res.ok) {
        return {
            success: true,
            message: "",
            url: (await res.json()).url
        };
    } else {
        let msg: string;

        try {
            msg = (await res.json()).message;
        } catch (_) {
            msg = "Something went wrong. Try again."
        }

        return {
            success: false,
            message: msg,
            url: ""
        };
    }
};

export const getPost = async (author: string, blogUrl: string, postUrl: string): Promise<Post | null> => {
    const res = await fetch(`${API_BASE}/blog/~${author}/${blogUrl}/${postUrl}`, {
        method: "GET",
        mode: "cors",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include"
    });

    if (!res.ok) return null;

    return await res.json();
};
