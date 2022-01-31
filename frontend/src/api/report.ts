import { API_BASE } from "./api";

export const reportBlog = async (author: string, blog: string, reason: string) => {
    await fetch(`${API_BASE}/report/${author}/${blog}`, {
        mode: "cors",
        method: "post",
        credentials: "include",
        body: reason,
        headers: {
            "Content-Type": "application/json"
        }
    });
};

export const reportPost = async (author: string, blog: string, post: string, reason: string) => {
    await fetch(`${API_BASE}/report/${author}/${blog}/${post}`, {
        mode: "cors",
        method: "post",
        credentials: "include",
        body: reason,
        headers: {
            "Content-Type": "application/json"
        }
    });
};

export const reportComment = async (id: number, reason: string) => {
    await fetch(`${API_BASE}/report/${id}`, {
        mode: "cors",
        method: "post",
        credentials: "include",
        body: reason,
        headers: {
            "Content-Type": "application/json"
        }
    });
};