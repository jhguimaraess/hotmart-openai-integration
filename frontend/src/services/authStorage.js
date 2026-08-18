const AUTH_STORAGE_KEY = "auth";

export function saveAuth({ accessToken, tokenType, expiresIn }) {
  const expiresAt = Date.now() + expiresIn * 1000;

  const auth = {
    accessToken,
    tokenType,
    expiresAt,
  };

  localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(auth));

  return auth;
}

export function getAuth() {
  const storedAuth = localStorage.getItem(AUTH_STORAGE_KEY);

  if (!storedAuth) {
    return null;
  }

  try {
    return JSON.parse(storedAuth);
  } catch {
    localStorage.removeItem(AUTH_STORAGE_KEY);

    return null;
  }
}

export function removeAuth() {
  localStorage.removeItem(AUTH_STORAGE_KEY);
}
