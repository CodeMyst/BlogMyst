import { API_BASE } from "./api";
import type { Blog } from "./blog";

export interface User {
    username: string;
}

export const getUser = async (username: string): Promise<User | null> => {
    const res = await fetch(`${API_BASE}/user/${username}`, {
        method: "GET",
        mode: "cors",
        credentials: "include",
        headers: {
            "Content-Type": "application/json"
        }
    });

    if (!res.ok) return null;

    return await res.json();
};

export const isFollowingBlog = async (author: string, blog: string): Promise<boolean> => {
    const res = await fetch(`${API_BASE}/user/follow/${author}/${blog}`, {
        method: "GET",
        mode: "cors",
        credentials: "include",
        headers: {
            "Content-Type": "application/json"
        }
    });

    return res.ok;
};

export const toggleFollowBlog = async (author: string, blog: string) => {
    await fetch(`${API_BASE}/user/follow/${author}/${blog}`, {
        method: "POST",
        mode: "cors",
        credentials: "include",
        headers: {
            "Content-Type": "application/json"
        }
    });
};

export const getFollowedBlogs = async (): Promise<Blog[]> => {
    const res = await fetch(`${API_BASE}/user/following`, {
        method: "GET",
        mode: "cors",
        credentials: "include",
        headers: {
            "Content-Type": "application/json"
        }
    });

    return await res.json();
};