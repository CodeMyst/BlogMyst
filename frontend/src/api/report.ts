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