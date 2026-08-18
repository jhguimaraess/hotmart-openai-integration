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
    const auth = JSON.parse(storedAuth);

    if (!auth.accessToken || !auth.expiresAt || auth.expiresAt <= Date.now()) {
      removeAuth();
      return null;
    }

    return auth;
  } catch {
    removeAuth();
    return null;
  }
}

export function removeAuth() {
  localStorage.removeItem(AUTH_STORAGE_KEY);
}
