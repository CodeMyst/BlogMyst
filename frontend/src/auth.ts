import cookie from "cookie";

const API_BASE = "http://localhost:8080";

export interface AuthResult {
    success: boolean;
    message: string;
}

export const register = async (username: string, password: string): Promise<AuthResult> => {
    const data = {
        username: username,
        password: password
    };

    const res = await fetch(`${API_BASE}/register`, {
        method: "POST",
        mode: "cors",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    });

    const success = res.ok;
    const usernameTaken = res.status === 400;

    let msg = "";

    if (!success) {
        if (usernameTaken) msg = "Username is already taken.";
        else msg = "There's an issue with the backend. Try again.";
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

    const res = await fetch(`${API_BASE}/login`, {
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

export const isLoggedIn = async (): Promise<boolean> => {
    return false;
};
