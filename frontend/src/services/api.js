const API_URL = import.meta.env.VITE_API_URL ?? "http://localhost:8080";

export async function apiRequest(path, { body, headers, ...options } = {}) {
  const response = await fetch(`${API_URL}${path}`, {
    ...options,

    headers: {
      ...(body
        ? {
            "Content-Type": "application/json",
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
    const error = new Error(data?.message ?? "Something went wrong");

    error.status = response.status;

    error.data = data;

    throw error;
  }

  return data;
}
