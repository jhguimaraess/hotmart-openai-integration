import { useEffect, useState } from "react";

import { useAuth } from "../../context/AuthContext";
import { getAuthenticatedUser } from "../../services/authService";

function AppPage() {
  const { logout } = useAuth();

  const [user, setUser] = useState(null);

  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    async function loadUser() {
      try {
        const authenticatedUser = await getAuthenticatedUser();

        setUser(authenticatedUser);
      } catch {
      } finally {
        setIsLoading(false);
      }
    }

    loadUser();
  }, []);

  if (isLoading) {
    return (
      <main>
        <p>Loading...</p>
      </main>
    );
  }

  return (
    <main>
      <h1>Authenticated area</h1>

      {user && (
        <>
          <p>User ID: {user.id}</p>

          <p>Email: {user.email}</p>
        </>
      )}

      <button type="button" onClick={logout}>
        Logout
      </button>
    </main>
  );
}

export default AppPage;
