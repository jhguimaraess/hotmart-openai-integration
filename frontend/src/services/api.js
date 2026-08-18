import { getAuth, removeAuth } from "./authStorage";

const API_URL = import.meta.env.VITE_API_URL ?? "http://localhost:8080";

export async function apiRequest(
  path,
  { body, headers, requiresAuth = true, ...options } = {},
) {
  const auth = requiresAuth ? getAuth() : null;

  const response = await fetch(`${API_URL}${path}`, {
    ...options,

    headers: {
      ...(body
        ? {
            "Content-Type": "application/json",
          }
        : {}),

      ...(auth?.accessToken
        ? {
            Authorization: `${auth.tokenType ?? "Bearer"} ${auth.accessToken}`,
          }
        : {}),

      ...headers,
    },

    body: body ? JSON.stringify(body) : undefined,
  });

  let data = null;

  if (response.status !== 204) {
    const responseText = await response.text();

    if (responseText) {
      try {
        data = JSON.parse(responseText);
      } catch {
        data = responseText;
      }
    }
  }

  if (!response.ok) {
    if (response.status === 401 && requiresAuth) {
      removeAuth();

      window.dispatchEvent(new Event("auth:unauthorized"));
    }

    const error = new Error(data?.message ?? "Something went wrong");

    error.status = response.status;

    error.data = data;

    throw error;
  }

  return data;
}
