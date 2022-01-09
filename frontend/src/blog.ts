import { API_BASE } from "./api";

export interface BlogCreateResult {
    success: boolean;
    message: string;
    url: string;
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