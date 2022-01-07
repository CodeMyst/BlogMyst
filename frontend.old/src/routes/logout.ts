import type { EndpointOutput } from "@sveltejs/kit";

// eslint-disable-next-line @typescript-eslint/explicit-module-boundary-types
export const get = async (req): Promise<EndpointOutput> => {
    console.log("what");

    req.locals.user = null;
    req.cookies = null;

    return {
        status: 302,
        headers: {
            location: "/"
        }
    };
};