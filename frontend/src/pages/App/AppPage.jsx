import { useAuth } from "../../context/AuthContext";

function AppPage() {
  const { logout } = useAuth();

  return (
    <main>
      <h1>Authenticated area</h1>

      <p>You are authenticated.</p>

      <button type="button" onClick={logout}>
        Logout
      </button>
    </main>
  );
}

export default AppPage;
