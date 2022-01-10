import { API_BASE } from "./api";

export interface AuthResult {
    success: boolean;
    message: string;
}

export const register = async (username: string, password: string): Promise<AuthResult> => {
    const data = {
        username: username,
        password: password
    };

    const res = await fetch(`${API_BASE}/auth/register`, {
        method: "POST",
        mode: "cors",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    });

    const success = res.ok;

    let msg = "";
    if (!success) {
        msg = (await res.json()).message;
    }

    return {
        success: success,
        message: msg
    };
};

export const login = async (username: string, password: string): Promise<AuthResult> => {
    const data = {
        username: username,
        password: password
    };

    const res = await fetch(`${API_BASE}/auth/login`, {
        method: "POST",
        mode: "cors",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include",
        body: JSON.stringify(data)
    });

    const success = res.ok;

    let msg = "";

    if (!success) msg = "Wrong username/password combination.";

    return {
        success: success,
        message: msg
    };
};

export const logout = async () => {
    const res = await fetch(`${API_BASE}/auth/logout`, {
        method: "POST",
        mode: "cors",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include"
    });
};

export const isLoggedIn = async (): Promise<boolean> => {
    const res = await fetch(`${API_BASE}/auth/username`, {
        method: "GET",
        mode: "cors",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include"
    });

    return res.ok;
};

export const getUsername = async (): Promise<string> => {
    const res = await fetch(`${API_BASE}/auth/username`, {
        method: "GET",
        mode: "cors",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include"
    });

    return (await res.json()).username;
};
