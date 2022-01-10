import { API_BASE } from "./api";

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