import { API_BASE } from "./api";
import type { User } from "./user";

export interface Report {
    id: number;
    type: string;
    date: Date;
    reason: string;
    subjectId: string;
    user: User;
}

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

export const getReports = async () => {
    const res = await fetch(`${API_BASE}/report`, {
        mode: "cors",
        method: "get",
        credentials: "include",
        headers: {
            "Content-Type": "application/json"
        }
    });

    return await res.json();
};

export const resolveReport = async (id: number) => {
    await fetch(`${API_BASE}/report/${id}`, {
        mode: "cors",
        method: "delete",
        credentials: "include",
        headers: {
            "Content-Type": "application/json"
        }
    });
};
