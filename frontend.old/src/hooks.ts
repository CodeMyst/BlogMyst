import type { GetSession } from "@sveltejs/kit";
import type { Handle, ServerRequest, ServerResponse } from "@sveltejs/kit/types/hooks";
import cookie from "cookie";

export const handle: Handle = async ({ request, resolve }): Promise<ServerResponse> => {
    const cookies = cookie.parse(request.headers.cookie || '');

    if (cookies.blogmyst) {
        const res = await fetch("http://localhost:8080/username", {
            method: "GET",
            headers: {
                "Authorization": cookies.blogmyst
            }
        });

        if (res.ok) {
            const payload = await res.text();
            const regex = /sub=(\w+)/gm;
            const username = regex.exec(payload)[1];
            request.locals.user = username;
        }
    }

    const response = await resolve(request);

    return response;
};

export const getSession: GetSession = async (request: ServerRequest<Record<string, unknown>, unknown>): Promise<unknown> => {
    return request.locals.user ? {
        user: request.locals.user
    } : {};
};